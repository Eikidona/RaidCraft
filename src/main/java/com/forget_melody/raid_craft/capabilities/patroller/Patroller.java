package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.patrol_manager.IPatrolManager;
import com.forget_melody.raid_craft.raid.Patrol;
import com.forget_melody.raid_craft.world.entity.ai.goal.patrol.PatrolGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.patrol.PatrolLeaderGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

public class Patroller implements IPatroller {
	private final Mob mob;
	private Patrol patrol;
	private boolean patrolLeader = false;
	private boolean patrolling = false;
	private PatrolGoal<Mob> goal;
	private PatrolLeaderGoal<Mob> leaderGoal;
	
	public Patroller(Mob mob) {
		this.mob = mob;
	}
	
	private void updatePatrolGoals() {
		// add
		if (patrol != null) {
			if (goal == null) {
				goal = new PatrolGoal<>(mob, 0.9D);
			}
			if(isPatrolLeader()){
				if(leaderGoal == null){
					leaderGoal = new PatrolLeaderGoal<>(mob);
				}
				mob.goalSelector.addGoal(2, leaderGoal);
			}
			mob.goalSelector.addGoal(2, goal);
		}
		// remove
		else {
			if (goal != null) {
				mob.goalSelector.removeGoal(goal);
			}
			if(leaderGoal != null){
				mob.goalSelector.removeGoal(leaderGoal);
			}
		}
	}
	
	@Override
	public Mob getMob() {
		return mob;
	}
	
	@Override
	public Patrol getPatrol() {
		return patrol;
	}
	
	@Override
	public void setPatrol(Patrol patrol) {
		this.patrol = patrol;
		updatePatrolGoals();
	}
	
	@Override
	public boolean isPatrolLeader() {
		return patrolLeader;
	}
	
	@Override
	public void setPatrolling(boolean patrolling){
		this.patrolling = patrolling;
	}
	
	@Override
	public void setPatrolLeader(boolean leader) {
		patrolLeader = leader;
		
	}
	
	@Override
	public boolean isPatrolling() {
		return patrolling;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("PatrolLeader", this.patrolLeader);
		if (this.patrol != null) {
			tag.putInt("Patrol", this.patrol.getId());
		}
		tag.putBoolean("Patrolling", this.patrolling);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.patrolLeader = nbt.getBoolean("PatrolLeader");
		if (nbt.contains("Patrol")) {
			Optional<IPatrolManager> optional = IPatrolManager.get((ServerLevel) mob.level());
			if (optional.isPresent()) {
				IPatrolManager manager = optional.get();
				this.patrol = manager.getPatrol(nbt.getInt("Patrol"));
				if (this.patrol != null) {
					this.patrol.joinPatrol(this);
					if (this.patrolLeader) {
						this.patrol.setLeader(this);
					}
					RaidCraft.LOGGER.info("加入巡逻队");
				} else {
					RaidCraft.LOGGER.error("意外的Null: Patroller反序列化时加入巡逻队，patrol为null");
				}
			}
		}
		this.patrolling = nbt.getBoolean("Patrolling");
		updatePatrolGoals();
	}
	
}
