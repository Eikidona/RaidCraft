package com.forget_melody.raid_craft.world.entity.ai.goal.patrol;

import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.raid.patrol.Patrol;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PatrolGoal<T extends Mob> extends Goal {
	private final T mob;
	private final double speedModifier;
	
	public PatrolGoal(T mob, double speedModifier) {
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}
	
	/**
	 * 实体必须拥有巡逻队 且处于巡逻状态才可用
	 *
	 * @return
	 */
	@Override
	public boolean canUse() {
		if (mob.getTarget() != null) {
			return false;
		}
		IPatroller patroller = IPatroller.get(mob);
		Patrol patrol = patroller.getPatrol();
		if (patrol == null) {
			return false;
		}
		if (!patroller.isPatrolling()) {
			return false;
		}
		if (patrol.getPatrolTarget() == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 普通成员 走到目标位置
	 * 巡逻队队长 除去走到目标位置外 还需要更新和检查巡逻位置
	 */
	@Override
	public void tick() {
		IPatroller patroller = IPatroller.get(mob);
		BlockPos target = patroller.getPatrol().getPatrolTarget();
		// 抵达目标点时解除巡逻状态
		if (patroller.getMob().blockPosition().distManhattan(target) <= patroller.getMob().getBbWidth() + 5.0D) {
			patroller.setPatrolling(false);
			return;
		}
		// 走向目标地点
		if(mob.getNavigation().isDone()){
			mob.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), speedModifier);
		}
	}
}