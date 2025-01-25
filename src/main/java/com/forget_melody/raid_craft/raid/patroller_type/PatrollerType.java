package com.forget_melody.raid_craft.raid.patroller_type;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.raid.patrol_type.PatrolType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PatrollerType {
	public static final Codec<PatrollerType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("entity_type").forGetter(PatrollerType::getEntityType),
			Codec.INT.optionalFieldOf("weight", 5).forGetter(PatrollerType::getWeight)
	).apply(instance, PatrollerType::new));
	
	private final ResourceLocation entityType;
	private final int weight;
	
	public PatrollerType(ResourceLocation entityType, int weight) {
		this.entityType = entityType;
		this.weight = weight;
	}
	
	public ResourceLocation getEntityType() {
		return entityType;
	}
	
	public int getWeight() {
		return weight;
	}
	
	@Nullable
	public IPatroller spawn(ServerLevel level, BlockPos pos, PatrolType patrolType) {
		EntityType<?> entityType1 = ForgeRegistries.ENTITY_TYPES.getValue(this.entityType);
		if (entityType1 == null) {
			RaidCraft.LOGGER.error("Not found Entity type id {}", this.entityType);
			return null;
		}
		Entity entity = entityType1.spawn(level, pos, MobSpawnType.PATROL);
		if(entity instanceof Mob){
			Optional<IPatroller> optional = IPatroller.get((Mob) entity);
			if(optional.isEmpty()){
				RaidCraft.LOGGER.error("IPatroller is null by entity {}", this.entityType);
				return null;
			}
			IPatroller patroller = optional.get();
			return patroller;
		}
		return null;
	}
}
