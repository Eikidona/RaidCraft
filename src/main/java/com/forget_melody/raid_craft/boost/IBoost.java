package com.forget_melody.raid_craft.boost;

import net.minecraft.world.entity.Mob;

public interface IBoost {
	BoostType getType();
	
	void apply(Mob mob);
	
}
