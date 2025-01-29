package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
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

public class RaiderType {
	public static final RaiderType DEFAULT = new RaiderType(new ResourceLocation(RaidCraft.MODID, "default"), 5, 0, 999, 0, 999);
	
	public static final Codec<RaiderType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("faction_entity_type").forGetter(RaiderType::getFactionEntityTypeLocation),
			Codec.INT.optionalFieldOf("weight", 5).forGetter(RaiderType::getWeight),
			Codec.INT.optionalFieldOf("minWave", 0).forGetter(RaiderType::getMinWave),// 最小生成波次
			Codec.INT.optionalFieldOf("maxWave", 999).forGetter(RaiderType::getMaxWave), // 最大生成波次
			Codec.INT.optionalFieldOf("minSpawned", 0).forGetter(RaiderType::getMinSpawned), // 波次内最少生成数
			Codec.INT.optionalFieldOf("maxSpawned", 999).forGetter(RaiderType::getMaxSpawned) // 波次内最大生成数
	).apply(instance, RaiderType::new));
	
	private final ResourceLocation factionEntityType;
	private final int weight;
	private final int minWave;
	private final int maxWave;
	private final int minSpawned;
	private final int maxSpawned;
	
	public RaiderType(ResourceLocation factionEntityType, int weight, int minWave, int maxWave, int minSpawned, int maxWaveSpawned) {
		this.factionEntityType = factionEntityType;
		this.weight = weight;
		this.minWave = minWave;
		this.maxWave = maxWave;
		this.minSpawned = minSpawned;
		this.maxSpawned = maxWaveSpawned;
	}
	
	public FactionEntityType getFactionEntityType() {
		return DataPackRegistries.Faction_ENTITY_TYPES.getValue(getFactionEntityTypeLocation());
	}
	
	public ResourceLocation getFactionEntityTypeLocation() {
		return factionEntityType;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public int getStrength() {
		return getFactionEntityType().getStrength();
	}
	
	public int getMinWave() {
		return minWave;
	}
	
	public int getMinSpawned() {
		return minSpawned;
	}
	
	public int getMaxWave() {
		return maxWave;
	}
	
	public int getMaxSpawned() {
		return maxSpawned;
	}
	
	@Nullable
	public IRaider spawn(ServerLevel level, BlockPos pos) {
		FactionEntityType factionEntityType1 = getFactionEntityType();
		if(factionEntityType1 == null){
			RaidCraft.LOGGER.error("Not found FactionEntityType id {}", getFactionEntityTypeLocation());
			return null;
		}
		IFactionEntity factionEntity = factionEntityType1.spawn(level, pos, MobSpawnType.EVENT);
		if(factionEntity == null){
			return null;
		}
		Optional<IRaider> optional = IRaider.get(factionEntity.getMob());
		if(optional.isEmpty()){
			RaidCraft.LOGGER.error("IRaider is null by FactionEntityType {}", getFactionEntityTypeLocation());
			return null;
		}
		return optional.get();
	}
}
