package com.forget_melody.raid_craft.boost;

import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

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
	
	public List<IBoost> getMandatoryBoosts() {
		return getMandatoryLocations().stream().map(DataPackRegistries.BOOSTS::getValue).filter(Objects::nonNull).toList();
	}
	
	public List<IBoost> getWhitelistBoosts() {
		return getWhitelistLocations().stream().map(DataPackRegistries.BOOSTS::getValue).filter(Objects::nonNull).toList();
	}
	
	public List<IBoost> getBlacklistBoosts() {
		return getBlacklistLocations().stream().map(DataPackRegistries.BOOSTS::getValue).filter(Objects::nonNull).toList();
	}
	
	public List<Pair<IBoost, BoostRarity>> getRarityOverrides() {
		return getRarityOverridesLocations().stream().map(pair -> new Pair<>(DataPackRegistries.BOOSTS.getValue(pair.getFirst()), pair.getSecond())).filter(pair -> Objects.nonNull(pair.getFirst())).toList();
	}
	
	public ArrayList<IBoost> getApplyBoosts(int targetStrength) {
		ArrayList<IBoost> boosts = new ArrayList<>(getMandatoryBoosts());
		
		List<Pair<IBoost, BoostRarity>> pairs;
		if (!getRarityOverrides().isEmpty()) {
			// 白名单
			if (!getWhitelistBoosts().isEmpty()) {
				pairs = getRarityOverrides().stream().filter(pair -> getWhitelistBoosts().contains(pair.getFirst())).toList();
			}
			// 黑名单
			else if (!getBlacklistBoosts().isEmpty()) {
				pairs = getRarityOverrides().stream().filter(pair -> !getBlacklistBoosts().contains(pair.getFirst())).toList();
			}
			// 无名单模式
			else {
				pairs = getRarityOverrides();
			}
			// 权重抽取 过滤强度 每个类型只抽取一个
			if (!pairs.isEmpty()) {
				pairs = pairs.stream().filter(pair -> pair.getFirst().getStrength() <= targetStrength).toList();
				List<BoostType> types = Arrays.asList(BoostType.values());
				Collections.shuffle(types);
				for(BoostType type: types){
					List<Pair<IBoost, BoostRarity>> list = pairs.stream().filter(pair -> pair.getFirst().getType() == type).toList();
					if(list.isEmpty()){
						continue;
					}
					int totalWeight = list.stream().mapToInt(pair -> pair.getSecond().getWeight()).sum();
					int currentStrength = 0;
					while (currentStrength < targetStrength) {
						int randomWeight = (int) (Math.random() * totalWeight);
						int currentWeight = 0;
						for (Pair<IBoost, BoostRarity> pair : list) {
							currentWeight += pair.getSecond().getWeight();
							if (currentWeight > randomWeight) {
								boosts.add(pair.getFirst());
								currentStrength += pair.getFirst().getStrength();
							}
						}
					}
				}
			}
		}
		return boosts;
	}
}
