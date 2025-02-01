package com.forget_melody.raid_craft.capabilities.boost_entity;

import com.forget_melody.raid_craft.boost.BoostType;
import com.forget_melody.raid_craft.boost.IBoost;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoostEntityHooks {
	/**
	 * 对EntityType.spawn()方法的包装 其可以为生成的实体应用某单次生效的Boost
	 * @param level - ServerLevel
	 * @param pos - BlockPos
	 * @param entityType - EntityType
	 * @param mobSpawnType - MobSpawnType
	 * @param boosts - BoostList
	 * @return IBoostEntity
	 */
	@Nullable
	public static IBoostEntity spawn(ServerLevel level, BlockPos pos, EntityType<?> entityType, MobSpawnType mobSpawnType, List<IBoost> boosts) {
		Entity entity = entityType.spawn(level, pos, mobSpawnType);
		if(entity instanceof Mob mob){
			IBoostEntity boostEntity = IBoostEntity.get(mob);
			boostEntity.setBoost(boosts);
			applyEquipmentBoost(boostEntity);
			return boostEntity;
		}
		return null;
	}
	
	private static void applyEquipmentBoost(IBoostEntity boostEntity){
		boostEntity.getBoosts().forEach(boost -> {
			if(boost.getType() == BoostType.EQUIPMENT){
//				RaidCraft.LOGGER.info("ApplyEquipmentBoost");
				boost.apply(boostEntity.getMob());
			}
		});
	}
}
