package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.world.entity.ai.goal.patrol.PatrolGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

public class Patroller implements IPatroller {
	private final Mob mob;
	private BlockPos patrolTarget;
	private boolean patrolLeader = false;
	private boolean patrolling = false;
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
	public void setPatrolTarget(BlockPos patrolTarget) {
		this.patrolTarget = patrolTarget;
	}
	
	@Override
	public BlockPos getPatrolTarget() {
		return patrolTarget;
	}
	
	@Override
	public boolean isPatrolLeader() {
		return patrolLeader;
	}
	
	@Override
	public boolean isPatrolling() {
		return patrolling;
	}
	
	@Override
	public void setPatrolling(boolean patrolling) {
		// 防止false to false尝试删除不存在的Goal导致崩溃 true to true也没有意义
		if (this.patrolling == patrolling) return;
		this.patrolling = patrolling;
		updatePatrolGoals();
	}
	
	@Override
	public boolean hasPatrolTarget() {
		return patrolTarget != null;
	}
	
	@Override
	public void findPatrolTarget() {
		this.patrolTarget = this.mob.blockPosition().offset(-500 + this.mob.getRandom().nextInt(1000), 0, this.mob.getRandom().nextInt(1000) - 500);
		this.patrolling = true;
	}
	
	@Override
	public boolean canJoinPatrol(Mob mob) {
		Optional<IFactionEntity> targetOptional = IFactionEntity.getFactionEntity(mob);
		Optional<IFactionEntity> selfOptional = IFactionEntity.getFactionEntity(this.mob);
		if (targetOptional.isEmpty() || selfOptional.isEmpty()) {
			return false;
		}
		IFactionEntity target = targetOptional.get();
		IFactionEntity self = targetOptional.get();
		if (target.getFaction() == null || self.getFaction() == null) {
			return false;
		}
		return target.getFaction() == self.getFaction();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("PatrolLeader", this.patrolLeader);
		tag.putBoolean("Patrolling", this.patrolling);
		if (patrolTarget != null) {
			tag.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.patrolling = nbt.getBoolean("Patrolling");
		this.patrolLeader = nbt.getBoolean("PatrolLeader");
		if (nbt.contains("PatrolTarget")) {
			this.patrolTarget = NbtUtils.readBlockPos(nbt.getCompound("PatrolTarget"));
		}
		if (this.patrolling) {
			updatePatrolGoals();
		}
	}
}
