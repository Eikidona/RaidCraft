package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.patrol_manager.IPatrolManager;
import com.forget_melody.raid_craft.patrol.Patrol;
import com.forget_melody.raid_craft.world.entity.ai.goal.patrol.PatrolGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.patrol.PatrolLeaderGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

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
			mob.goalSelector.addGoal(2, goal);
		}
		// remove
		else {
			if (goal != null) {
				mob.goalSelector.removeGoal(goal);
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
	
	private void updateLeaderGoal(boolean leader){
		if(leader){
			if(leaderGoal == null){
				leaderGoal = new PatrolLeaderGoal<>(mob);
			}
			mob.goalSelector.addGoal(3, leaderGoal);
		}else {
			if(leaderGoal != null){
				mob.goalSelector.removeGoal(leaderGoal);
			}
		}
	}
	
	@Override
	public void setPatrolLeader(boolean leader) {
		patrolLeader = leader;
		updateLeaderGoal(leader);
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
			IPatrolManager manager = IPatrolManager.get((ServerLevel) mob.level());
			this.patrol = manager.getPatrol(nbt.getInt("Patrol"));
			if (this.patrol != null) {
				this.patrol.joinPatrol(this);
				if (this.patrolLeader) {
					this.patrol.setLeader(this);
				}
//				RaidCraft.LOGGER.info("加入巡逻队");
			} else {
				setPatrolling(false);
				setPatrolLeader(false);
				RaidCraft.LOGGER.error("意外的Null: Patroller反序列化时加入巡逻队，patrol为null");
			}
		}
		this.patrolling = nbt.getBoolean("Patrolling");
		updatePatrolGoals();
	}
	
}
