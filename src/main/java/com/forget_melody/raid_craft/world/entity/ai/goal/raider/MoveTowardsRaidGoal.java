package com.forget_melody.raid_craft.world.entity.ai.goal.raider;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MoveTowardsRaidGoal<T extends Mob> extends Goal {
	private final T mob;
	
	public MoveTowardsRaidGoal(T mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}
	
	@Override
	public boolean canUse() {
		IRaider raider = IRaider.get(mob);
		RaidCraft.LOGGER.info("canUse: Target: {}, hasActiveRaid: {}", mob.getTarget() == null, raider.hasActiveRaid());
		return mob.getTarget() == null && !mob.isVehicle() && raider.hasActiveRaid();
	}
	
	@Override
	public boolean canContinueToUse() {
		IRaider raider = IRaider.get(mob);
		RaidCraft.LOGGER.info("canContinueToUse: Target: {}, hasActiveRaid: {}", mob.getTarget() == null, raider.hasActiveRaid());
		return mob.getTarget() == null && !mob.isVehicle() && raider.hasActiveRaid();
	}
	
	@Override
	public void tick() {
		IRaider raider = IRaider.get(mob);
		if(mob.getNavigation().isDone()){
			mob.getNavigation().moveTo(raider.getRaid().getCenter().getX(), raider.getRaid().getCenter().getY(), raider.getRaid().getCenter().getZ(), 1.0D);
		}
	}
}
