package com.forget_melody.raid_craft.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.faction_interaction.IFactionInteraction;
import com.forget_melody.raid_craft.capabilities.raid_interaction.IRaidInteraction;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.MoveTowardsRaidGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.ObtainRaidLeaderBannerGoal;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
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

public abstract class Raid {
	public static final int RAID_REMOVAL_THRESHOLD_SQR = 12544;
	protected final int id;
	protected final ServerLevel level;
	protected final List<Faction> factions = new ArrayList<>();
	protected final RaidConfig raidConfig;
	protected final ServerBossEvent raidEvent;
	protected BlockPos center;
	private RaidStatus status = RaidStatus.START;
	protected final Map<Integer, Set<IRaider>> waveRaiderMap = new HashMap<>();
	protected final Map<Integer, IRaider> waveLeaderMap = new HashMap<>();
	protected int activeTicks = 0;
	protected int postRaidTicks = 0;
	protected int raidCooldownTicks = 300;
	protected int celebrateTicks = 600;
	protected int waveSpawned = 0;
	protected int waveTotal = 0;
	protected float totalHealth = 0;
	protected int strength = 20;
	protected int badOmenLevel = 0;
	protected final List<BlockPos> waveSpawnPos = new ArrayList<>();
	protected final List<UUID> heroes = new ArrayList<>();
	protected boolean started = false;
	protected boolean stopped = false;
	protected boolean victory = false;
	protected boolean defeat = false;
	
	public Raid(int id, ServerLevel level, Faction faction, BlockPos center) {
		this.id = id;
		this.level = level;
		factions.add(faction);
		this.raidConfig = factions.get(0).getRaidConfig();
		this.raidEvent = new ServerBossEvent(this.raidConfig.getNameComponent(), this.raidConfig.getColor(), this.raidConfig.getOverlay());
		raidEvent.setProgress(0.0F);
		raidEvent.setVisible(true);
		this.center = center;
	}
	
