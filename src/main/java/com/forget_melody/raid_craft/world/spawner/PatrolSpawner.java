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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PatrolSpawner implements CustomSpawner {
	private int cooldownTicks;
	
	@Override
	public int tick(@NotNull ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
		// 非生成敌对怪物 退出
		if (!spawnEnemies) {
			return 0;
		}
		// 当天时间小于配置的最早生成时间
		if (level.getDayTime() < Config.PATROL_DAYTIME_BEFORE_SPAWNING.get()) {
			return 0;
		}
		// 概率判断
		RandomSource random = level.getRandom();
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
		int numPlayer = level.players().size();
		ServerPlayer player = level.players().stream().filter(player1 -> !player1.isSpectator()).toList().get(random.nextInt(numPlayer));
		
		// 如果距离村庄很近则退出
		if (level.isCloseToVillage(player.blockPosition(), 2)) {
			return 0;
		}
		// 选择位置 如果区块未加载则退出
		int x = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
		int z = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
		BlockPos.MutableBlockPos blockPos$mutable = player.blockPosition().mutable().move(x, 0, z);
		if (!level.hasChunksAt(blockPos$mutable.getX() - 10, blockPos$mutable.getY() - 10, blockPos$mutable.getZ() - 10, blockPos$mutable.getX() + 10, blockPos$mutable.getY() + 10, blockPos$mutable.getZ() + 10)) {
			return 0;
		}
		Holder<Biome> holder = level.getBiome(blockPos$mutable);
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
		List<Faction> factions = DataPackRegistries.FACTIONS.getValues().stream().filter(faction1 -> faction1 != Faction.DEFAULT).toList();
		Faction faction = factions.get(level.random.nextInt(factions.size()));
		if (faction == null || faction.getPatrolConfig().getPatrollerTypes().isEmpty()) {
			return 0;
		}
		IPatrolManager manager = IPatrolManager.get(level);
		Patrol patrol = manager.createPatrol(faction, blockPos$mutable);
		if (patrol == null) {
			return 0;
		}
		return patrol.getNumPatrollers();
	}
}
