package com.forget_melody.raid_craft.world.entity.ai.goal.patrol;

import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.EnumSet;
import java.util.Optional;

public class PatrolGoal<T extends Mob> extends Goal {
	private final T mob;
	private final double speedModifier;
	private final double leaderSpeedModifier;
	private int patrolCooldown = 200;
	
	public PatrolGoal(T mob, double speedModifier, double leaderSpeedModifier) {
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.leaderSpeedModifier = leaderSpeedModifier;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}
	
	@Override
	public boolean canUse() {
		if (this.patrolCooldown > 0) {
			patrolCooldown--;
			return false;
		} else {
			patrolCooldown = 200;
		}
		Optional<IPatroller> optional = IPatroller.get(this.mob);
		if (optional.isEmpty()) {
			return false;
		}
		IPatroller patroller = optional.get();
		return patroller.getPatrol() != null && mob.getNavigation().isDone();
	}
	
	@Override
	public void tick() {
		Optional<IPatroller> optional = IPatroller.get(this.mob);
		if (optional.isEmpty()) {
			return;
		}
		IPatroller patroller = optional.get();
		BlockPos patrolTarget = patroller.getPatrol().getPatrolTarget();
		if (patrolTarget == null) {
			if (patroller.isPatrolLeader()) {
				patroller.getPatrol().setPatrolTarget(findPatrolTarget());
			}
			return;
		}
		if (patrolTarget.closerThan(mob.blockPosition(), 3.0)) {
			return;
		}
		if (!mob.getNavigation().moveTo(patrolTarget.getX(), patrolTarget.getY(), patrolTarget.getZ(), speedModifier)) {
			moveRandomly();
		}
	}
	
	private void moveRandomly() {
		RandomSource random = this.mob.getRandom();
		BlockPos blockpos = this.mob.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.mob.blockPosition().offset(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.mob.getNavigation().moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), this.speedModifier);
	}
	
	private BlockPos findPatrolTarget() {
		BlockPos blockPos = mob.blockPosition().offset(-500 + mob.getRandom().nextInt(1000), 0, -500 + mob.getRandom().nextInt(1000));
		return mob.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos);
	}
}
