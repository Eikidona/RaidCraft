package com.forget_melody.raid_craft.world.entity.ai.goal.raider;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Optional;

public class MoveTowardsRaidGoal<T extends Mob> extends Goal {
	private final T mob;
	
	public MoveTowardsRaidGoal(T mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}
	
	@Override
	public boolean canUse() {
		Optional<IRaider> raider = IRaider.get(mob);
		return mob.getTarget() == null && !mob.isVehicle() && raider.get().hasActiveRaid();
	}
	
	@Override
	public boolean canContinueToUse() {
		Optional<IRaider> raider = IRaider.get(mob);
		return mob.getTarget() == null && !mob.isVehicle() && raider.get().hasActiveRaid();
	}
	
	@Override
	public void tick() {
		Optional<IRaider> raider = IRaider.get(mob);
		if(mob.getNavigation().isDone()){
			mob.getNavigation().moveTo(raider.get().getRaid().getCenter().getX(), raider.get().getRaid().getCenter().getY(), raider.get().getRaid().getCenter().getZ(), 1.0D);
		}
	}
}
