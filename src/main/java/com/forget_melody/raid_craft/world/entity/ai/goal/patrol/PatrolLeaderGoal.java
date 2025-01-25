package com.forget_melody.raid_craft.world.entity.ai.goal.patrol;

import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.raid.Patrol;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

/**
 * 仅负责寻找巡逻位置 更新自身及其队友的巡逻状态
 * @param <T>
 */
public class PatrolLeaderGoal<T extends Mob> extends Goal {
	private final Mob mob;
	private int cooldownTicks = 200;
	public PatrolLeaderGoal(Mob mob) {
		this.mob = mob;
	}
	
	@Override
	public boolean canUse() {
		Optional<IPatroller> optional = getPatroller();
		if(optional.isEmpty()){
			return false;
		}
		if(mob.getTarget() != null){
			return false;
		}
		IPatroller patroller = optional.get();
		if(patroller.getPatrol() == null){
			return false;
		}
		if(patroller.isPatrolling()){
			return false;
		}
		if(!patroller.isPatrolLeader()){
			return false;
		}
		return true;
	}
	
	@Override
	public void tick() {
		Optional<IPatroller> optional = getPatroller();
		if(optional.isEmpty()){
			return;
		}
		IPatroller patroller = optional.get();
		Patrol patrol = patroller.getPatrol();
		if(cooldownTicks == 0){
			patrol.updatePatrolTarget(findPatrolTarget());
		}else {
			cooldownTicks--;
		}
	}
	
	private BlockPos findPatrolTarget() {
		BlockPos blockPos = mob.blockPosition().offset(
				-32 + mob.getRandom().nextInt(64),
				0,
				-32 + mob.getRandom().nextInt(64)
		);
		return mob.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos);
	}
	
	private Optional<IPatroller> getPatroller() {
		return IPatroller.get(mob);
	}
}
