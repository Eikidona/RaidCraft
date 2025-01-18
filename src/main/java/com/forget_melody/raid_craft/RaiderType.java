package com.forget_melody.raid_craft;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class RaiderType {
	private int strength;
	private EntityType<?> entityType;
	
	public RaiderType(int strength, EntityType<?> entityType) {
		this.strength = strength;
		this.entityType = entityType;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public EntityType<?> getEntityType() {
		return entityType;
	};
}
