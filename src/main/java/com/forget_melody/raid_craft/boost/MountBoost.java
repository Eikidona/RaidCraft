package com.forget_melody.raid_craft.boost;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class MountBoost implements IBoost{
	private final EntityType<?> entityType;
	
	public MountBoost(EntityType<?> entityType) {
		this.entityType = entityType;
	}
	
	@Override
	public BoostType getType() {
		return BoostType.MOUNT;
	}
	
	@Override
	public void apply(Mob mob) {
	
	}
	
	@Override
	public int getStrength() {
		return 0;
	}
}
