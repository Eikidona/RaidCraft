package com.forget_melody.raid_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class MountBoost implements IBoost{
	public static final Codec<MountBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("entity_type").forGetter(MountBoost::getEntityTypeLocation),
			Codec.INT.optionalFieldOf("strength", 5).forGetter(MountBoost::getStrength)
	).apply(instance, MountBoost::new));
	
	private final ResourceLocation entityTypeLocation;
	private final int strength;
	
	public MountBoost(ResourceLocation entityTypeLocation, int strength) {
		this.entityTypeLocation = entityTypeLocation;
		this.strength = strength;
	}
	
	private Optional<EntityType<?>> getEntityType(){
		return Optional.ofNullable(ForgeRegistries.ENTITY_TYPES.getValue(entityTypeLocation));
	}
	
	public ResourceLocation getEntityTypeLocation() {
		return entityTypeLocation;
	}
	
	@Override
	public BoostType getType() {
		return BoostType.MOUNT;
	}
	
	@Override
	public void apply(Mob mob) {
		Optional<EntityType<?>> optional = getEntityType();
		optional.ifPresent(entityType -> {
			Entity entity = entityType.spawn((ServerLevel) mob.level(), mob.blockPosition(), MobSpawnType.EVENT);
			if(entity instanceof Mob mount){
				mob.startRiding(mount, false);
			}
		});
	}
	
	@Override
	public int getStrength() {
		return strength;
	}
}