	public Raid(ServerLevel level, CompoundTag tag) {
		this.level = level;
		this.id = tag.getInt("Id");
		ListTag factionList = tag.getList("Factions", Tag.TAG_STRING);
		tag.getList("Factions", Tag.TAG_STRING).forEach(tag1 -> {
			StringTag stringTag = (StringTag) tag1;
			ResourceLocation id = new ResourceLocation(stringTag.getAsString());
			Faction faction = DataPackRegistries.FACTIONS.getValue(id);
			if (faction != null) {
				this.factions.add(faction);
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
		this.started = tag.getBoolean("Started");
		this.stopped = tag.getBoolean("Stopped");
		this.victory = tag.getBoolean("Victory");
		this.defeat = tag.getBoolean("Defeat");
		
		ListTag listTag = tag.getList("Heroes", Tag.TAG_INT_ARRAY);
		listTag.forEach(tag1 -> {
			IntArrayTag intArrayTag = (IntArrayTag) tag1;
			heroes.add(UUIDUtil.uuidFromIntArray(intArrayTag.getAsIntArray()));
		});
	}
	
	protected abstract RaidType getType();
	
	protected abstract boolean checkLoseCondition();
	
	protected abstract void updateTargetPos();
	
	protected abstract int computedStrength();
	
	protected float getStrengthFactor() {
		float factor = 0.0F;
		Difficulty difficulty = level.getDifficulty();
		switch (difficulty) {
			case EASY -> factor = 0.7F;
			
			case NORMAL -> factor = 1.0F;
			
			case HARD -> factor = 1.2F;
		}
		return factor;
	}
	
	protected int computedTotalWave() {
		int baseWave = 0;
		Difficulty difficulty = level.getDifficulty();
		switch (difficulty) {
			case EASY -> baseWave += level.getRandom().nextInt(3) + 1;
			
			case NORMAL -> baseWave += level.getRandom().nextInt(5) + 1;
			
			case HARD -> baseWave += level.getRandom().nextInt(7) + 1;
		}
		return baseWave;
	}
	
	
	public void tick() {
		if (!level.hasChunkAt(center)) {
			return;
		}
		
		if (!canContinueTick()) {
//			RaidCraft.LOGGER.info("Stop Raid, because difficulty is peaceful.");
			stop();
			return;
		}
		updatePlayer();
		
		activeTicks++;
		if (activeTicks >= 48000) {
			stop();
			return;
		}
		
		if (status == RaidStatus.START) {
			// 检查RaidConfig有效性
			if (raidConfig.getRaiderTypes().isEmpty()) {
//				RaidCraft.LOGGER.info("Stop Raid, because raiderTypes is empty.");
				stop();
				return;
			}
			// 检查目标有效性
			if (checkLoseCondition()) {
//				RaidCraft.LOGGER.info("check Raid Target Block Pos: {}", getCenter());
				updateTargetPos();
				postRaidTicks++;
				if (postRaidTicks >= 40) {
//					RaidCraft.LOGGER.info("Stop Raid, because checkLoseCondition.");
					stop();
					return;
				}
//				RaidCraft.LOGGER.info("after check Raid Target Block Pos: {}", getCenter());
//				if (checkLoseCondition()) {
//					stop();
//					return;
//				}
			} else {
				postRaidTicks = 0;
			}
			// BossBar
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
			updateRaiders(); // getNumOfLivingRaider方法依赖此方法更新的值
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
//					RaidCraft.LOGGER.info("BossBar branch-1");
				}
				raidEvent.setName(raidConfig.getNameComponent());
			} else {
				raidCooldownTicks = 300;
			}
			if (numLivingRaider > 0) {
				if (numLivingRaider <= 3) {
					raidEvent.setName(raidConfig.getNameComponent().copy().append("-").append(Component.translatable("event.minecraft.raid.raiders_remaining", numLivingRaider)));
				}
//				RaidCraft.LOGGER.info("BossBar branch-2");
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
				if (postRaidTicks >= 40) {
					victory();
					return;
				} else {
					postRaidTicks++;
				}
			}
			// Defeat
			else if (numLivingRaider > 0 && getPLayers().isEmpty()) {
				if (postRaidTicks >= 40) {
					defeat();
					return;
				} else {
					postRaidTicks++;
				}
			}
			// Target Defeat
			else if (checkLoseCondition()) {
				if (postRaidTicks >= 40) {
					updateTargetPos();
					if (checkLoseCondition()) {
						defeat();
						return;
					}
				} else {
					postRaidTicks++;
				}
				
			} else {
//				RaidCraft.LOGGER.info("成功失败阶段PostRaidTicks归零");
				postRaidTicks = 0;
			}
			return;
		}
		
		if (status == RaidStatus.OVER) {
			//BossBar
			if (isVictory()) {
				raidEvent.setName(raidConfig.getVictoryComponent());
				raidEvent.setProgress(0.0F);
			} else {
				raidEvent.setName(raidConfig.getDefeatComponent());
			}
			
			if (celebrateTicks > 0) {
				celebrateTicks--;
			} else {
				stop();
			}
		}
		
	}
	
	protected boolean canContinueTick() {
		return level.getDifficulty() != Difficulty.PEACEFUL;
	}
	
	protected void absorbBadOmenLevel() {
		for (ServerPlayer player : getPLayers()) {
			if (badOmenLevel < 4) {
				badOmenLevel += IRaidInteraction.get(player).getBadOmenLevel() + 1;
			}
			IRaidInteraction.get(player).clearBadOmen();
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
		heroes.forEach(uuid -> {
			ServerPlayer player = (ServerPlayer) level.getPlayerByUUID(uuid);
			if (player != null) {
				player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
				player.awardStat(Stats.RAID_WIN);
				CriteriaTriggers.RAID_WIN.trigger(player);
			}
			
		});
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
		absorbBadOmenLevel();
		strength += (int) (computedStrength() * getStrengthFactor());
		strength += getBadOmenLevel() * 10;
		waveTotal = computedTotalWave();
	}
	
	public void setCenter(BlockPos blockPos) {
		this.center = blockPos;
	}
	
	private void updateRaiders() {
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
		
		outOfRaid.forEach(raider -> removeRaider(raider, true));
		deadRaider.forEach(raider -> removeRaider(raider, false));
	}
	
	private void removeRaider(IRaider raider, boolean isAlive) {
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
	
	private void removeAllRaiders() {
		waveRaiderMap.values().forEach(set -> set.forEach(raider -> {
			raider.setRaid(null);
			raider.setWave(0);
		}));
		waveLeaderMap.values().forEach(raider -> raider.setLeader(false));
	}
	
	private void updateBossBar() {
//		RaidCraft.LOGGER.info("health:{}, totalHealth: {}", getHealthOfLivingRaider(), totalHealth);
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
//			RaidCraft.LOGGER.info("Spawn Wave Size: {} i: {}", waveSpawnPos.size(), i);
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
//				RaidCraft.LOGGER.info("Spawn Must Spawn Group");
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
						IFactionEntity factionEntity = IFactionEntity.get(mob);
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
//					RaidCraft.LOGGER.info("Spawn can spawn Group");
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
	}
	
	private void addWaveRaider(int wave, IRaider raider) {
		waveRaiderMap.computeIfAbsent(wave, integer -> new HashSet<>()).add(raider);
		raider.setRaid(this);
		raider.setWave(wave);
		addGoals(raider);
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
		IFactionEntity factionEntity = IFactionEntity.get(mob);
		ItemStack banner = factionEntity.getFaction().getBanner().copy();
		mob.setItemSlot(EquipmentSlot.HEAD, banner);
		mob.setDropChance(EquipmentSlot.HEAD, 2.0F);
	}
	
	@Nullable
	private BlockPos findRandomPos(int offsetMultiplier, int maxTry) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i1 = 0; i1 < maxTry; ++i1) {
			float f = level.getRandom().nextFloat() * ((float) Math.PI * 2F);
			int x = center.getX() + Mth.floor(Mth.cos(f) * 48.0F * (float) offsetMultiplier) + this.level.random.nextInt(5);
			int z = center.getZ() + Mth.floor(Mth.sin(f) * 48.0F * (float) offsetMultiplier) + this.level.random.nextInt(5);
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
	
	public boolean hasMoreWave() {
		return waveSpawned < waveTotal;
	}
	
	public void stop() {
		stopped = true;
		raidEvent.removeAllPlayers();
		raidEvent.setVisible(false);
		removeAllRaiders();
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
//		RaidCraft.LOGGER.info("Save Factions: {}", this.factions.size());
		this.factions.forEach(faction -> {
			listTag.add(StringTag.valueOf(DataPackRegistries.FACTIONS.getKey(faction).toString()));
//			RaidCraft.LOGGER.info("FactionName: {}", DataPackRegistries.FACTIONS.getKey(faction).toString());
		});
//		RaidCraft.LOGGER.info("Save FactionList: {}", listTag.size());
		tag.put("Factions", listTag);
		tag.put("Center", NbtUtils.writeBlockPos(this.center));
		tag.putString("RaidStatus", this.status.name());
		tag.putInt("ActiveTicks", this.activeTicks);
		tag.putInt("RaidCooldownTicks", this.raidCooldownTicks);
		tag.putInt("WaveSpawned", this.waveSpawned);
		tag.putInt("WaveTotal", this.waveTotal);
		tag.putFloat("TotalHealth", this.totalHealth);
		tag.putInt("Strength", this.strength);
//		tag.putString("RaidTarget", RaidTargets.RAID_TARGETS.get().getKey(this.raidTarget).toString());
		tag.putBoolean("Started", this.started);
		tag.putBoolean("Stopped", this.stopped);
		tag.putBoolean("Victory", this.victory);
		tag.putBoolean("Defeat", this.defeat);
		tag.putInt("CelebrateTicks", this.celebrateTicks);
		ListTag listTag1 = new ListTag();
		heroes.forEach(uuid -> listTag1.add(new IntArrayTag(UUIDUtil.uuidToIntArray(uuid))));
		tag.put("Heroes", listTag1);
		tag.putString("RaidType", getType().name());
		return tag;
	}
	
	public ServerLevel getLevel() {
		return level;
	}
	
	public boolean isEnemyMobOfRaider(Mob mob) {
		for (Faction faction : factions) {
			if (faction.isEnemy(IFactionEntity.get(mob).getFaction())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEnemyPlayerOfRaider(ServerPlayer player) {
		if (heroes.contains(player.getUUID())) {
			return true;
		}
		boolean flag = false;
		for (Faction faction : factions) {
			if (!IFactionInteraction.get(player).isAlly(faction)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	public Collection<ServerPlayer> getPLayers() {
		return raidEvent.getPlayers();
	}
	
	public void addHero(ServerPlayer player) {
		this.heroes.add(player.getUUID());
	}
	
	public boolean isActive() {
//		RaidCraft.LOGGER.info("isActive: isStarted:{}, !isOver: {}, isVictory: {}, isDefeat: {}", isStarted(), !isOver(), isVictory(), isDefeat());
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
	
	public List<Faction> getFactions() {
		return factions;
	}
	
	protected void addGoals(IRaider raider) {
		raider.addGoal(2, new ObtainRaidLeaderBannerGoal(raider.getMob()));
		raider.addGoal(5, new MoveTowardsRaidGoal<>(raider.getMob()));
	}

//	public IRaidTarget getRaidTarget() {
//		return raidTarget;
//	}
	
	public int getBadOmenLevel() {
		return badOmenLevel;
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