package com.forget_melody.raid_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum BoostRarity {
	COMMON("common", 10),
	UNCOMMON("uncommon", 7),
	RARE("rare", 5),
	EPIC("epic", 3),
	ANCIENT("ancient", 1);
	
	private final String name;
	private final int weight;
	BoostRarity(String name, int weight) {
		this.name = name;
		this.weight = weight;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public String getName(){
		return name;
	}
	
	public static final Codec<BoostRarity> CODEC = Codec.STRING.flatComapMap(BoostRarity::byName, boostRarity -> DataResult.success(boostRarity.name()));
	
	public static final BoostRarity[] VALUES = values();
	
	public static BoostRarity byName(String name){
		for(BoostRarity rarity: VALUES){
			if(rarity.name().equals(name)){
				return rarity;
			}
		}
		return COMMON;
	}
}
