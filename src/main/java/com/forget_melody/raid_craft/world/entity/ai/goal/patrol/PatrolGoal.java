package com.forget_melody.raid_craft.world.entity.ai.goal.patrol;

import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class PatrolGoal<T extends Mob> extends Goal {
	private final T mob;
	private final double speedModifier;
	private final double leaderSpeedModifier;
	private int patrolCooldown = 300;
	
	public PatrolGoal(T mob, double speedModifier, double leaderSpeedModifier) {
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.leaderSpeedModifier = leaderSpeedModifier;
	}
	
	@Override
	public boolean canUse() {
		if (this.patrolCooldown > 0) {
			patrolCooldown--;
			return false;
		}else {
			patrolCooldown = 200;
		}
		Optional<IPatroller> optional = IPatroller.get(this.mob);
		if (optional.isEmpty()) {
			return false;
		}
		IPatroller patroller = optional.get();
		return patroller.isPatrolling() && this.mob.getTarget() == null && !this.mob.isVehicle() && patroller.hasPatrolTarget();
	}
	
	@Override
	public void tick() {
		Optional<IPatroller> optional = IPatroller.get(this.mob);
		if (optional.isEmpty()) {
			return;
		}
		IPatroller patroller = optional.get();
		boolean isLeader = patroller.isPatrolLeader();
		PathNavigation pathNavigator = this.mob.getNavigation();
		
		if (pathNavigator.isDone()) {
			List<Mob> list = this.findPatrolCompanions();
			if (isLeader && patroller.getPatrolTarget().closerThan(this.mob.blockPosition(), 10.0D)) {
				patroller.findPatrolTarget();
			} else {
				Vec3 vector3d = Vec3.atBottomCenterOf(patroller.getPatrolTarget());
				Vec3 vector3d1 = this.mob.position();
				Vec3 vector3d2 = vector3d1.subtract(vector3d);
				vector3d = vector3d2.yRot(90.0F).scale(0.4D).add(vector3d);
				Vec3 vector3d3 = vector3d.subtract(vector3d1).normalize().scale(10.0D).add(vector3d1);
				BlockPos blockpos = new BlockPos((int) vector3d3.x(), (int) vector3d3.y(), (int) vector3d3.z());
				
				blockpos = this.mob.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos);
				
				if (!pathNavigator.moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), isLeader ? this.leaderSpeedModifier : this.speedModifier)) {
					this.moveRandomly();
				} else if (isLeader) {
					for (Mob mob : list) {
						Optional<IPatroller> optional1 = IPatroller.get(mob);
						if (optional1.isPresent()) {
							IPatroller patroller1 = optional1.get();
							patroller1.setPatrolTarget(blockpos);
						}
					}
				}
			}
		}
	}
	
	private List<Mob> findPatrolCompanions() {
		return this.mob.level().getEntitiesOfClass(Mob.class, this.mob.getBoundingBox().inflate(16.0D), (mob) -> {
			Optional<IPatroller> optional = IPatroller.get(mob);
			return optional.filter(patroller -> patroller.canJoinPatrol(this.mob) && !mob.is(this.mob)).isPresent();
		});
	}
	
	private boolean moveRandomly() {
		RandomSource random = this.mob.getRandom();
		BlockPos blockpos = this.mob.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.mob.blockPosition().offset(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		return this.mob.getNavigation().moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), this.speedModifier);
	}
}
