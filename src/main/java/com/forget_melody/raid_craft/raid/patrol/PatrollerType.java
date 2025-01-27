package com.forget_melody.raid_craft.raid.patrol;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PatrollerType {
	public static final PatrollerType DEFAULT = new PatrollerType(new ResourceLocation(RaidCraft.MODID, "default"), 5);
	
	public static final Codec<PatrollerType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			net.minecraft.resources.ResourceLocation.CODEC.fieldOf("faction_entity_type").forGetter(PatrollerType::getFactionEntityTypeLocation),
			Codec.INT.optionalFieldOf("weight", 5).forGetter(PatrollerType::getWeight)
	).apply(instance, PatrollerType::new));
	
	private final net.minecraft.resources.ResourceLocation factionEntityTypeLocation;
	private final int weight;
	
	public PatrollerType(net.minecraft.resources.ResourceLocation factionEntityTypeLocation, int weight) {
		this.factionEntityTypeLocation = factionEntityTypeLocation;
		this.weight = weight;
	}
	
	public net.minecraft.resources.ResourceLocation getFactionEntityTypeLocation() {
		return factionEntityTypeLocation;
	}
	
	@Nullable
	public FactionEntityType getFactionEntityType(){
		return DataPackRegistries.Faction_ENTITY_TYPES.getValue(getFactionEntityTypeLocation());
	}
	
	public int getWeight() {
		return weight;
	}
	
	@Nullable
	public IPatroller spawn(ServerLevel level, BlockPos pos, Patrol patrol) {
		FactionEntityType factionEntityType = getFactionEntityType();
		if(factionEntityType == null){
			RaidCraft.LOGGER.error("Not found FactionEntityType id {}", getFactionEntityTypeLocation());
			return null;
		}
		IFactionEntity factionEntity = factionEntityType.spawn(level, pos, MobSpawnType.PATROL);
		if(factionEntity == null){
			return null;
		}
		Optional<IPatroller> optional = IPatroller.get(factionEntity.getMob());
		if(optional.isEmpty()){
			RaidCraft.LOGGER.error("IPatroller is null by FactionEntityType id {}", getFactionEntityTypeLocation());
			return null;
		}
		return optional.get();
	}
}
