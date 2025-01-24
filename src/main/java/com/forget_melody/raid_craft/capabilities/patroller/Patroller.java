package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.capabilities.patrol_manager.IPatrolManager;
import com.forget_melody.raid_craft.raid.Patrol;
import com.forget_melody.raid_craft.world.entity.ai.goal.patrol.PatrolGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

public class Patroller implements IPatroller {
	private final Mob mob;
	private Patrol patrol;
	private boolean patrolLeader = false;
	private final PatrolGoal<Mob> goal;
	
	public Patroller(Mob mob) {
		this.mob = mob;
		this.goal = new PatrolGoal<>(mob, 0.7D, 0.595D);
	}
	
	private void updatePatrolGoals() {
		if (this.isPatrolling()) {
			this.mob.goalSelector.addGoal(4, goal);
		} else {
			this.mob.goalSelector.removeGoal(goal);
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
		if (this.patrol == patrol) {
			return;
		}
		this.patrol = patrol;
		updatePatrolGoals();
	}
	
	@Override
	public boolean isPatrolLeader() {
		return patrolLeader;
	}
	
	@Override
	public void setPatrolLeader(boolean leader) {
		this.patrolLeader = leader;
	}
	
	@Override
	public boolean isPatrolling() {
		return this.patrol != null;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("PatrolLeader", this.patrolLeader);
		if(this.patrol != null){
			tag.putInt("Patrol", this.patrol.getId());
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.patrolLeader = nbt.getBoolean("PatrolLeader");
		if(nbt.contains("Patrol")){
			Optional<IPatrolManager> optional = IPatrolManager.get((ServerLevel) mob.level());
			if(optional.isPresent()){
				IPatrolManager manager = optional.get();
				this.patrol = manager.getPatrol(nbt.getInt("Patrol"));
				
			}
		}
		if (isPatrolling()) {
			updatePatrolGoals();
		}
	}
	
}
