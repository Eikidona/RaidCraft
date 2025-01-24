package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.raid_interaction.IRaidInteraction;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.event.raid.RaidComputeStrengthEvent;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.raid.raider_type.RaiderType;
import com.forget_melody.raid_craft.registries.DatapackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Raid implements IRaid {
	protected final Map<RaiderType, Integer> raiderTypeCountMap = new HashMap<>();
	protected final ServerLevel level;
	protected final RaidData raidData;
	protected final RaidType raidType;
	protected final ServerBossEvent bossBar;
	protected final List<RaiderType> raiderTypeList;
	protected BlockPos center;
	protected IRaider leaderRaider;
	protected Set<IRaider> raiders = new HashSet<>();
	protected Set<IRaider> aliveRaiders = new HashSet<>();
	protected int spawnedWave = 0;
	protected int totalWave = 0;
	protected int strength = 0;
	protected int badOmenLevel = 0;
	
	public Raid(ServerLevel level, int id, RaidType raidType, BlockPos center) {
		this.level = level;
		this.raidData = new RaidData(id);
		this.raidType = raidType;
		this.bossBar = new ServerBossEvent(this.raidType.getNameComponent(), this.raidType.getColor(), this.raidType.getOverlay());
		this.bossBar.setVisible(true);
		this.bossBar.setProgress(0.0F);
		this.raiderTypeList = raidType.getFactionEntityTypes();
		this.center = center;
	}
	
	public Raid(ServerLevel level, CompoundTag tag) {
		this.level = level;
		this.raidData = new RaidData(tag);
		this.raidType = DatapackRegistries.RAID_TYPES.getValue(new ResourceLocation(tag.getString("RaidType")));
		this.bossBar = new ServerBossEvent(this.raidType.getNameComponent(), this.raidType.getColor(), this.raidType.getOverlay());
		this.bossBar.setVisible(true);
		this.raiderTypeList = raidType.getFactionEntityTypes();
		this.center = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
		this.badOmenLevel = tag.getInt("BadOmenLevel");
		ListTag listTag = tag.getList("RaiderTypeCountMap", Tag.TAG_COMPOUND);
		listTag.forEach(tag0 -> {
			CompoundTag tag1 = (CompoundTag) tag0;
			ResourceLocation id = new ResourceLocation(tag1.getString("RaiderType"));
			RaiderType raiderType = DatapackRegistries.RAIDER_TYPES.getValue(id);
			int count = tag1.getInt("Count");
			if (raiderType != null) {
				this.raiderTypeCountMap.put(raiderType, count);
			} else {
				RaidCraft.LOGGER.error("Not found raiderType id:{}", id);
			}
		});
	}
	
	/**
	 * 更新中心位置
	 */
	protected void updateCenter() {
	
	}
	
	/**
	 * 吸收不祥之兆Buff 启动阶段 更新玩家后调用
	 */
	protected void absorbBadOmen() {
		int badOmenLevel = 0;
		Iterator<ServerPlayer> iterator = getPlayers().iterator();
		while (iterator.hasNext()) {
			ServerPlayer player = iterator.next();
			IRaidInteraction raidInteraction = IRaidInteraction.get(player).get();
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
	 * @return
	 */
	@Override
	public Collection<ServerPlayer> getPlayers() {
		return this.bossBar.getPlayers();
	}
	
	protected void spawnWave() {
		spawnedWave++;
		BlockPos pos = findRandomPos(1, 20);
		
		if (pos == null) {
			RaidCraft.LOGGER.error("Failed to find a valid position.");
			return;
		}
		
		spawnGroup(strength, pos);
		
		if (raidType.getWaveSoundEvent() != null) {
			playSound(center, raidType.getWaveSoundEvent());
		} else {
			RaidCraft.LOGGER.error("Wave sound event is null. No sound will be played.");
		}
	}
	
	/**
	 * 生成一组生物
	 *
	 * @param targetStrength 这组的目标强度
	 * @param spawnPos       位置
	 */
	protected void spawnGroup(int targetStrength, BlockPos spawnPos) {
		
		// 必须生成的 不累计强度值
		
		List<RaiderType> mustSpawnRaiderTypeList = getMustSpawnRaiderTypeList();
		
		if (!mustSpawnRaiderTypeList.isEmpty()) {
			for (RaiderType raiderType : mustSpawnRaiderTypeList) {
				int count = 0;
				while (count < raiderType.getMinSpawned()) {
					IRaider raider = raiderType.spawn(level, spawnPos.offset(this.level.getRandom().nextInt(11) - 5, 0, this.level.getRandom().nextInt(11) - 5));
					if(raider != null){
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
					IRaider raider = raiderType.spawn(level, spawnPos.offset(this.level.getRandom().nextInt(11) - 5, 0, this.level.getRandom().nextInt(11) - 5));
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
	 * @return
	 */
	protected @NotNull List<RaiderType> getMustSpawnRaiderTypeList() {
		return raiderTypeList.stream()
							 .filter(factionEntityType -> factionEntityType.getMinWave() <= spawnedWave && factionEntityType.getMaxWave() >= spawnedWave)
							 .filter(factionEntityType -> {
								 int count = raiderTypeCountMap.getOrDefault(factionEntityType, 0);
								 return count < factionEntityType.getMinSpawned();
							 })
							 .toList();
	}
	
	/**
	 * 本轮次允许生成的RaiderType
	 *
	 * @return
	 */
	protected @NotNull List<RaiderType> getSpawnableRaiderTypeList() {
		return raiderTypeList.stream()
							 .filter(factionEntityType -> factionEntityType.getMinWave() <= spawnedWave && factionEntityType.getMaxWave() >= spawnedWave)
							 .filter(factionEntityType -> {
								 int count = raiderTypeCountMap.getOrDefault(factionEntityType, 0);
								 return count < factionEntityType.getMaxSpawned();
							 })
							 .toList();
	}
	
	@Override
	public void joinRaid(Mob mob) {
		Optional<IRaider> optional = IRaider.getRaider(mob);
		if(optional.isEmpty()){
			return;
		}else {
			joinRaid(optional.get());
		}
	}
	
	@Override
	public void joinRaid(IRaider raider) {
		raiders.add(raider);
		raider.setRaid(this);
		if (leaderRaider == null) {
			setLeaderRaider(raider);
		}
		
		// 计数RaiderType
		if (raider.getRaiderType() == null) return;
		if (raiderTypeCountMap.containsKey(raider.getRaiderType())) {
			raiderTypeCountMap.put(raider.getRaiderType(), raiderTypeCountMap.get(raider.getRaiderType()) + 1);
		} else {
			raiderTypeCountMap.put(raider.getRaiderType(), 1);
		}
	}
	
	protected void updateBossBar(float progress) {
		bossBar.setProgress(Mth.clamp(progress, 0.0F, 1.0F));
	}
	
	/**
	 * 负责更新Player 加入与离开Raid
	 */
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
				if (raider.getMob().blockPosition().distSqr(getCenter()) < IRaid.RAID_REMOVAL_THRESHOLD_SQR) {
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
		raidData.setTotalHealth(totalHealth);
		raidData.setHealth(health);
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
		return raidData.getHealth();
	}
	
	public float getTotalHealth() {
		return raidData.getTotalHealth();
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
	
	@Override
	public boolean canJoinRaid(Mob mob) {
		IRaider raider = IRaider.getRaider(mob).get();
		if (!raider.canJoinRaid()) {
			return false;
		}
		IFactionEntity factionEntity = IFactionEntity.get(mob).get();
		if (factionEntity.getFaction() != this.raidType.getFaction()) {
			return false;
		}
		return true;
	}
	
	@Override
	public void setBadOmenLevel(int level) {
		this.badOmenLevel = level;
	}
	
	@Override
	public int getBadOmenLevel() {
		return badOmenLevel;
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
	
	public void setLeaderRaider(Mob mob){
		Optional<IRaider> optional = IRaider.getRaider(mob);
		if(optional.isEmpty()){
			return;
		}else {
			setLeaderRaider(optional.get());
		}
	}
	
	@Override
	public void setLeaderRaider(IRaider raider) {
		raider.setLeader(true);
		leaderRaider = raider;
	}
	
	@Override
	public void tick() {
		if (isStopped()) return;
		
		if (raidData.getActiveTicks() >= 72000 || level.getDifficulty() == Difficulty.PEACEFUL) {
			stop();
			return;
		}
		
		updatePlayer();
		// 启动阶段
		if (!isStarted()) {
			if (raidData.getRaidCooldown() == 0) {
				raidData.setRaidCooldown(300);
				handleStartRaid();
			} else {
				raidData.setRaidCooldown(raidData.getRaidCooldown() - 1);
				updateBossBar((300 - (float) raidData.getRaidCooldown()) / 300);
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
						if (raidData.getRaidCooldown() == 0) {
							raidData.setRaidCooldown(300);
							raiders.clear(); // 生成之前清理旧的实体
							leaderRaider = null;
							spawnWave();
						} else {
							raidData.setRaidCooldown(raidData.getRaidCooldown() - 1);
							updateBossBar((300 - (float) raidData.getRaidCooldown()) / 300);
						}
						bossBar.setName(raidType.getNameComponent());
					} else {
						victory();
					}
				}
			} else {
				if (bossBar.getPlayers().isEmpty()) {
					if (raidData.getRaidCooldown() == 0) {
						defeat();
					} else {
						raidData.setRaidCooldown(raidData.getRaidCooldown() - 1);
					}
				} else {
					raidData.setRaidCooldown(300);
				}
				if (getNumOfAliveRaiders() <= 3) {
					if (raidData.getActiveTicks() % 200 == 0) {
						getAliveRaiders().forEach(raider -> raider.getMob().addEffect(new MobEffectInstance(MobEffects.GLOWING, 200)));
					}
					bossBar.setName(raidType.getNameComponent().copy().append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", getNumOfAliveRaiders())));
				}
				updateBossBar(getHealth() / getTotalHealth());
			}
		}
		// 结束阶段
		else {
			if (raidData.getCelebrateTicks() == 0) {
				stop();
			} else {
				raidData.setCelebrateTicks(raidData.getCelebrateTicks() - 1);
			}
		}
		raidData.setActiveTicks(raidData.getActiveTicks() + 1);
	}
	
	/**
	 * 处理开始阶段
	 */
	protected void handleStartRaid() {
		absorbBadOmen();
		computeStrength();
		start();
	}
	
	/**
	 * 计算强度 波次
	 */
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
			double d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x); // 偏移
			double d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z); // 偏移
			if (d0 <= 64.0D || collection.contains(serverplayer)) {
				serverplayer.connection.send(new ClientboundSoundPacket(soundEvent, SoundSource.NEUTRAL, d1, serverplayer.getY(), d2, volume, pitch, seed));
			}
		}
		
	}
	
	@Override
	public int getSpawnedWave() {
		return spawnedWave;
	}
	
	@Override
	public int getTotalWave() {
		return totalWave;
	}
	
	@Override
	public IRaider getLeader() {
		return leaderRaider;
	}
	
	@Override
	public void start() {
		raidData.setStarted(true);
	}
	
	@Override
	public void stop() {
		raidData.setStopped(true);
		bossBar.setVisible(false);
		bossBar.removeAllPlayers();
	}
	
	@Override
	public void victory() {
		raidData.setVictory(true);
		updateBossBar(0.0F);
		bossBar.setName(raidType.getVictoryComponent());
		playSound(center, raidType.getVictorySoundEvent());
	}
	
	@Override
	public void defeat() {
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
	public Faction getFaction() {
		return raidType.getFaction();
	}
	
	@Override
	public ItemStack getBanner() {
		return raidType.getFaction().getBanner();
	}
	
	@Override
	public CompoundTag save() {
		CompoundTag tag = raidData.save();
		ResourceLocation raidTypeKey = DatapackRegistries.RAID_TYPES.getKey(this.raidType);
		if (raidTypeKey != null) {
			tag.putString("RaidType", raidTypeKey.toString());
		} else {
			RaidCraft.LOGGER.error("RaidType's key is null");
		}
		tag.putInt("X", this.center.getX());
		tag.putInt("Y", this.center.getY());
		tag.putInt("Z", this.center.getZ());
		tag.putInt("BadOmenLevel", this.badOmenLevel);
		ListTag listTag = new ListTag();
		this.raiderTypeCountMap.forEach((raiderType, integer) -> {
			CompoundTag tag1 = new CompoundTag();
			ResourceLocation raiderTypesKey = DatapackRegistries.RAIDER_TYPES.getKey(raiderType);
			if (raiderTypesKey != null) {
				tag1.putString("RaiderType", raiderTypesKey.toString());
				tag1.putInt("Count", integer);
				listTag.add(tag1);
			} else {
				RaidCraft.LOGGER.error("RaiderType's key is null");
			}
		});
		tag.put("RaiderTypeCountMap", listTag);
		return tag;
	}
}