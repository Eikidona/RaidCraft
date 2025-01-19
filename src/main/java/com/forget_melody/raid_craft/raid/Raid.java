package com.forget_melody.raid_craft.raid;

import com.forget_melody.raid_craft.IRaidType;
import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.capabilities.raider.api.RaiderHelper;
import com.forget_melody.raid_craft.registries.RaidTypes;
import com.forget_melody.raid_craft.utils.weight_table.WeightTable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Raid {
	public static final int RAID_REMOVAL_THRESHOLD_SQR = 12544;
	private int id;
	private ServerLevel level;
	private BlockPos center;
	private IRaidType raidType;
	private ServerBossEvent bossEvent;
	/**
	 * Raider相关
	 */
	private Map<Integer, Mob> leaderRaider;
	private Map<Integer, HashSet<Mob>> raiders = new HashMap<>();
	private int waveSpawned = 0;
	private int waveTotal = 3;
	private WeightTable<IRaidType> raidTypeWeightTable;
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
	
	public Raid(int id, ServerLevel level, BlockPos blockPos, IRaidType raidType) {
		this.id = id;
		this.level = level;
		this.center = blockPos;
		this.raidType = raidType;
		this.bossEvent = new ServerBossEvent(raidType.getRaidDisplay(), raidType.getColor(), raidType.getOverlay());
		// 设置可见性
		this.bossEvent.setVisible(true);
		this.bossEvent.setProgress(0.0F);
		
	}
	
	public Raid(ServerLevel level, CompoundTag tag) {
		this.level = level;
		this.id = tag.getInt("Id");
		this.center = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
		this.raidType = RaidTypes.RAID_TYPE_REGISTRY.get().getValue(new ResourceLocation(tag.getString("RaidType")));
		
		this.bossEvent = new ServerBossEvent(this.raidType.getRaidDisplay(), this.raidType.getColor(), this.raidType.getOverlay());
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
				
				/*
				  @todo {也许应该使用Goal}
				 */

//				// 导航实体
//				else {
//					if (mob.isAlive()) {
//						if (mob.getNavigation().isDone()) {
//							mob.getNavigation().moveTo(this.center.getX(), this.center.getY(), this.center.getZ(), 1.1d);
//						}
//					}
//				}
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
		int strength = 0;
		if (difficulty == Difficulty.EASY) {
			strength += 10;
		} else if (difficulty == Difficulty.NORMAL) {
			strength += 20;
			this.waveTotal += 2;
		} else {
			strength += 40;
			this.waveTotal += 4;
		}
		strength += this.level.getCurrentDifficultyAt(this.getCenter()).getEffectiveDifficulty() * 10;
		this.strength = strength;
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
		this.bossEvent.setName(this.raidType.getWinDisplay());
		this.bossEvent.setProgress(0.0F);
		this.win = true;
	}
	
	public void defeated() {
		this.bossEvent.setName(this.raidType.getLoseDisplay());
		this.bossEvent.setProgress(0.0F);
		this.lose = true;
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
			RaidCraft.LOGGER.info("Raid 初始化 %s $b".formatted(this.raidCooldown).formatted(this.started));
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
					RaidCraft.LOGGER.info("Raid numAliveRaiders == 0");
					// 还有更多波次
					if (hasMoreWave()) {
						RaidCraft.LOGGER.info("Raid hasMoreWave");
						
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
						bossEvent.setName(raidType.getRaidDisplay());
					}
					// 没更多波次
					else {
						bossEvent.setName(raidType.getWinDisplay());
						RaidCraft.LOGGER.info("Raid victory");
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
						bossEvent.setName(raidType.getRaidDisplay().copy().append("-").append(Component.translatable("event.minecraft.raid.raiders_remaining", numAliveRaiders)));
					} else {
						bossEvent.setName(raidType.getRaidDisplay());
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
		RaidCraft.LOGGER.info("[Raid] RaidCooldown %d".formatted(this.raidCooldown));
		activeTicks++;
	}
	
	private boolean hasMoreWave() {
		return waveSpawned < waveTotal;
	}
	
	private void spawnWave() {
		RaidCraft.LOGGER.info("Raid spawnWave 强度: %d".formatted(this.strength));
		
		// 状态更新
		totalHealth = 0;
		++waveSpawned;
		// 累计强度
		int cumulativeStrength = 0;
		// 位置
		BlockPos pos = null;
		for (int i = 1; i < 2 && pos == null; i++) {
			pos = findRandomPos(i, 20);
			
		}
		// 生成
		while (cumulativeStrength < strength) {
			if (pos != null) {
				RaiderType raiderType = raidType.getRaiderTypes().getEntry().get();
				if (raiderType != null) {
					if (raiderType.getEntityType().isPresent()) {
						Entity entity = raiderType.getEntityType().get().spawn(level, pos, MobSpawnType.EVENT);
						if (entity instanceof Mob) {
							joinRaid((Mob) entity);
						}
						cumulativeStrength += raiderType.getStrength();
					}
				} else {
					cumulativeStrength = strength;
					RaidCraft.LOGGER.warn("RaiderType的RaiderTypes是空列表");
				}
			}
		}
	}
	
	@Nullable
	private BlockPos findRandomPos(int mutiple, int tryCount) {
		int mutiple_ = mutiple == 0 ? 2 : 2 - mutiple;
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
		tag.putString("RaidType", this.raidType.getId().toString());
		
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
	
	public int getId() {
		return id;
	}
	
	public ServerLevel getLevel() {
		return level;
	}
	
	public BlockPos getCenter() {
		return center;
	}
	
	public IRaidType getRaidType() {
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
}
