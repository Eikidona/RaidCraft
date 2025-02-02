package com.forget_melody.raid_craft.boost;

public enum BoostType {
	EMPTY("empty"),
	EQUIPMENT("equipment"),
	MOUNT("mount");
	
	private final String name;
	
	
	public static final BoostType[] VALUES = values();
	
	BoostType(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public static BoostType byName(String name) {
		for (BoostType type : VALUES) {
			if (type.getName().equals(name)) {
				return type;
			}
		}
		return EMPTY;
	}
}
