package com.forget_melody.raid_craft.faction.faction_entity_type;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.boost.BoostConfig;
import com.forget_melody.raid_craft.boost.IBoost;
import com.forget_melody.raid_craft.capabilities.boost_entity.BoostEntityHooks;
import com.forget_melody.raid_craft.capabilities.boost_entity.IBoostEntity;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FactionEntityType {
	public static final Codec<FactionEntityType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("entity_type").forGetter(FactionEntityType::getEntityTypeLocation),
			BoostConfig.CODEC.optionalFieldOf("boost_config", BoostConfig.DEFAULT).forGetter(FactionEntityType::getBoostConfig),
			CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(FactionEntityType::getCompoundTag),
			Codec.INT.optionalFieldOf("strength", 5).forGetter(FactionEntityType::getStrength)
	).apply(instance, FactionEntityType::new));
	
	private final ResourceLocation entityTypeLocation;
	private final BoostConfig boostConfig;
	private final CompoundTag tag;
	private final int strength;
	
	public FactionEntityType(ResourceLocation entityTypeLocation, BoostConfig boostConfig, CompoundTag tag, int strength) {
		this.entityTypeLocation = entityTypeLocation;
		this.boostConfig = boostConfig;
		this.tag = tag;
		this.strength = strength;
	}
	
	@Nullable
	public EntityType<?> getEntityType() {
		return ForgeRegistries.ENTITY_TYPES.getValue(getEntityTypeLocation());
	}
	
	@Nullable
	public IFactionEntity spawn(ServerLevel level, BlockPos pos, MobSpawnType mobSpawnType) {
		EntityType<?> entityType = getEntityType();
		if (entityType == null) {
			RaidCraft.LOGGER.error("Not found entity type id {}", getEntityTypeLocation());
			return null;
		}
		Faction faction = DataPackRegistries.FACTIONS.getFaction(entityType);
		List<IBoost> boosts = faction.getBoostConfig().getApplyBoosts(strength);
		boosts.addAll(getBoostConfig().getApplyBoosts(strength));
		RaidCraft.LOGGER.info("FactionEntityType Boosts: {}", boosts.size());
		
		IBoostEntity boostEntity = BoostEntityHooks.spawn(level, pos, entityType, MobSpawnType.EVENT, boosts);
		if (boostEntity != null) {
			return IFactionEntity.get(boostEntity.getMob());
		}
		
		RaidCraft.LOGGER.error("entity type {} is not a mob!", getEntityTypeLocation());
		return null;
	}
	
	public BoostConfig getBoostConfig() {
		return boostConfig;
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
