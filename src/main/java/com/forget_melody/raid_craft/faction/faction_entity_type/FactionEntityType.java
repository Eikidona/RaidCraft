package com.forget_melody.raid_craft.faction.faction_entity_type;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FactionEntityType {
	public static final Codec<FactionEntityType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("entity_type").forGetter(FactionEntityType::getEntityTypeLocation),
			CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(FactionEntityType::getCompoundTag),
			Codec.INT.optionalFieldOf("strength", 5).forGetter(FactionEntityType::getStrength)
	).apply(instance, FactionEntityType::new));
	
	private final ResourceLocation entityTypeLocation;
	private final CompoundTag tag;
	private final int strength;
	
	public FactionEntityType(ResourceLocation entityTypeLocation, CompoundTag tag, int strength) {
		this.entityTypeLocation = entityTypeLocation;
		this.tag = tag;
		this.strength = strength;
	}
	
	@Nullable
	public EntityType<?> getEntityType(){
		return ForgeRegistries.ENTITY_TYPES.getValue(getEntityTypeLocation());
	}
	
	@Nullable
	public IFactionEntity spawn(ServerLevel level, BlockPos pos, MobSpawnType mobSpawnType){
		EntityType<?> entityType = getEntityType();
		if(entityType == null){
			RaidCraft.LOGGER.error("Not found entity type id {}", getEntityTypeLocation());
			return null;
		}
		Entity entity = entityType.spawn(level, pos, mobSpawnType);
		if(!(entity instanceof Mob)){
			RaidCraft.LOGGER.error("entity type {} is not a mob!", getEntityTypeLocation());
			return null;
		}
		Optional<IFactionEntity> optional = IFactionEntity.get((Mob) entity);
		if(optional.isEmpty()){
			RaidCraft.LOGGER.error("FactionEntity is null by entity type {}", getEntityTypeLocation());
			return null;
		}
		return optional.get();
	}
	
	public ResourceLocation getEntityTypeLocation() {
		return entityTypeLocation;
	}
	
	public CompoundTag getCompoundTag() {
		return tag;
	}
	
	public int getStrength() {
		return strength;
	}
}
