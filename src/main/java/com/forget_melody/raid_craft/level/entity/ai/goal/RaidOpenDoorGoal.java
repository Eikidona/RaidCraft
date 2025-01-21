package com.forget_melody.raid_craft.level.entity.ai.goal;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;

public class RaidOpenDoorGoal extends OpenDoorGoal {
	
	public RaidOpenDoorGoal(Mob pMob) {
		super(pMob, false);
	}
	
	@Override
	public boolean canUse() {
		IRaider raider = IRaider.getRaider(mob);
		return super.canUse() && raider.hasActiveRaid();
	}
}
