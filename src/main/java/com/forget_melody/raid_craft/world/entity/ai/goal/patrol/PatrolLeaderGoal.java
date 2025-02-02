package com.forget_melody.raid_craft.world.entity.ai.goal.patrol;

import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.patrol.Patrol;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 仅负责寻找巡逻位置 更新自身及其队友的巡逻状态
 *
 * @param <T>
 */
public class PatrolLeaderGoal<T extends Mob> extends Goal {
	private final Mob mob;
	private int cooldownTicks = 100;
	
	public PatrolLeaderGoal(Mob mob) {
		this.mob = mob;
	}
	
	@Override
	public boolean canUse() {
		
		if (mob.getTarget() != null) {
			return false;
		}
		IPatroller patroller = IPatroller.get(mob);
		if (patroller.getPatrol() == null) {
			return false;
		}
		if (patroller.isPatrolling()) {
			return false;
		}
		if (!patroller.isPatrolLeader()) {
			return false;
		}
		return true;
	}
	
	@Override
	public void tick() {
		
		IPatroller patroller = IPatroller.get(mob);
		Patrol patrol = patroller.getPatrol();
		if (cooldownTicks == 0) {

cooldownTicks = 100;
			patrol.updatePatrolTarget(findPatrolTarget(patrol.getOriginPos()));
		} else {
			cooldownTicks--;
		}
	}
	
	private BlockPos findPatrolTarget(BlockPos pos) {
		BlockPos blockPos = pos.offset(
				-32 + mob.getRandom().nextInt(64),
				0,
				-32 + mob.getRandom().nextInt(64)
		);
		return mob.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos);
	}
	
	private IPatroller getPatroller() {
		return IPatroller.get(mob);
	}
}
