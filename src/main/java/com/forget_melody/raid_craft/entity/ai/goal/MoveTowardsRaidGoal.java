package com.forget_melody.raid_craft.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class MoveTowardsRaidGoal<T extends Mob> extends Goal {
	@Override
	public boolean canUse() {
		return false;
	}
}
