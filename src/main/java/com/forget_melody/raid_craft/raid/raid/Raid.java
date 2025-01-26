package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raid_interaction.IRaidInteraction;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.event.raid.RaidComputeStrengthEvent;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Raid {
	public static final int RAID_REMOVAL_THRESHOLD_SQR = 12544;
	private final Map<ResourceLocation, Integer> factionEntityTypeCountMap = new HashMap<>();
	private final ServerLevel level;
	// Data Start
	private final int id;
	private float totalHealth = 0;
	private float health = 0;
	private int celebrateTicks = 600;
	private int raidCooldownTicks = 300;
	private int activeTicks = 0;
	private boolean started = false;
	private boolean victory = false;
	private boolean defeat = false;
	private boolean stopped = false;
	// Data End
	private final RaidConfig raidConfig;
	private final ServerBossEvent bossBar;
	private final Faction faction;
	private final List<RaiderType> raiderTypes;
	private BlockPos center;
	private IRaider leaderRaider;
	private final Set<IRaider> raiders = new HashSet<>();
	private final Set<IRaider> aliveRaiders = new HashSet<>();
	private int spawnedWave = 0;
	private int totalWave = 0;
	private int strength = 0;
	private int badOmenLevel = 0;
	
	public Raid(ServerLevel level, int id, Faction faction, BlockPos center) {
		this.level = level;
		this.id = id;
		this.faction = faction;
		this.raidConfig = faction.getRaidConfig();
		this.bossBar = new ServerBossEvent(this.raidConfig.getNameComponent(), this.raidConfig.getColor(), this.raidConfig.getOverlay());
		this.bossBar.setVisible(true);
		this.bossBar.setProgress(0.0F);
		this.raiderTypes = faction.getRaidConfig().getRaiderTypes();
		this.center = center;
	}
	
	public Raid(ServerLevel level, CompoundTag tag) {
		this.level = level;
		// Data
		this.id = tag.getInt("Id");
		this.totalHealth = tag.getInt("TotalHealth");
		this.health = tag.getInt("Health");
		this.raidCooldownTicks = tag.getInt("RaidCooldown");
		this.activeTicks = tag.getInt("ActiveTicks");
		this.started = tag.getBoolean("Started");
		this.victory = tag.getBoolean("Victory");
		this.defeat = tag.getBoolean("Defeat");
		this.stopped = tag.getBoolean("Stopped");
		this.celebrateTicks = tag.getInt("CelebrateTicks");
		//
		this.faction = DataPackRegistries.FACTIONS.getValue(new ResourceLocation(tag.getString("Faction")));
		if (faction == null) {
			RaidCraft.LOGGER.error("Not found faction id {}", tag.getString("Faction"));
			throw new NullPointerException();
		}
		this.raidConfig = faction.getRaidConfig();
		this.bossBar = new ServerBossEvent(this.raidConfig.getNameComponent(), this.raidConfig.getColor(), this.raidConfig.getOverlay());
		this.bossBar.setVisible(true);
		this.raiderTypes = faction.getRaidConfig().getRaiderTypes();
		this.center = NbtUtils.readBlockPos(tag.getCompound("Center"));
		this.badOmenLevel = tag.getInt("BadOmenLevel");
	}
	
	/**
	 * 更新中心位置
	 */
	protected void updateCenter() {
	
	}
	
	public void setCenter(BlockPos blockPos) {
		center = blockPos;
	}
	
	/**
	 * 吸收不祥之兆Buff 启动阶段 更新玩家后调用
	 */
	protected void absorbBadOmen() {
		int badOmenLevel = 0;
		for (ServerPlayer player : getPlayers()) {
			Optional<IRaidInteraction> optional = IRaidInteraction.get(player);
			if (optional.isEmpty()) {
				RaidCraft.LOGGER.error("IRaidInteraction is null by player {}, uuid: {}", player.getName(), player.getUUID());
				return;
			}
			IRaidInteraction raidInteraction = optional.get();
			badOmenLevel += raidInteraction.getBadOmenLevel();
			raidInteraction.clearBadOmen();
			if (badOmenLevel >= 4) {
				setBadOmenLevel(4);
				break;
			}
		}
		setBadOmenLevel(badOmenLevel);
	}
	
	/**
	 * 获取袭击中的玩家，这是由{@link #updatePlayer()}更新获取的
	 *
	 * @return BossBar中的玩家
	 */
	public Collection<ServerPlayer> getPlayers() {
		return this.bossBar.getPlayers();
	}
	
	protected void spawnWave() {
		spawnedWave++;
		// 寻觅位置
		BlockPos pos = findRandomPos(2, 50);
//		for (int i = 1; i <= 2; i++) {
//			pos = findRandomPos(i, 20);
//			if(pos != null){
//				break;
//			}
//		}
		if (pos == null) {
			RaidCraft.LOGGER.error("Failed to find a valid position.");
			return;
		}
		// 生成一个组
		spawnGroup(strength, pos);
		// 设置一个Leader
		for (IRaider raider : raiders) {
			setLeaderRaider(raider);
			equipBanner(raider);
			break;
		}
		// 播放声音
		if (raidConfig.getWaveSoundEvent() != null) {
			playSound(center, raidConfig.getWaveSoundEvent());
		} else {
			RaidCraft.LOGGER.error("Wave sound event is null. No sound will be played.");
		}
	}
	
	/**
	 * 生成一组生物
	 *
	 * @param targetStrength 这组的目标强度
	 * @param pos            位置
	 */
	protected void spawnGroup(int targetStrength, BlockPos pos) {
		
		// 必须生成的 不累计强度值
		List<RaiderType> mustSpawnRaiderTypeList = getMustSpawnRaiderTypeList();
		
		if (!mustSpawnRaiderTypeList.isEmpty()) {
			for (RaiderType raiderType : mustSpawnRaiderTypeList) {
				int count = 0;
				while (count < raiderType.getMinSpawned()) {
					BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.offset(this.level.getRandom().nextInt(11) - 5, 0, this.level.getRandom().nextInt(11) - 5));
					IRaider raider = raiderType.spawn(level, spawnPos);
					if (raider != null) {
						joinRaid(raider);
					}
					count++;
				}
			}
		}
		
		List<RaiderType> spawnableRaiderTypes = getSpawnableRaiderTypeList();
		
		if (spawnableRaiderTypes.isEmpty()) {
			RaidCraft.LOGGER.error("No faction entity types available for this wave.");
			return;
		}
		
		int totalWeight = spawnableRaiderTypes.stream().mapToInt(RaiderType::getWeight).sum();
		int currentStrength = 0;
		while (currentStrength < targetStrength) {
			int randomWeight = this.level.getRandom().nextInt(totalWeight);
			int cumulativeWeight = 0;
			for (RaiderType raiderType : spawnableRaiderTypes) {
				cumulativeWeight += raiderType.getWeight();
				if (cumulativeWeight > randomWeight) {
					BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.offset(this.level.getRandom().nextInt(11) - 5, 0, this.level.getRandom().nextInt(11) - 5));
					IRaider raider = raiderType.spawn(level, spawnPos);
					if (raider == null) {
						RaidCraft.LOGGER.error("Failed to spawn entity: {}", raiderType);
						continue;
					}
					currentStrength += raiderType.getStrength();
					joinRaid(raider);
					break;
				}
			}
		}
	}
	
	/**
	 * 本轮次必须生成的RaiderType
	 *
	 * @return 设置最小生成数大于0的FactionEntityType列表
	 */
	protected @NotNull List<RaiderType> getMustSpawnRaiderTypeList() {
		return raiderTypes.stream()
						  .filter(factionEntityType -> factionEntityType.getMinWave() <= spawnedWave && factionEntityType.getMaxWave() >= spawnedWave)
						  .filter(raiderType -> {
							  int count = factionEntityTypeCountMap.getOrDefault(raiderType.getFactionEntityTypeLocation(), 0);
							  return count < raiderType.getMinSpawned();
						  })
						  .toList();
	}
	
	/**
	 * 本轮次允许生成的RaiderType
	 *
	 * @return 最大生成数还未达到的FactionEntityType列表
	 */
	protected @NotNull List<RaiderType> getSpawnableRaiderTypeList() {
		return raiderTypes.stream()
						  .filter(factionEntityType -> factionEntityType.getMinWave() <= spawnedWave && factionEntityType.getMaxWave() >= spawnedWave)
						  .filter(raiderType -> {
							  int count = factionEntityTypeCountMap.getOrDefault(raiderType.getFactionEntityTypeLocation(), 0);
							  return count < raiderType.getMaxSpawned();
						  })
						  .toList();
	}
	
	public void joinRaid(IRaider raider) {
		raiders.add(raider);
		raider.setRaid(this);
	}
	
	protected void updateBossBar(float progress) {
		bossBar.setProgress(Mth.clamp(progress, 0.0F, 1.0F));
	}
	
	/**
	 * 负责更新Player 加入与离开Raid
	 */
	protected void updatePlayer() {
		for (ServerPlayer player : level.players()) {
			if (player.blockPosition().distSqr(getCenter()) < Raid.RAID_REMOVAL_THRESHOLD_SQR) {
				if (!bossBar.getPlayers().contains(player)) {
					bossBar.addPlayer(player);
				}
			} else {
				if (bossBar.getPlayers().contains(player)) {
					bossBar.removePlayer(player);
				}
			}
		}
	}
	
	/**
	 * 更新Raider 最大生命值 最小生命值 存活实体 Leader实体 Raider更新频率必须与BossBar更新频率同步或更快，否则BossBar会抽搐
	 */
	protected void updateRaider() {
		float totalHealth = 0;
		float health = 0;
		aliveRaiders.clear();
		
		Set<IRaider> outOfRaidRaiders = new HashSet<>();
		for (IRaider raider : raiders) {
			if (raider.getMob().isAlive()) {
				if (raider.getMob().blockPosition().distSqr(getCenter()) < Raid.RAID_REMOVAL_THRESHOLD_SQR) {
					health += raider.getMob().getHealth();
					aliveRaiders.add(raider);
				} else {
					outOfRaidRaiders.add(raider);
				}
			} else {
				if (raider.isLeader()) {
					leaderRaider = null;
				}
			}
			totalHealth += raider.getMob().getMaxHealth();
			
		}
		
		for (IRaider raider : outOfRaidRaiders) {
			removeRaider(raider);
		}
		this.totalHealth = totalHealth;
		this.health = health;
	}
	
	protected void removeRaider(IRaider raider) {
		raiders.remove(raider);
		raider.setRaid(null);
		if (raider.isLeader()) {
			raider.setLeader(false);
			leaderRaider = null;
		}
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getTotalHealth() {
		return totalHealth;
	}
	
	protected boolean firstWaveNotSpawned() {
		return spawnedWave == 0;
	}
	
	@Nullable
	protected BlockPos findRandomPos(int offsetMultiplier, int maxTry) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		
		for (int i1 = 0; i1 < maxTry; ++i1) {
			float f = getLevel().getRandom().nextFloat() * ((float) Math.PI * 2F);
			int j = getCenter().getX() + Mth.floor(Mth.cos(f) * 32.0F * (float) offsetMultiplier) + this.level.random.nextInt(5);
			int l = getCenter().getZ() + Mth.floor(Mth.sin(f) * 32.0F * (float) offsetMultiplier) + this.level.random.nextInt(5);
			int k = getLevel().getHeight(Heightmap.Types.WORLD_SURFACE, j, l);
			pos.set(j, k, l);
			
			if (getLevel().hasChunksAt(pos.getX() - 10, pos.getZ() - 10, pos.getX() + 10, pos.getZ() + 10) && this.level.isPositionEntityTicking(pos) && level.getBlockState(pos.below()).is(TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation(RaidCraft.MODID, "raiders_spawnable_on"))) && level.getBlockState(pos).isAir()) {
				return pos;
			}
			
		}
		
		return null;
	}
	
	public boolean hasMoreWave() {
		return spawnedWave < totalWave;
	}
	
	public Set<IRaider> getAliveRaiders() {
		return aliveRaiders;
	}
	
	public int getNumOfAliveRaiders() {
		return getAliveRaiders().size();
	}
	
	public void setBadOmenLevel(int level) {
		this.badOmenLevel = level;
	}
	
	public int getBadOmenLevel() {
		return badOmenLevel;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public int getId() {
		return id;
	}
	
	public void setLeaderRaider(IRaider raider) {
		leaderRaider = raider;
	}
	
	private void equipBanner(IRaider raider) {
		Mob mob = raider.getMob();
		mob.setItemSlot(EquipmentSlot.HEAD, this.faction.getBanner());
		mob.setDropChance(EquipmentSlot.HEAD, 2.0F);
	}
	
	public void tick() {
		if (isStopped()) return;
		
		activeTicks++;
		if (activeTicks >= 48000 || level.getDifficulty() == Difficulty.PEACEFUL) {
			stop();
			return;
		}
		
		updatePlayer();
		// 启动阶段
		if (!isStarted()) {
			if (raidCooldownTicks == 0) {
				raidCooldownTicks = 300;
				start();
			} else {
				raidCooldownTicks--;
				updateBossBar((float) (300 - raidCooldownTicks) / 300);
			}
			return;
		}
		
		// 活动阶段
		if (!isOver()) {
			updateCenter();
			updateRaider();
			if (getNumOfAliveRaiders() == 0) {
				if (firstWaveNotSpawned()) {
					spawnWave();
				} else {
					if (hasMoreWave()) {
						if (raidCooldownTicks == 0) {
							raidCooldownTicks = 300;
							raiders.clear(); // 清理旧的实体
							leaderRaider = null;
							spawnWave();
						} else {
							raidCooldownTicks--;
							updateBossBar((300 - (float) raidCooldownTicks) / 300);
						}
						bossBar.setName(raidConfig.getNameComponent());
					} else {
						if (raidCooldownTicks == 0) {
							victory();
						} else {
							raidCooldownTicks--;
							bossBar.setName(raidConfig.getNameComponent());
						}
						updateBossBar(0.0F);
					}
				}
			} else {
				if (bossBar.getPlayers().isEmpty()) {
					if (raidCooldownTicks == 0) {
						defeat();
					} else {
						raidCooldownTicks--;
					}
				} else {
					raidCooldownTicks = 300;
				}
				if (getNumOfAliveRaiders() <= 3) {
					if (activeTicks % 200 == 0) {
						getAliveRaiders().forEach(raider -> raider.getMob().addEffect(new MobEffectInstance(MobEffects.GLOWING, 200)));
					}
					bossBar.setName(raidConfig.getNameComponent().copy().append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", getNumOfAliveRaiders())));
				}
				updateBossBar(getHealth() / getTotalHealth());
			}
			return;
		}
		
		// 结束阶段
		if (celebrateTicks == 0) {
			stop();
		} else {
			celebrateTicks--;
		}
	}
	
	
	/**
	 * 计算强度 波次
	 */
	protected void computeStrength() {
		// Strength & TotalWave
		int baseStrength = raidConfig.getStrength();
		int baseWave = 3;
		if (level.getDifficulty() == Difficulty.EASY) {
			baseStrength += 10;
		} else if (level.getDifficulty() == Difficulty.NORMAL) {
			baseStrength += 20;
			baseWave += 2;
		} else if (level.getDifficulty() == Difficulty.HARD) {
			baseStrength += 40;
			baseWave += 2;
		}
		this.strength = baseStrength + (this.badOmenLevel + 1) * 10;
		this.totalWave += baseWave;
		MinecraftForge.EVENT_BUS.post(new RaidComputeStrengthEvent(level, this));
	}
	
	protected void playSound(BlockPos pPos, Holder<SoundEvent> soundEvent) {
		float pitch = 1.0F;
		float volume = 64.0F;
		long seed = level.random.nextLong();
		
		Collection<ServerPlayer> collection = bossBar.getPlayers();
		for (ServerPlayer serverplayer : level.players()) {
			Vec3 vec3 = serverplayer.position();
			Vec3 vec31 = Vec3.atCenterOf(pPos);
			double d0 = Math.sqrt((vec31.x - vec3.x) * (vec31.x - vec3.x) + (vec31.z - vec3.z) * (vec31.z - vec3.z)); // 距离
			double d1;
			double d2;
			if (d0 == 0) {
				d1 = vec3.x; // 如果距离为0，偏移后的x坐标保持不变
				d2 = vec3.z; // 如果距离为0，偏移后的z坐标保持不变
			} else if (d0 < 13.0D) {
				d1 = vec31.x; // 如果距离小于13，直接使用目标位置的x坐标
				d2 = vec31.z; // 如果距离小于13，直接使用目标位置的z坐标
			} else {
				d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x);
				d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z);
			}
			if (d0 <= 64.0D || collection.contains(serverplayer)) {
				serverplayer.connection.send(new ClientboundSoundPacket(soundEvent, SoundSource.NEUTRAL, d1, serverplayer.getY(), d2, volume, pitch, seed));
			}
		}
		
	}
	
	public int getSpawnedWave() {
		return spawnedWave;
	}
	
	public int getTotalWave() {
		return totalWave;
	}
	
	public IRaider getLeader() {
		return leaderRaider;
	}
	
	public void start() {
		started = true;
		absorbBadOmen();
		computeStrength();
	}
	
	public void stop() {
		stopped = true;
		bossBar.setVisible(false);
		bossBar.removeAllPlayers();
	}
	
	public void victory() {
		victory = true;
		updateBossBar(0.0F);
		bossBar.setName(raidConfig.getVictoryComponent());
		playSound(center, raidConfig.getVictorySoundEvent());
	}
	
	public void defeat() {
		defeat = true;
		updateBossBar(0.0F);
		bossBar.setName(raidConfig.getDefeatComponent());
		playSound(center, raidConfig.getDefeatSoundEvent());
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public boolean isOver() {
		return isVictory() || isDefeat();
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isVictory() {
		return victory;
	}
	
	public boolean isDefeat() {
		return defeat;
	}
	
	public boolean isActive() {
		return !isStopped() && isStarted();
	}
	
	public BlockPos getCenter() {
		return center;
	}
	
	public ServerLevel getLevel() {
		return level;
	}
	
	public Faction getFaction() {
		return faction;
	}
	
	public ItemStack getBanner() {
		return faction.getBanner();
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		// Data
		tag.putInt("Id", this.id);
		tag.putFloat("TotalHealth", this.totalHealth);
		tag.putFloat("Health", this.health);
		tag.putInt("RaidCooldown", this.raidCooldownTicks);
		tag.putInt("CelebrateTicks", this.celebrateTicks);
		tag.putFloat("ActiveTicks", this.health);
		tag.putBoolean("Started", this.started);
		tag.putBoolean("Victory", this.victory);
		tag.putBoolean("Defeat", this.defeat);
		tag.putBoolean("Stopped", this.stopped);
		//
		ResourceLocation factionLocation = DataPackRegistries.FACTIONS.getKey(this.faction);
		if (factionLocation != null) {
			tag.putString("Faction", factionLocation.toString());
		} else {
			RaidCraft.LOGGER.error("Faction's key is null");
		}
		tag.put("Center", NbtUtils.writeBlockPos(this.center));
		tag.putInt("BadOmenLevel", this.badOmenLevel);
		return tag;
	}
	
}