package com.forget_melody.raid_craft.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.api.event.raid.RaidComputeStrengthEvent;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.capabilities.raider.RaiderHelper;
import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
import com.forget_melody.raid_craft.utils.weight_table.WeightEntry;
import com.forget_melody.raid_craft.utils.weight_table.WeightTable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class Raid {
	public static final int RAID_REMOVAL_THRESHOLD_SQR = 12544;
	private final int id;
	private final ServerLevel level;
	private BlockPos center;
	private final RaidType raidType;
	private final ServerBossEvent bossEvent;
	private final Faction faction;
	private ArrayList<WeightTable<RaiderType>> raiderTypeTables;
	/**
	 * Raider相关
	 */
	private final Map<Integer, Mob> leaderRaider = new HashMap<>();
	private final Map<Integer, HashSet<Mob>> raiders = new HashMap<>();
	private int waveSpawned = 0;
	private int waveTotal = 3;
	private final WeightTable<ResourceLocation> raiderTable;
	private final WeightTable<ResourceLocation> raiderTypeTable;
	/**
	 * 数值状态
	 */
	private float totalHealth = 0;
	private float health = 0;
	private int raidCooldown = 300;
	private int activeTicks = 0;
	private int strength = 0;
	private int celebrateTicks = 600;
	/**
	 * 一些阶段标记
	 */
	private boolean started = false;
	private boolean win = false;
	private boolean lose = false;
	private boolean stopped = false;
	
	public Raid(int id, ServerLevel level, BlockPos blockPos, RaidType raidType) {
		this.id = id;
		this.level = level;
		this.center = blockPos;
		
		this.faction = raidType.getFaction();
		
		
		this.raidType = raidType;
		this.raiderTable = WeightTable.of(raidType.getRaiders().stream().map(raiderEntry -> WeightEntry.of(raiderEntry.entityType(), raiderEntry.weight())).toList());
		this.raiderTypeTable = WeightTable.of(raidType.getRaiderTypes().stream().map(raiderEntry -> WeightEntry.of(raiderEntry.raiderType(), raiderEntry.weight())).toList());
		this.bossEvent = new ServerBossEvent(raidType.getNameComponent(), raidType.getColor(), raidType.getOverlay());
		// 设置可见性
		this.bossEvent.setVisible(true);
		this.bossEvent.setProgress(0.0F);
		
	}
	
	public Raid(ServerLevel level, CompoundTag tag) {
		this.level = level;
		this.id = tag.getInt("Id");
		this.center = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
		this.faction = DatapackRegistries.FACTIONS.getValue(new ResourceLocation(tag.getString("Faction")));
		this.raidType = DatapackRegistries.RAID_TYPES.getValue(new ResourceLocation(tag.getString("RaidType")));
		
		this.raiderTable = WeightTable.of(raidType.getRaiders().stream().filter(raiderEntry -> ForgeRegistries.ENTITY_TYPES.containsKey(raiderEntry.entityType())).map(raiderEntry -> WeightEntry.of(raiderEntry.entityType(), raiderEntry.weight())).toList());
		this.raiderTypeTable = WeightTable.of(raidType.getRaiderTypes().stream().filter(raiderEntry -> DatapackRegistries.RAIDER_TYPES.containsKey(raiderEntry.raiderType())).map(raiderEntry -> WeightEntry.of(raiderEntry.raiderType(), raiderEntry.weight())).toList());
		
		this.bossEvent = new ServerBossEvent(this.raidType.getNameComponent(), this.raidType.getColor(), this.raidType.getOverlay());
		// 设置可见性
		this.bossEvent.setVisible(true);
		this.bossEvent.setProgress(0.0F);
		
		this.waveSpawned = tag.getInt("WaveSpawned");
		this.waveTotal = tag.getInt("WaveTotal");
		this.totalHealth = tag.getInt("TotalHealth");
		this.health = tag.getInt("Health");
		this.raidCooldown = tag.getInt("RaidCooldown");
		this.activeTicks = tag.getInt("ActiveTicks");
		this.strength = tag.getInt("Strength");
		this.celebrateTicks = tag.getInt("CelebrateTicks");
		
		this.started = tag.getBoolean("Started");
		this.win = tag.getBoolean("Win");
		this.lose = tag.getBoolean("Lose");
		this.stopped = tag.getBoolean("Stopped");

//		this.level.getEntitiesOfClass(Mob.class, new AABB(this.center.getX() - 56, this.center.getY() - 56, this.center.getZ() - 56, this.center.getX() + 56, this.center.getY() + 56, this.center.getZ() + 56), mob -> {
//					IRaider raider = mob.getCapability(Capabilities.RAIDER).orElse(Raider.EMPTY);
//					return raider.getRaid() != null && raider.getRaid().getId() == this.getId();
//				}
//		).forEach(this::joinRaid);
	}
	
	/**
	 * 使实体加入袭击
	 *
	 * @param mob
	 */
	public void joinRaid(Mob mob) {
		if (mob.getCapability(Capabilities.RAIDER).isPresent()) {
			Optional<IRaider> optional = RaiderHelper.getRaider(mob);
			optional.ifPresent(raider -> {
				addWaveMob(waveSpawned, mob);
				this.totalHealth += mob.getMaxHealth();
				raider.setRaid(this);
				raider.setWave(waveSpawned);
			});
			
		}
	}
	
	public void removeRaid(Mob mob, boolean outOfRaid) {
		Optional<IRaider> optional = RaiderHelper.getRaider(mob);
		optional.ifPresent(raider -> {
			removeWaveRaid(raider.getWave(), mob, outOfRaid);
		});
		
	}
	
	public void addWaveMob(int wave, Mob mob) {
		HashSet<Mob> mobs = this.raiders.computeIfAbsent(wave, integer -> new HashSet<>());
		mobs.add(mob);
	}
	
	public void removeWaveRaid(int wave, Mob mob, boolean outOfRaid) {
		this.raiders.get(wave).remove(mob);
		Optional<IRaider> optional = RaiderHelper.getRaider(mob);
		optional.ifPresent(raider -> {
			raider.setRaid(null);
			raider.setWave(null);
			if (outOfRaid) {
				this.totalHealth -= raider.get().getMaxHealth();
			}
		});
		
	}
	
	/**
	 * 更新Player
	 */
	private void updatePlayer() {
		this.level.players().forEach(player -> {
			// remove
			if (this.bossEvent.getPlayers().contains(player)) {
				if (player.blockPosition().distSqr(this.center) >= RAID_REMOVAL_THRESHOLD_SQR) {
					this.bossEvent.removePlayer(player);
				}
			}
			// add
			else {
				if (player.blockPosition().distSqr(this.center) < RAID_REMOVAL_THRESHOLD_SQR) {
					this.bossEvent.addPlayer(player);
				}
			}
		});
	}
	
	private void updateRaider() {
		Set<Mob> set = new HashSet<>();
		Iterator<HashSet<Mob>> mobSets = this.raiders.values().iterator();
		while (mobSets.hasNext()) {
			Iterator<Mob> mobs = mobSets.next().iterator();
			while (mobs.hasNext()) {
				Mob mob = mobs.next();
				// 移除超出范围
				if (mob.blockPosition().distSqr(this.center) >= RAID_REMOVAL_THRESHOLD_SQR) {
					set.add(mob);
				}
			}
		}
		Iterator<Mob> removeMobs = set.iterator();
		while (removeMobs.hasNext()) {
			Mob mob = removeMobs.next();
			removeWaveRaid(waveSpawned, mob, true);
		}
		
	}
	
	private void updateBossBar() {
		this.bossEvent.setProgress(Mth.clamp(this.getHealthOfAliveRaiders() / this.totalHealth, 0.0F, 1.0F));
	}
	
	private float getHealthOfAliveRaiders() {
		float health = 0;
		Iterator<Mob> mobs = getRaidersOfAlive().iterator();
		while (mobs.hasNext()) {
			health += mobs.next().getHealth();
		}
		this.health = health;
		return health;
	}
	
	private void computeStrength() {
		Difficulty difficulty = this.level.getDifficulty();
		int strength = raidType.getStrength();
		if (difficulty == Difficulty.EASY) {
			strength += 10;
		} else if (difficulty == Difficulty.NORMAL) {
			strength += 20;
			this.waveTotal += 2;
		} else {
			strength += 40;
			this.waveTotal += 4;
		}
		strength += (int) (this.level.getCurrentDifficultyAt(this.getCenter()).getEffectiveDifficulty() * 10);
		this.strength = strength;
		MinecraftForge.EVENT_BUS.post(new RaidComputeStrengthEvent(getLevel(), this));
	}
	
	private void start() {
		if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
			this.stopped = true;
		} else {
			this.started = true;
			this.computeStrength();
		}
	}
	
	public void victory() {
		this.bossEvent.setName(this.raidType.getVictoryComponent());
		this.bossEvent.setProgress(0.0F);
		this.win = true;
		this.playSound(center, raidType.getVictorySoundEvent());
	}
	
	public void defeated() {
		this.bossEvent.setName(this.raidType.getDefeatComponent());
		this.bossEvent.setProgress(0.0F);
		this.lose = true;
		this.playSound(center, raidType.getDefeatSoundEvent());
	}
	
	public void stop() {
		this.stopped = true;
		this.bossEvent.setVisible(false);
	}
	
	public boolean isOver() {
		return this.isVictory() || this.isDefeat();
	}
	
	public boolean isVictory() {
		return this.win;
	}
	
	public boolean isDefeat() {
		return this.lose;
	}
	
	public boolean isStop() {
		return this.stopped;
	}
	
	public boolean isActive() {
		return isStarted() && !isOver();
	}
	
	public Set<Mob> getRaidersOfAlive() {
		Set<Mob> aliveRaiders = new HashSet<>();
		Iterator<HashSet<Mob>> raiderSets = this.raiders.values().iterator();
		while (raiderSets.hasNext()) {
			Iterator<Mob> raiders = raiderSets.next().iterator();
			
			while (raiders.hasNext()) {
				Mob mob = raiders.next();
				if (mob.isAlive()) {
					aliveRaiders.add(mob);
				}
			}
		}
		return aliveRaiders;
	}
	
	public void tick() {
		if (this.isStop()) return;
		
		if (level.getDifficulty() == Difficulty.PEACEFUL) {
			this.stop();
			return;
		}
		// 初始化
		if (!this.isStarted()) {
			// 冷却时间
			if (this.raidCooldown > 0) {
				--this.raidCooldown;
				bossEvent.setProgress(Mth.clamp((float) (300 - raidCooldown) / 300.0F, 0.0F, 1.0F));
			}
			// 启动
			else {
				this.raidCooldown = 300;
				this.start();
			}
		}
		// 已经开始
		else {
			// 未分出胜负
			if (!isOver()) {
				// ActiveTicks
				if (activeTicks > 72000) {
					this.stop();
					return;
				}
				// 生成尝试
				Set<Mob> raidersOfAlive = this.getRaidersOfAlive();
				int numAliveRaiders = raidersOfAlive.size();
				// 存活数==0 && 还有更多波次
				if (numAliveRaiders == 0) {
					// 还有更多波次
					if (hasMoreWave()) {
						// 冷却时间==0
						if (this.raidCooldown == 0) {
							this.spawnWave();
							raidCooldown = 300;
						}
						// 冷却 != 0
						else {
							// 第一波 || 其他波
							if (waveSpawned == 0) {
								spawnWave();
							} else {
								raidCooldown--;
								bossEvent.setProgress(Mth.clamp((float) (300 - raidCooldown) / 300.0F, 0.0F, 1.0F));
							}
						}
						// 显示组件
						bossEvent.setName(raidType.getNameComponent());
					}
					// 没更多波次
					else {
						bossEvent.setName(raidType.getVictoryComponent());
						victory();
					}
					
				}
				// 存活数 > 0
				else {
					// 大于0小于等于3
					if (numAliveRaiders <= 3) {
						if (this.activeTicks % 120 == 0) {
							raidersOfAlive.forEach(raider -> raider.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60)));
						}
						bossEvent.setName(raidType.getNameComponent().copy().append("-").append(Component.translatable("event.minecraft.raid.raiders_remaining", numAliveRaiders)));
					} else {
						bossEvent.setName(raidType.getNameComponent());
					}
					// 检查玩家存活
					if (bossEvent.getPlayers().isEmpty()) {
						if (this.raidCooldown == 0) {
							defeated();
						} else {
							this.raidCooldown = 300;
						}
					} else {
						this.raidCooldown = 300;
					}
					
					// 更新BossBar
					updateBossBar();
				}
				if (activeTicks % 20 == 0) {
					updateRaider();
				}
			}
			// 分出胜负
			else {
				// 庆祝时间
				if (celebrateTicks == 0) {
					this.stop();
				} else {
					celebrateTicks--;
				}
			}
		}
		if (activeTicks % 20 == 0) {
			updatePlayer();
		}
		activeTicks++;
	}
	
	private boolean hasMoreWave() {
		return waveSpawned < waveTotal;
	}
	
	private void spawnWave() {
		// 状态更新
		totalHealth = 0;
		++waveSpawned;
		// 每个参与Raid的faction都具有一个位置
		BlockPos pos = null;
		for (int i = 1; i < 2; i++) {
			pos = findRandomPos(i, 20);
		}
		// 生成
		if (pos != null) {
			int cumulativeStrength = 0;
			while (cumulativeStrength < strength) {
				EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(raiderTable.getElement());
				RaiderType raiderType = DatapackRegistries.RAIDER_TYPES.getValue(raiderTypeTable.getElement());
				Entity entity = entityType.spawn(level, pos, MobSpawnType.EVENT);
				
				if (entityType == null) break;
				if (entity instanceof Mob) {
					joinRaid((Mob) entity);
					Optional<IRaider> optional = RaiderHelper.getRaider((Mob) entity);
					if (raiderType != null) {
						optional.ifPresent(raider -> raider.setRaiderType(raiderType));
						cumulativeStrength += (2 + raiderType.getStrength());
					} else {
						cumulativeStrength += 2;
					}
				}
			}
			
		}
		
		playSound(center, raidType.getWaveSoundEvent());
	}
	
	private BlockPos findRandomPos(int multiple, int tryCount) {
		int mutiple_ = multiple == 0 ? 2 : 2 - multiple;
		for (int i = 0; i < tryCount; i++) {
			BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
			float f = level.random.nextFloat() * ((float) Math.PI * 2);
			int x = (int) (this.center.getX() + Math.floor(Math.cos(f) * 32.0F * (float) mutiple_) + this.level.random.nextInt(5));
			int z = (int) (this.center.getZ() + Math.floor(Math.sin(f) * 32.0F * (float) mutiple_) + this.level.random.nextInt(5));
			int y = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
			blockPos.set(x, y, z);
			if (level.hasChunksAt(x - 10, z - 10, x + 10, z + 10) && level.isPositionEntityTicking(blockPos)) {
				return blockPos;
			}
		}
		return null;
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Id", this.id);
		tag.putInt("X", this.center.getX());
		tag.putInt("Y", this.center.getY());
		tag.putInt("Z", this.center.getZ());
		tag.putString("Faction", DatapackRegistries.FACTIONS.getKey(this.faction).toString());
		tag.putString("RaidType", DatapackRegistries.RAID_TYPES.getKey(this.raidType).toString());
		
		tag.putInt("WaveSpawned", this.waveSpawned);
		tag.putInt("WaveTotal", this.waveTotal);
		tag.putFloat("TotalHealth", this.totalHealth);
		tag.putFloat("Health", this.health);
		tag.putInt("RaidCooldown", this.raidCooldown);
		tag.putInt("ActiveTicks", this.activeTicks);
		tag.putInt("Strength", this.strength);
		tag.putInt("CelebrateTicks", this.celebrateTicks);
		
		tag.putBoolean("Started", this.started);
		tag.putBoolean("Win", this.win);
		tag.putBoolean("Lose", this.lose);
		tag.putBoolean("Stopped", this.stopped);
		
		return tag;
	}
	
	private void playSound(BlockPos pPos, Holder<SoundEvent> soundEvent) {
		float pitch = 1.0F;
		float volume = 64.0F;
		long seed = level.random.nextLong();
		
		Collection<ServerPlayer> collection = bossEvent.getPlayers();
		for (ServerPlayer serverplayer : level.players()) {
			Vec3 vec3 = serverplayer.position();
			Vec3 vec31 = Vec3.atCenterOf(pPos);
			double d0 = Math.sqrt((vec31.x - vec3.x) * (vec31.x - vec3.x) + (vec31.z - vec3.z) * (vec31.z - vec3.z)); // 距离
			double d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x); // 偏移
			double d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z); // 偏移
			if (d0 <= 64.0D || collection.contains(serverplayer)) {
				serverplayer.connection.send(new ClientboundSoundPacket(soundEvent, SoundSource.NEUTRAL, d1, serverplayer.getY(), d2, volume, pitch, seed));
			}
		}
		
	}
	
	public int getId() {
		return id;
	}
	
	public ServerLevel getLevel() {
		return level;
	}
	
	public BlockPos getCenter() {
		return center;
	}
	
	public RaidType getRaidType() {
		return raidType;
	}
	
	public ServerBossEvent getBossEvent() {
		return bossEvent;
	}
	
	public Mob getLeaderRaider() {
		return leaderRaider.get(waveSpawned);
	}
	
	public HashSet<Mob> getWaveRaiders() {
		return raiders.get(waveSpawned);
	}
	
	public int getWaveSpawned() {
		return waveSpawned;
	}
	
	public int getWaveTotal() {
		return waveTotal;
	}
	
	public float getTotalHealth() {
		return totalHealth;
	}
	
	public float getHealth() {
		return health;
	}
	
	public int getRaidCooldown() {
		return raidCooldown;
	}
	
	public int getActiveTicks() {
		return activeTicks;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isWin() {
		return win;
	}
	
	public boolean isLose() {
		return lose;
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public int getCelebrateTicks() {
		return celebrateTicks;
	}
	
	public void setStrength(int strength) {
		this.strength = strength;
	}
}
