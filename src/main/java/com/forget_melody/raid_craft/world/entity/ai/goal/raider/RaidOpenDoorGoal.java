package com.forget_melody.raid_craft.world.entity.ai.goal.raider;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;

import java.util.Optional;

public class RaidOpenDoorGoal extends OpenDoorGoal {
	
	public RaidOpenDoorGoal(Mob pMob) {
		super(pMob, false);
	}
	
	@Override
	public boolean canUse() {
		Optional<IRaider> raider = IRaider.get(mob);
		return super.canUse() && raider.get().hasActiveRaid();
	}
}
