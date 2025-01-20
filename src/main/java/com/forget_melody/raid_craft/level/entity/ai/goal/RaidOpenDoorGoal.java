package com.forget_melody.raid_craft.level.entity.ai.goal;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.capabilities.raider.RaiderHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;

import java.util.Optional;

public class RaidOpenDoorGoal extends OpenDoorGoal {
	
	public RaidOpenDoorGoal(Mob pMob) {
		super(pMob, false);
	}
	
	@Override
	public boolean canUse() {
		Optional<IRaider> raider = RaiderHelper.getRaider(mob);
		if(!raider.isPresent()){
			return false;
		}
		return super.canUse() && raider.get().hasActiveRaid();
	}
}
