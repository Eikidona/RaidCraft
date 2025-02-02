package com.forget_melody.raid_craft.raid;

public enum RaidType {
	PLAYER,
	VILLAGE;
	
	public static final RaidType[] VALUES = values();
	
	public static RaidType byName(String name){
		for(RaidType type: VALUES){
			if(type.name().equals(name)){
				return type;
			}
		}
		return PLAYER;
	}
}
