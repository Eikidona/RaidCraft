package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.api.event.raid.RaidComputeStrengthEvent;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
import com.forget_melody.raid_craft.utils.weight_table.IWeightEntry;
import com.forget_melody.raid_craft.utils.weight_table.IWeightTable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractRaid implements IRaid {
	protected final ServerLevel level;
	protected final RaidData raidData;
	protected final RaidType raidType;
	protected final ServerBossEvent bossBar;
	protected BlockPos center;
	protected IRaider leaderRaider;
	protected Set<IRaider> raiders = new HashSet<>();
	protected Set<IRaider> aliveRaiders = new HashSet<>();
	protected int spawnedWave = 0;
	protected int totalWave = 0;
	protected final IWeightTable<EntityType<?>> entityTypeTable;
	protected final IWeightTable<RaiderType> raiderTypeTable;
	protected int strength;
	
	public AbstractRaid(ServerLevel level, int id, RaidType raidType, BlockPos center) {
		this.level = level;
		this.raidData = new RaidData(id, 300, 600);
		this.raidType = raidType;
		this.bossBar = new ServerBossEvent(this.raidType.getNameComponent(), this.raidType.getColor(), this.raidType.getOverlay());
		this.bossBar.setProgress(0.0F);
		this.bossBar.setVisible(true);
		this.center = center;
		
		// Table
		List<IWeightEntry<EntityType<?>>> entityTypeEntries = new ArrayList<>();
		for (RaidType.RaiderEntry entry : raidType.getRaiders()) {
			if (ForgeRegistries.ENTITY_TYPES.containsKey(entry.entityType())) {
				EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entry.entityType());
				entityTypeEntries.add(IWeightEntry.of(entityType, entry.weight()));
			} else {
				RaidCraft.LOGGER.warn("Not found entity type id: {}", entry.entityType());
			}
		}
		List<IWeightEntry<RaiderType>> raiderTypeEntries = new ArrayList<>();
		for (RaidType.RaiderTypeEntry entry : raidType.getRaiderTypes()) {
			if (DatapackRegistries.RAIDER_TYPES.containsKey(entry.raiderType())) {
				RaiderType raiderType = DatapackRegistries.RAIDER_TYPES.getValue(entry.raiderType());
				raiderTypeEntries.add(IWeightEntry.of(raiderType, entry.weight()));
			} else {
				RaidCraft.LOGGER.warn("Not found raider type id: {}", entry.raiderType());
			}
		}
		this.entityTypeTable = IWeightTable.of(entityTypeEntries);
		this.raiderTypeTable = IWeightTable.of(raiderTypeEntries);
	}
	
	public AbstractRaid(ServerLevel level, CompoundTag tag) {
		this.level = level;
		this.raidData = new RaidData(tag);
		this.raidType = DatapackRegistries.RAID_TYPES.getValue(new ResourceLocation((tag.getString("RaidType"))));
		this.bossBar = new ServerBossEvent(this.raidType.getNameComponent(), this.raidType.getColor(), this.raidType.getOverlay());
		this.bossBar.setVisible(true);
		this.center = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
		
		// Table
		List<IWeightEntry<EntityType<?>>> entityTypeEntries = new ArrayList<>();
		for (RaidType.RaiderEntry entry : raidType.getRaiders()) {
			if (ForgeRegistries.ENTITY_TYPES.containsKey(entry.entityType())) {
				EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entry.entityType());
				entityTypeEntries.add(IWeightEntry.of(entityType, entry.weight()));
			} else {
				RaidCraft.LOGGER.warn("Not found entity type id: {}", entry.entityType());
			}
		}
		List<IWeightEntry<RaiderType>> raiderTypeEntries = new ArrayList<>();
		for (RaidType.RaiderTypeEntry entry : raidType.getRaiderTypes()) {
			if (DatapackRegistries.RAIDER_TYPES.containsKey(entry.raiderType())) {
				RaiderType raiderType = DatapackRegistries.RAIDER_TYPES.getValue(entry.raiderType());
				raiderTypeEntries.add(IWeightEntry.of(raiderType, entry.weight()));
			} else {
				RaidCraft.LOGGER.warn("Not found raider type id: {}", entry.raiderType());
			}
		}
		this.entityTypeTable = IWeightTable.of(entityTypeEntries);
		this.raiderTypeTable = IWeightTable.of(raiderTypeEntries);
	}
	
	protected void updateBossBar(float progress) {
		bossBar.setProgress(Mth.clamp(progress, 0.0F, 1.0F));
	}
	
	protected void updatePlayer() {
		for (ServerPlayer player : level.players()) {
			if (player.blockPosition().distSqr(getCenter()) < IRaid.RAID_REMOVAL_THRESHOLD_SQR) {
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
	
	protected void updateRaider() {
		float totalHealth = 0;
		float health = 0;
		aliveRaiders.clear();
		
		Set<IRaider> outOfRaidRaiders = new HashSet<>();
		for (IRaider raider : raiders) {
			if (raider.getEntity().isAlive()) {
				if (raider.getEntity().blockPosition().distSqr(getCenter()) < IRaid.RAID_REMOVAL_THRESHOLD_SQR) {
					health += raider.getEntity().getHealth();
					aliveRaiders.add(raider);
				} else {
					outOfRaidRaiders.add(raider);
				}
			}
			totalHealth += raider.getEntity().getMaxHealth();
			
		}
		
		for (IRaider raider : outOfRaidRaiders) {
			removeRaider(raider);
		}
		raidData.setTotalHealth(totalHealth);
		raidData.setHealth(health);
	}
	
	/**
	 * 更新中心位置 也可以不更新
	 */
	protected abstract void updateCenter();
	
	protected void removeRaider(IRaider raider) {
		raiders.remove(raider);
		raider.setRaid(null);
		raider.setWave(null);
		if (raider.isLeader()) {
			raider.setLeader(false);
			leaderRaider = null;
		}
	}
	
	public float getHealth() {
		return raidData.getHealth();
	}
	
	public float getTotalHealth() {
		return raidData.getTotalHealth();
	}
	
	protected boolean firstWaveNotSpawned() {
		return spawnedWave == 0;
	}
	
	protected boolean shouldSpawnWave() {
		return firstWaveNotSpawned() || hasMoreWave() && raidData.getRaidCooldown() == 0;
	}
	
	/**
	 * 生成波次
	 */
	protected abstract void spawnWave();
	
	@Nullable
	protected BlockPos findRandomPos(int offsetMultiplier, int maxTry) {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		
		for (int i1 = 0; i1 < maxTry; ++i1) {
			float f = getLevel().getRandom().nextFloat() * ((float) Math.PI * 2F);
			int j = getCenter().getX() + Mth.floor(Mth.cos(f) * 32.0F * (float) offsetMultiplier) + this.level.random.nextInt(5);
			int l = getCenter().getZ() + Mth.floor(Mth.sin(f) * 32.0F * (float) offsetMultiplier) + this.level.random.nextInt(5);
			int k = getLevel().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, j, l);
			blockpos$mutableblockpos.set(j, k, l);
			
			if (getLevel().hasChunksAt(blockpos$mutableblockpos.getX() - 10, blockpos$mutableblockpos.getZ() - 10, blockpos$mutableblockpos.getX() + 10, blockpos$mutableblockpos.getZ() + 10) && this.level.isPositionEntityTicking(blockpos$mutableblockpos)) {
				// 删掉的条件
//				 && (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, blockpos$mutableblockpos, null) || this.level.getBlockState(blockpos$mutableblockpos.below()).is(Blocks.SNOW) && this.level.getBlockState(blockpos$mutableblockpos).isAir())
				return blockpos$mutableblockpos;
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
	
	@Override
	public int getStrength() {
		return strength;
	}
	
	@Override
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	@Override
	public int getId() {
		return raidData.getId();
	}
	
	@Override
	public void joinRaid(IRaider raider) {
		raiders.add(raider);
		raider.setRaid(this);
		raider.setWave(spawnedWave);
		if (leaderRaider == null) {
			raider.setLeader(true);
			leaderRaider = raider;
		}
	}
	
	@Override
	public void tick() {
		if (isStopped()) return;
		
		if(raidData.getActiveTicks() >= 72000){
			stop();
			return;
		}
		
		updatePlayer();
		
		if (!isStarted()) {
			if (raidData.getRaidCooldown() == 0) {
				raidData.setRaidCooldown(300);
				start();
			} else {
				raidData.setRaidCooldown(raidData.getRaidCooldown() - 1);
				updateBossBar((300 - (float) raidData.getRaidCooldown()) / 300);
			}
			return;
		}
		
		if (!isOver()) {
			RaidCraft.LOGGER.info("Raid is Not Over");
			if (raidData.getActiveTicks() % 200 == 0) {
				updateCenter();
			}
			updateRaider();
			if (getNumOfAliveRaiders() == 0) {
				RaidCraft.LOGGER.info("Raid num of alive raiders is 0");
				if (firstWaveNotSpawned()) {
					RaidCraft.LOGGER.info("try spawn frist wave");
					spawnWave();
				} else {
					if (hasMoreWave()) {
						if (raidData.getRaidCooldown() == 0) {
							raidData.setRaidCooldown(300);
							raiders.clear(); // 生成之前清理旧的实体
							spawnWave();
						} else {
							raidData.setRaidCooldown(raidData.getRaidCooldown() - 1);
							updateBossBar((300 - (float) raidData.getRaidCooldown()) / 300);
						}
					} else {
						victory();
					}
				}
			} else {
				RaidCraft.LOGGER.info("Raid num of alive raiders is not 0");
				if (bossBar.getPlayers().isEmpty()) {
					if (raidData.getRaidCooldown() == 0) {
						defeat();
					} else {
						raidData.setRaidCooldown(raidData.getRaidCooldown() - 1);
					}
				} else {
					raidData.setRaidCooldown(300);
				}
				updateBossBar(getHealth() / getTotalHealth());
			}
		} else {
			if (raidData.getCelebrateTicks() == 0) {
				stop();
			} else {
				raidData.setCelebrateTicks(raidData.getCelebrateTicks() - 1);
			}
		}
		raidData.setActiveTicks(raidData.getActiveTicks() + 1);
	}

//	protected void init() {
//		// Table
//		List<IWeightEntry<EntityType<?>>> entityTypeEntries = new ArrayList<>();
//		for (RaidType.RaiderEntry entry : raidType.getRaiders()) {
//			if (ForgeRegistries.ENTITY_TYPES.containsKey(entry.entityType())) {
//				EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entry.entityType());
//				entityTypeEntries.add(IWeightEntry.of(entityType, entry.weight()));
//			} else {
//				RaidCraft.LOGGER.warn("Not found entity type id: {}", entry.entityType());
//			}
//		}
//		List<IWeightEntry<RaiderType>> raiderTypeEntries = new ArrayList<>();
//		for (RaidType.RaiderTypeEntry entry : raidType.getRaiderTypes()) {
//			if (DatapackRegistries.RAIDER_TYPES.containsKey(entry.raiderType())) {
//				RaiderType raiderType = DatapackRegistries.RAIDER_TYPES.getValue(entry.raiderType());
//				raiderTypeEntries.add(IWeightEntry.of(raiderType, entry.weight()));
//			} else {
//				RaidCraft.LOGGER.warn("Not found raider type id: {}", entry.raiderType());
//			}
//		}
//		this.entityTypeTable = IWeightTable.of(entityTypeEntries);
//		this.raiderTypeTable = IWeightTable.of(raiderTypeEntries);
//
//	}
	
	protected void computeStrength() {
		// Strength & TotalWave
		int baseStrength = raidType.getStrength();
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
		this.strength = baseStrength;
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
			double d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x); // 偏移
			double d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z); // 偏移
			if (d0 <= 64.0D || collection.contains(serverplayer)) {
				serverplayer.connection.send(new ClientboundSoundPacket(soundEvent, SoundSource.NEUTRAL, d1, serverplayer.getY(), d2, volume, pitch, seed));
			}
		}
		
	}
	
	@Override
	public void start() {
		raidData.setStarted(true);
		computeStrength();
	}
	
	@Override
	public void stop() {
		raidData.setStopped(true);
		bossBar.setVisible(false);
		bossBar.removeAllPlayers();
	}
	
	@Override
	public void victory() {
		RaidCraft.LOGGER.info("Raid victory");
		raidData.setVictory(true);
		updateBossBar(0.0F);
		bossBar.setName(raidType.getVictoryComponent());
		playSound(center, raidType.getVictorySoundEvent());
	}
	
	@Override
	public void defeat() {
		RaidCraft.LOGGER.info("Raid defeat");
		raidData.setDefeat(true);
		updateBossBar(0.0F);
		bossBar.setName(raidType.getDefeatComponent());
		playSound(center, raidType.getDefeatSoundEvent());
	}
	
	@Override
	public boolean isStopped() {
		return raidData.isStopped();
	}
	
	@Override
	public boolean isOver() {
		return isVictory() || isDefeat();
	}
	
	@Override
	public boolean isStarted() {
		return raidData.isStarted();
	}
	
	@Override
	public boolean isVictory() {
		return raidData.isVictory();
	}
	
	@Override
	public boolean isDefeat() {
		return raidData.isDefeat();
	}
	
	@Override
	public boolean isActive() {
		return !isStopped() && isStarted();
	}
	
	@Override
	public BlockPos getCenter() {
		return center;
	}
	
	@Override
	public ServerLevel getLevel() {
		return level;
	}
	
	@Override
	public RaidType getRaidType() {
		return raidType;
	}
	
	@Override
	public CompoundTag save() {
		CompoundTag tag = raidData.save();
		tag.putString("RaidType", DatapackRegistries.RAID_TYPES.getKey(this.raidType).toString());
		tag.putInt("X", this.center.getX());
		tag.putInt("Y", this.center.getY());
		tag.putInt("Z", this.center.getZ());
		return tag;
	}
}
