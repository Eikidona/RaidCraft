package com.forget_melody.raid_craft.raid.raid.target;

public enum RaidTargetType {
	PLAYER,
	VILLAGE,
	DESTROY;
	
	public static final RaidTargetType[] VALUES = values();
	
	public RaidTargetType byName(String name){
		for(RaidTargetType type: VALUES){
			if(type.name().equals(name)){
				return type;
			}
		}
		return PLAYER;
	}
}
