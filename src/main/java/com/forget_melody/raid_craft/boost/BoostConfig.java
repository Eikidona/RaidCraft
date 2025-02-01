package com.forget_melody.raid_craft.boost;

import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoostConfig {
	public static final BoostConfig DEFAULT = new BoostConfig(List.of(), List.of(), List.of(), List.of());
	public static final Codec<BoostConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.listOf().optionalFieldOf("mandatory", new ArrayList<>()).forGetter(BoostConfig::getMandatoryLocations),
			ResourceLocation.CODEC.listOf().optionalFieldOf("whitelist", new ArrayList<>()).forGetter(BoostConfig::getWhitelistLocations),
			ResourceLocation.CODEC.listOf().optionalFieldOf("blacklist", new ArrayList<>()).forGetter(BoostConfig::getBlacklistLocations),
			Codec.mapPair(ResourceLocation.CODEC.fieldOf("boost"), BoostRarity.CODEC.fieldOf("rarity")).codec().listOf().optionalFieldOf("rarity_overrides", new ArrayList<>()).forGetter(BoostConfig::getRarityOverridesLocations)
	).apply(instance, BoostConfig::new));
	
	private final List<ResourceLocation> mandatoryLocations;
	private final List<ResourceLocation> whitelistLocations;
	private final List<ResourceLocation> blacklistLocations;
	private final List<Pair<ResourceLocation, BoostRarity>> rarityOverridesLocations;
	
	public BoostConfig(List<ResourceLocation> mandatoryLocations,
					   List<ResourceLocation> whitelistLocations,
					   List<ResourceLocation> blacklistLocations,
					   List<Pair<ResourceLocation, BoostRarity>> rarityOverridesLocations
	) {
		this.mandatoryLocations = mandatoryLocations;
		this.whitelistLocations = whitelistLocations;
		this.blacklistLocations = blacklistLocations;
		this.rarityOverridesLocations = rarityOverridesLocations;
	}
	
	public List<ResourceLocation> getMandatoryLocations() {
		return mandatoryLocations;
	}
	
	public List<ResourceLocation> getWhitelistLocations() {
		return whitelistLocations;
	}
	
	public List<ResourceLocation> getBlacklistLocations() {
		return blacklistLocations;
	}
	
	public List<Pair<ResourceLocation, BoostRarity>> getRarityOverridesLocations() {
		return rarityOverridesLocations;
	}
	
	public List<IBoost> getMandatoryBoosts(){
		return getMandatoryLocations().stream().map(DataPackRegistries.BOOSTS::getValue).filter(Objects::nonNull).toList();
	}
	
	public List<IBoost> getWhitelistBoosts(){
		return getWhitelistLocations().stream().map(DataPackRegistries.BOOSTS::getValue).filter(Objects::nonNull).toList();
	}
	
	public List<IBoost> getBlacklistBoosts(){
		return getBlacklistLocations().stream().map(DataPackRegistries.BOOSTS::getValue).filter(Objects::nonNull).toList();
	}
	
	public List<Pair<IBoost, BoostRarity>> getRarityOverrides(){
		return getRarityOverridesLocations().stream().map(pair -> new Pair<>(DataPackRegistries.BOOSTS.getValue(pair.getFirst()), pair.getSecond())).filter(pair -> Objects.nonNull(pair.getFirst())).toList();
	}
	
}
