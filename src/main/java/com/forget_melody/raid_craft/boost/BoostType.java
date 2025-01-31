package com.forget_melody.raid_craft.boost;

public enum BoostType {
	EMPTY,
	EQUIPMENT,
	MOUNT;
	
	public static final BoostType[] VALUES = values();
	
	public static BoostType byName(String name) {
		for (BoostType type : VALUES) {
			if (type.name().equals(name)) {
				return type;
			}
		}
		return EMPTY;
	}
}
