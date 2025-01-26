package com.forget_melody.raid_craft.world.spawner;

import com.forget_melody.raid_craft.capabilities.patrol_manager.IPatrolManager;
import com.forget_melody.raid_craft.config.Config;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.patrol.Patrol;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.Optional;

public class PatrolSpawner implements CustomSpawner {
	private int cooldownTicks;
	
	@Override
	public int tick(ServerLevel pLevel, boolean pSpawnEnemies, boolean pSpawnFriendlies) {
		// 非生成敌对怪物 退出
		if (!pSpawnEnemies) {
			return 0;
		}
		// 当天时间小于配置的最早生成时间
		if (pLevel.getDayTime() < Config.PATROL_DAYTIME_BEFORE_SPAWNING.get()) {
			return 0;
		}
		// 概率判断
		RandomSource random = pLevel.getRandom();
		if (random.nextFloat() <= Config.PATROL_SPAWN_CHANCE_ON_SPAWN_ATTEMPT.get()) {
			return 0;
		}
		// 触发冷却 冷却大于0退出 否则更新冷却时间
		--cooldownTicks;
		if (cooldownTicks > 0) {
			return 0;
		}
		this.cooldownTicks += Config.PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get() + random.nextInt(Config.PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get());
		
		// 随机选择一个非观察者玩家
		int numPlayer = pLevel.players().size();
		ServerPlayer player = pLevel.players().stream().filter(player1 -> !player1.isSpectator()).toList().get(random.nextInt(numPlayer));
		
		// 如果距离村庄很近则退出
		if (pLevel.isCloseToVillage(player.blockPosition(), 2)) {
			return 0;
		}
		// 选择位置 如果区块未加载则退出
		int x = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
		int z = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
		BlockPos.MutableBlockPos blockpos$mutable = player.blockPosition().mutable().move(x, 0, z);
		if (!pLevel.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
			return 0;
		}
		Holder<Biome> holder = pLevel.getBiome(blockpos$mutable);
		Biome biome = holder.get();
		ITagManager<Biome> biomeITagManager = ForgeRegistries.BIOMES.tags();
		ITag<Biome> biomeTag = null;
		if (biomeITagManager != null) {
			biomeTag = ForgeRegistries.BIOMES.tags().getTag(TagKey.create(ForgeRegistries.BIOMES.getRegistryKey(), new ResourceLocation("forge", "is_mushroom")));
		}
		// 如果生物群系是具有蘑菇岛标签则退出
		if (biomeTag != null && biomeTag.contains(biome)) {
			return 0;
		}
		Faction faction = DataPackRegistries.FACTIONS.getRandomValue();
		if (faction == null) {
			return 0;
		}
//		Faction patrolType = DataPackRegistries.PATROL_TYPES.getValueByReMapKey(faction);
//		if (patrolType == null) {
//			return 0;
//		}
		Optional<IPatrolManager> optional = IPatrolManager.get(pLevel);
		if (optional.isEmpty()) {
			return 0;
		}
		IPatrolManager manager = optional.get();
		Patrol patrol = manager.createPatrol(faction, blockpos$mutable);
		if(patrol == null){
			return 0;
		}
		return patrol.getNumPatrollers();
		// 生成巡逻队
//		return patrolType.spawn(pLevel, blockpos$mutable).size();
	}

//	public static int spawnPatrol(ServerLevel pLevel, RandomSource random, PatrolType patrolType, BlockPos blockpos) {
//		BlockPos.MutableBlockPos pos = blockpos.mutable();
//		boolean leader = true;
//		int numCurrentSpawn = 0;
//		int numTotalSpawn = (int) Math.ceil(pLevel.getCurrentDifficultyAt(pos).getEffectiveDifficulty()) + 1;
//		IWeightTable<PatrollerType> weightTable = IWeightTable.of(patrolType.getPatrollerTypeList().stream().map(patrollerType -> IWeightEntry.of(patrollerType, patrollerType.getWeight())).toList());
//		while (numCurrentSpawn < numTotalSpawn) {
//			numCurrentSpawn++;
//			pos.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY());
//			pos.setX(pos.getX() + random.nextInt(5) - random.nextInt(5));
//			pos.setZ(pos.getZ() + random.nextInt(5) - random.nextInt(5));
//			weightTable.getElement().spawn(pLevel, pos);
//			if (leader) {
//				leader = false;
//			}
//		}
//		return numCurrentSpawn;
//	}

//	private static boolean spawnPatroller(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, boolean pLeader, Faction faction) {
//		BlockState blockstate = pLevel.getBlockState(pPos);
//		List<Pair<FactionEntityType, Integer>> weightMap = faction.getWeightMap();
//		List<Pair<FactionEntityType, Integer>> filtered = weightMap.stream().filter(pair -> (pLeader && pair.getFirst().canBeBannerHolder()) || (!pLeader && pair.getFirst().getRank().equals(FactionEntityType.FactionRank.SOLDIER))).collect(Collectors.toList());
//		FactionEntityType factionEntityType = GeneralUtils.getRandomEntry(filtered, pRandom);
//		EntityType<? extends Mob> entityType = (EntityType<? extends Mob>) ENTITIES.getValue(factionEntityType.getFactionEntityTypeLocation());
//		if (!WorldEntitySpawner.isValidEmptySpawnBlock(pLevel, pPos, blockstate, blockstate.getFluidState(), entityType)) {
//			return false;
//		} else if (!(pLevel.getBrightness(LightType.BLOCK, pPos) <= 8 && pLevel.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(entityType, pLevel, SpawnReason.PATROL, pPos, pRandom))) {
//			return false;
//		} else {
//			Mob entity = (Mob) factionEntityType.createEntity(pLevel, faction, pPos, pLeader, SpawnReason.PATROL);
//			if (entity != null) {
//				faction.getBoostConfig().getMandatoryBoosts().forEach(boost -> boost.apply(entity));
//				factionEntityType.getBoostConfig().getMandatoryBoosts().forEach(boost -> boost.apply(entity));
//				IPatroller patrollerCap = PatrollerHelper.getPatrollerCapability(entity);
//				if (pLeader) {
//					patrollerCap.setPatrolLeader(true);
//					patrollerCap.findPatrolTarget();
//				}
//				patrollerCap.setPatrolling(true);
//				return true;
//			} else {
//				return false;
//			}
//		}
//	}
}
