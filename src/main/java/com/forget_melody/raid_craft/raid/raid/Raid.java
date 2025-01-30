package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import com.forget_melody.raid_craft.raid.raid.target.IRaidTarget;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.forget_melody.raid_craft.registries.RaidTargets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Raid {
	public static final int RAID_REMOVAL_THRESHOLD_SQR = 12544;
	private final int id;
	private final ServerLevel level;
	private final List<Faction> factions = new ArrayList<>();
	private final RaidConfig raidConfig;
	private final IRaidTarget raidTarget;
	private final ServerBossEvent raidEvent;
	private BlockPos center;
	private RaidStatus status = RaidStatus.START;
	private final Map<Integer, Set<IRaider>> waveRaiderMap = new HashMap<>();
	private final Map<Integer, IRaider> waveLeaderMap = new HashMap<>();
	private int activeTicks = 0;
	private int postRaidTicks = 0;
	private int raidCooldownTicks = 300;
	private int celebrateTicks = 600;
	private int waveSpawned = 0;
	private int waveTotal = 0;
	private float totalHealth = 0;
	private int strength = 0;
	private final List<BlockPos> waveSpawnPos = new ArrayList<>();
	private boolean started = false;
	private boolean stopped = false;
	private boolean victory = false;
	private boolean defeat = false;
	private boolean active = true;
	
	public Raid(int id, ServerLevel level, Faction faction, BlockPos center, IRaidTarget raidTarget) {
		this.id = id;
		this.level = level;
		factions.add(faction);
		this.raidConfig = factions.get(0).getRaidConfig();
		this.raidEvent = new ServerBossEvent(this.raidConfig.getNameComponent(), this.raidConfig.getColor(), this.raidConfig.getOverlay());
		raidEvent.setProgress(0.0F);
		raidEvent.setVisible(true);
		this.center = center;
		this.raidTarget = raidTarget;
	}
	
	public Raid(ServerLevel level, CompoundTag tag) {
		this.level = level;
		this.id = tag.getInt("Id");
		tag.getList("Factions", CompoundTag.TAG_STRING).forEach(tag1 -> {
			StringTag stringTag = (StringTag) tag1;
			ResourceLocation id = new ResourceLocation(stringTag.getAsString());
			Faction faction = DataPackRegistries.FACTIONS.getValue(id);
			if (faction != null) {
				factions.add(faction);
			} else {
				RaidCraft.LOGGER.error("Not found faction id {}", id);
			}
			if (factions.isEmpty()) {
				stop();
				RaidCraft.LOGGER.error("Not found any faction, factions is empty!");
			}
		});
		this.raidConfig = factions.get(0).getRaidConfig();
		this.raidEvent = new ServerBossEvent(this.raidConfig.getNameComponent(), this.raidConfig.getColor(), this.raidConfig.getOverlay());
		raidEvent.setVisible(true);
		this.center = NbtUtils.readBlockPos(tag.getCompound("Center"));
		this.status = RaidStatus.byName(tag.getString("RaidStatus"));
		this.activeTicks = tag.getInt("ActiveTicks");
		this.raidCooldownTicks = tag.getInt("RaidCooldownTicks");
		this.celebrateTicks = tag.getInt("CelebrateTicks");
		this.waveSpawned = tag.getInt("WaveSpawned");
		this.waveTotal = tag.getInt("WaveTotal");
		this.totalHealth = tag.getInt("TotalHealth");
		this.strength = tag.getInt("Strength");
		this.raidTarget = RaidTargets.RAID_TARGETS.get().getValue(new ResourceLocation(tag.getString("RaidTarget")));
		this.started = tag.getBoolean("Started");
		this.stopped = tag.getBoolean("Stopped");
		this.victory = tag.getBoolean("Victory");
		this.defeat = tag.getBoolean("Defeat");
		this.active = tag.getBoolean("Active");
	}
	
	public void tick() {
		if (isStopped()) {
			return;
		}
		
		boolean flag = active;
		active = level.hasChunkAt(center);
		if (flag != active) {
			if (!active) {
				if (postRaidTicks > 40) {
					RaidCraft.LOGGER.info("Stop Raid");
					stop();
				} else {
					postRaidTicks++;
				}
			} else {
				RaidCraft.LOGGER.info("运行检查阶段PostRaidTicks归零");
				postRaidTicks = 0;
			}
		}
		
		if (level.getDifficulty() == Difficulty.PEACEFUL) {
			stop();
			return;
		}
//		// Stop or Defeat
//		if (!raidTarget.isValidTarget(this)) {
//			Optional<BlockPos> optional = raidTarget.updateTargetPos(this);
//			if (optional.isEmpty()) {
//				if (!isStarted()) {
//					RaidCraft.LOGGER.info("Raid is not valid target");
//					stop();
//				} else {
//					defeat();
//				}
//				return;
//			} else {
//				setCenter(optional.get());
//			}
//		}
		
		updatePlayer(); // 干脆不延时了 延时延出问题来
		
		activeTicks++;
		if (activeTicks >= 48000) {
			stop();
			return;
		}
		
		if (status == RaidStatus.START) {
			// 检查RaidConfig有效性
			if(raidConfig.getRaiderTypes().isEmpty()){
				stop();
				return;
			}
			// 检查目标有效性
			if (!raidTarget.isValidTarget(this)) {
				Optional<BlockPos> optional = raidTarget.updateTargetPos(this);
				if (optional.isEmpty()) {
					RaidCraft.LOGGER.info("Raid is not valid target");
					stop();
					return;
				} else {
					setCenter(optional.get());
				}
			}
			if (raidCooldownTicks == 0) {
				raidCooldownTicks = 300;
				start();
			} else {
				raidCooldownTicks--;
				raidEvent.setProgress(Mth.clamp((float) (300 - raidCooldownTicks) / 300, 0.0F, 1.0F));
			}
			return;
		}
		
		if (status == RaidStatus.ACTIVE) {
			updateRaider(); // getNumOfLivingRaider方法依赖此方法更新的值
			// Spawn
			while (shouldSpawnWave()) {
				int multiplier = 1;
				for (int i = waveSpawnPos.size(); i < factions.size(); i++) {
					BlockPos blockPos = findRandomPos(multiplier, 20);
					if (blockPos != null) {
						waveSpawnPos.add(blockPos);
					}
					if (waveSpawnPos.size() >= factions.size()) {
						break;
					} else {
						if (multiplier < 2) {
							multiplier++;
						} else {
							break;
						}
					}
				}
				if (!waveSpawnPos.isEmpty()) {
					spawnWave();
					playWaveSound();
					waveSpawnPos.clear();
				}
			}
			
			int numLivingRaider = getNumOfLivingRaiders();
			// BossBar
			if (numLivingRaider == 0) {
				if (hasMoreWave() && raidCooldownTicks > 0) {
					raidCooldownTicks--;
					raidEvent.setProgress(Mth.clamp((float) (300 - raidCooldownTicks) / 300, 0.0F, 1.0F));
					RaidCraft.LOGGER.info("BossBar branch-1");
				}
				raidEvent.setName(raidConfig.getNameComponent());
			} else {
				raidCooldownTicks = 300;
			}
			if (numLivingRaider > 0) {
				if (numLivingRaider <= 3) {
					raidEvent.setName(raidConfig.getNameComponent().copy().append("-").append(Component.translatable("event.minecraft.raid.raiders_remaining", numLivingRaider)));
				} else {
					raidEvent.setName(raidConfig.getNameComponent());
				}
				RaidCraft.LOGGER.info("BossBar branch-2");
				updateBossBar();
			}
			// Glowing
			if (numLivingRaider > 0 && numLivingRaider <= 3) {
				if (activeTicks % 120 == 0) {
					Set<IRaider> livingRaiders = getLivingRaiders();
					livingRaiders.forEach(raider -> raider.getMob().addEffect(new MobEffectInstance(MobEffects.GLOWING, 60)));
				}
			}
			// Victory
			if (numLivingRaider == 0 && !hasMoreWave()) {
				if (postRaidTicks > 40) {
					victory();
					return;
				} else {
					postRaidTicks++;
				}
			}
			// Defeat
			else if (numLivingRaider > 0 && raidEvent.getPlayers().isEmpty()) {
				if (postRaidTicks > 40) {
					defeat();
					return;
				} else {
					postRaidTicks++;
				}
			} else {
				RaidCraft.LOGGER.info("成功失败阶段PostRaidTicks归零");
				postRaidTicks = 0;
			}
			// Target Defeat
			if (!raidTarget.isValidTarget(this)) {
				Optional<BlockPos> optional = raidTarget.updateTargetPos(this);
				if (optional.isEmpty()) {
					defeat();
					return;
				} else {
					setCenter(optional.get());
				}
			}
			RaidCraft.LOGGER.info("Raid: livingOfRaider: {}, raidCooldownTicks: {}, hasMoreWave: {}, postRaidTicks: {}", getNumOfLivingRaiders(), raidCooldownTicks, hasMoreWave(), postRaidTicks);
			return;
		}
		
		if (status == RaidStatus.OVER) {
			if (celebrateTicks > 0) {
				celebrateTicks--;
			} else {
				stop();
			}
		}
		
	}
	
	private void defeat() {
		defeat = true;
		status = RaidStatus.OVER;
		raidEvent.setName(raidConfig.getDefeatComponent());
		playDefeatSound();
	}
	
	private void victory() {
		victory = true;
		status = RaidStatus.OVER;
		raidEvent.setName(raidConfig.getVictoryComponent());
		raidEvent.setProgress(0.0F);
		playVictorySound();
	}
	
	public boolean isVictory() {
		return victory;
	}
	
	public boolean isDefeat() {
		return defeat;
	}
	
	private void updatePlayer() {
		for (ServerPlayer player : level.players()) {
			if (raidEvent.getPlayers().contains(player)) {
				if (player.blockPosition().distSqr(center) >= RAID_REMOVAL_THRESHOLD_SQR) {
					raidEvent.removePlayer(player);
				}
			} else {
				if (player.blockPosition().distSqr(center) < RAID_REMOVAL_THRESHOLD_SQR) {
					raidEvent.addPlayer(player);
				}
			}
		}
	}
	
	private void start() {
		this.status = RaidStatus.ACTIVE;
		this.started = true;
		this.strength = raidTarget.getTargetStrength(this) + 20;
		this.waveTotal = 3;
	}
	
	private void setCenter(BlockPos blockPos) {
		this.center = blockPos;
	}
	
	private void updateRaider() {
		Set<IRaider> outOfRaid = new HashSet<>();
		Set<IRaider> deadRaider = new HashSet<>();
		for (Set<IRaider> raiderSet : waveRaiderMap.values()) {
			for (IRaider raider : raiderSet) {
				Mob mob = raider.getMob();
				if (mob.isAlive()) {
					if (mob.blockPosition().distSqr(center) >= RAID_REMOVAL_THRESHOLD_SQR) {
						outOfRaid.add(raider);
					}
				} else {
					deadRaider.add(raider);
				}
			}
		}
		if (activeTicks % 200 == 0) {
			waveRaiderMap.values().stream().flatMap(Collection::stream).toList().forEach(raider -> RaidCraft.LOGGER.info("isAlive: {}, position: {}, isAddedToWorld: {}", raider.getMob().isAlive(), raider.getMob().blockPosition(), raider.getMob().isAddedToWorld()));
		}
		
		outOfRaid.forEach(raider -> removeForRaid(raider, true));
		deadRaider.forEach(raider -> removeForRaid(raider, false));
	}
	
	private void removeForRaid(IRaider raider, boolean isAlive) {
		waveRaiderMap.get(raider.getWave()).remove(raider);
		if (raider.isLeader()) {
			waveLeaderMap.remove(raider.getWave());
		}
		if (isAlive) {
			totalHealth -= raider.getMob().getMaxHealth();
			raider.setRaid(null);
			raider.setWave(0);
		}
	}
	
	private void updateBossBar() {
		RaidCraft.LOGGER.info("health:{}, totalHealth: {}", getHealthOfLivingRaider(), totalHealth);
		raidEvent.setProgress(Mth.clamp(getHealthOfLivingRaider() / totalHealth, 0.0F, 1.0F));
	}
	
	private float getHealthOfLivingRaider() {
		return (float) waveRaiderMap.values().stream().mapToDouble(raiderSet -> raiderSet.stream().mapToDouble(raider -> raider.getMob().getHealth()).sum()).sum();
	}
	
	private void spawnWave() {
		totalHealth = 0;
		waveSpawned++;
		for (int i = 0; i < waveSpawnPos.size(); i++) {
			BlockPos blockPos = waveSpawnPos.get(i);
			Faction faction = factions.get(i);
			spawnGroup(strength, blockPos, faction);
			RaidCraft.LOGGER.info("Spawn Wave Size: {} i: {}", waveSpawnPos.size(), i);
		}
	}
	
	private void playWaveSound() {
		Holder<SoundEvent> soundEvent = raidConfig.getWaveSoundEvent();
		if (soundEvent != null) {
			playSound(center, soundEvent);
		} else {
			RaidCraft.LOGGER.error("Wave SoundEvent is null");
		}
	}
	
	private void playVictorySound() {
		Holder<SoundEvent> soundEvent = raidConfig.getVictorySoundEvent();
		if (soundEvent != null) {
			playSound(center, soundEvent);
		} else {
			RaidCraft.LOGGER.error("Victory SoundEvent is null");
		}
	}
	
	private void playDefeatSound() {
		Holder<SoundEvent> soundEvent = raidConfig.getDefeatSoundEvent();
		if (soundEvent != null) {
			playSound(center, soundEvent);
		} else {
			RaidCraft.LOGGER.error("Defeat SoundEvent is null");
		}
	}
	
	private void playSound(BlockPos pPos, Holder<SoundEvent> soundEvent) {
		float pitch = 1.0F;
		float volume = 64.0F;
		long seed = level.random.nextLong();
		
		Collection<ServerPlayer> collection = raidEvent.getPlayers();
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
	
	private void spawnGroup(int strength, BlockPos blockPos, Faction faction) {
		Map<FactionEntityType, Integer> map = new HashMap<>();
		List<RaiderType> mustSpawn = faction.getRaidConfig().getRaiderTypes().stream().filter(raiderType -> raiderType.getStrength() < strength && waveSpawned >= raiderType.getMinWave() && waveSpawned <= raiderType.getMaxWave() && raiderType.getMinSpawned() > 0).toList();
		int currentWeight = 0;
		int currentStrength = 0;
		int totalWeight;
		int randomWeight;
		
		if (!mustSpawn.isEmpty()) {
			totalWeight = mustSpawn.stream().mapToInt(RaiderType::getWeight).sum();
			randomWeight = level.random.nextInt(totalWeight);
			for (RaiderType raiderType : mustSpawn) {
				RaidCraft.LOGGER.info("Spawn Must Spawn Group");
				currentWeight += raiderType.getWeight();
				if (currentWeight >= randomWeight) {
					int numSpawn = raiderType.getMinSpawned();
					int currentSpawn = 0;
					while (currentSpawn < numSpawn) {
						IRaider raider = raiderType.spawn(level, blockPos);
						currentStrength += raiderType.getStrength();
						if (raider != null) {
							joinRaid(raider, true);
						} else {
							break;
						}
						currentSpawn++;
						Mob mob = raider.getMob();
						IFactionEntity factionEntity = IFactionEntity.get(mob).get();
						FactionEntityType factionEntityType = factionEntity.getFactionEntityType();
						if (map.containsKey(factionEntityType)) {
							map.put(factionEntityType, map.get(factionEntityType) + 1);
						} else {
							map.put(factionEntityType, 1);
						}
					}
					
				}
			}
		}
		
		if (currentStrength < strength) {
			List<RaiderType> trySpawn = faction.getRaidConfig().getRaiderTypes().stream().filter(raiderType -> raiderType.getStrength() < strength && waveSpawned >= raiderType.getMinWave() && waveSpawned <= raiderType.getMaxWave() && map.getOrDefault(raiderType.getFactionEntityType(), 0) < raiderType.getMaxSpawned()).toList();
			currentWeight = 0;
			totalWeight = trySpawn.stream().mapToInt(RaiderType::getWeight).sum();
			
			while (currentStrength < strength) {
				randomWeight = level.random.nextInt(totalWeight);
				for (RaiderType raiderType : trySpawn) {
					RaidCraft.LOGGER.info("Spawn can spawn Group");
					currentWeight += raiderType.getWeight();
					if (currentWeight >= randomWeight) {
						IRaider raider = raiderType.spawn(level, blockPos);
						currentStrength += raiderType.getStrength();
						if (raider != null) {
							joinRaid(raider, true);
						} else {
							break;
						}
						FactionEntityType factionEntityType = raiderType.getFactionEntityType();
						if (map.containsKey(factionEntityType)) {
							map.put(factionEntityType, map.get(factionEntityType) + 1);
						} else {
							map.put(factionEntityType, 1);
						}
					}
				}
			}
			
		}
	}
	
	public void joinRaid(IRaider raider, boolean fresh) {
		addWaveRaider(waveSpawned, raider);
		if (getLeader() == null) {
			setRaider(raider, fresh);
		}
		if (fresh) {
			this.totalHealth += raider.getMob().getMaxHealth();
		}
		raider.setRaid(this);
		raider.setWave(waveSpawned);
	}
	
	private void addWaveRaider(int wave, IRaider raider) {
		waveRaiderMap.computeIfAbsent(wave, integer -> new HashSet<>()).add(raider);
		raider.setRaid(this);
		raider.setWave(wave);
	}
	
	public void setRaider(IRaider raider, boolean banner) {
		waveLeaderMap.put(waveSpawned, raider);
		raider.setLeader(true);
		if (banner) {
			equipBanner(raider);
		}
	}
	
	private void equipBanner(IRaider raider) {
		Mob mob = raider.getMob();
		IFactionEntity factionEntity = IFactionEntity.get(mob).get();
		ItemStack banner = factionEntity.getFaction().getBanner();
		mob.setItemSlot(EquipmentSlot.HEAD, banner);
		mob.setDropChance(EquipmentSlot.HEAD, 2.0F);
	}
	
	@Nullable
	private BlockPos findRandomPos(int offsetMultiplier, int maxTry) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i1 = 0; i1 < maxTry; ++i1) {
			float f = level.getRandom().nextFloat() * ((float) Math.PI * 2F);
			int x = center.getX() + Mth.floor(Mth.cos(f) * 32.0F * (float) offsetMultiplier) + this.level.random.nextInt(5);
			int z = center.getZ() + Mth.floor(Mth.sin(f) * 32.0F * (float) offsetMultiplier) + this.level.random.nextInt(5);
			int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
			pos.set(x, y, z);
			if (level.hasChunksAt(pos.getX() - 10, pos.getZ() - 10, pos.getX() + 10, pos.getZ() + 10) && this.level.isPositionEntityTicking(pos) && level.getBlockState(pos.below()).is(TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation(RaidCraft.MOD_ID, "raiders_spawnable_on"))) && level.getBlockState(pos).isAir()) {
				return pos;
			}
		}
		return null;
	}
	
	private boolean shouldSpawnWave() {
		return !hasFirstWaveSpawned() || raidCooldownTicks == 0 && hasMoreWave() && getNumOfLivingRaiders() == 0;
	}
	
	public int getNumOfLivingRaiders() {
		return waveRaiderMap.values().stream().mapToInt(Set::size).sum();
	}
	
	private Set<IRaider> getLivingRaiders() {
		return waveRaiderMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
	}
	
	public boolean hasFirstWaveSpawned() {
		return waveSpawned >= 1;
	}
	
	private boolean hasMoreWave() {
		return waveSpawned < waveTotal;
	}
	
	public void stop() {
		stopped = true;
		raidEvent.removeAllPlayers();
		raidEvent.setVisible(false);
	}
	
	public BlockPos getCenter() {
		return center;
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public int getId() {
		return id;
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Id", this.id);
		ListTag listTag = new ListTag();
		factions.forEach(faction -> listTag.add(StringTag.valueOf(DataPackRegistries.FACTIONS.getKey(faction).toString())));
		tag.put("Factions", listTag);
		tag.put("Center", NbtUtils.writeBlockPos(this.center));
		tag.putString("RaidStatus", this.status.name());
		tag.putInt("ActiveTicks", this.activeTicks);
		tag.putInt("RaidCooldownTicks", this.raidCooldownTicks);
		tag.putInt("WaveSpawned", this.waveSpawned);
		tag.putInt("WaveTotal", this.waveTotal);
		tag.putFloat("TotalHealth", this.totalHealth);
		tag.putInt("Strength", this.strength);
		tag.putString("RaidTarget", RaidTargets.RAID_TARGETS.get().getKey(this.raidTarget).toString());
		tag.putBoolean("Started", this.started);
		tag.putBoolean("Stopped", this.stopped);
		tag.putBoolean("Victory", this.victory);
		tag.putBoolean("Defeat", this.defeat);
		tag.putBoolean("Active", this.active);
		return tag;
	}
	
	public ServerLevel getLevel() {
		return level;
	}
	
	public boolean isTarget(Mob mob) {
		for (Faction faction : factions) {
			if (faction.getFactionRelations().getEnemies().contains(ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isActive() {
		RaidCraft.LOGGER.info("isActive: isStarted:{}, !isOver: {}, isVictory: {}, isDefeat: {}", isStarted(), !isOver(), isVictory(), isDefeat());
		return isStarted() && !isOver();
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isOver() {
		return isVictory() || isDefeat();
	}
	
	@Nullable
	public IRaider getLeader() {
		return waveLeaderMap.get(waveSpawned);
	}
	
	public boolean isBetweenWaves() {
		return hasFirstWaveSpawned() && getNumOfLivingRaiders() == 0 && raidCooldownTicks > 0;
	}
	
	private enum RaidStatus {
		START,
		ACTIVE,
		OVER;
		
		private static final RaidStatus[] VALUES = values();
		
		public static RaidStatus byName(String name) {
			for (RaidStatus status : VALUES) {
				if (status.name().equals(name)) {
					return status;
				}
			}
			return START;
		}
	}
}