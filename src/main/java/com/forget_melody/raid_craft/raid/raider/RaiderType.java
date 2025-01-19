package com.forget_melody.raid_craft.raid.raider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class RaiderType {
	public static final Codec<RaiderType> CODEC = RecordCodecBuilder.create(raiderTypeInstance -> raiderTypeInstance.group(
			Codec.INT.fieldOf("strength").forGetter(RaiderType::getStrength),
			ResourceLocation.CODEC.fieldOf("entity_type").forGetter(RaiderType::getEntityTypeLocation)
	).apply(raiderTypeInstance, RaiderType::new));
	
	private final int strength;
	private final ResourceLocation entityType;
	
	public RaiderType(int strength, ResourceLocation entityType) {
		this.strength = strength;
		this.entityType = entityType;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public ResourceLocation getEntityTypeLocation(){
		return entityType;
	}
	
	public Optional<EntityType<?>> getEntityType() {
		return Optional.ofNullable(ForgeRegistries.ENTITY_TYPES.getValue(entityType));
	}
}