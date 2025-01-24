package com.forget_melody.raid_craft.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.raid.patrol_type.PatrolType;
import com.forget_melody.raid_craft.registries.DatapackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Patrol {
	private boolean stopped;
	private int noActiveTicks = 0;
	private final int id;
	private BlockPos patrolTarget;
	private final PatrolType patrolType;
	private IPatroller leader;
	private final Set<IPatroller> members;
	
	public Patrol(int id, PatrolType patrolType, IPatroller leader, Set<IPatroller> members) {
		this.id = id;
		this.patrolType = patrolType;
		this.leader = leader;
		this.members = members;
	}
	
	public Patrol(CompoundTag tag){
		this.id = tag.getInt("Id");
		if(tag.contains("PatrolTarget")){
			this.patrolTarget = NbtUtils.readBlockPos(tag.getCompound("PatrolTarget"));
		}
		ResourceLocation patrolTypeId = new ResourceLocation(tag.getString("PatrolType"));
		PatrolType patrolType1 = DatapackRegistries.PATROL_TYPES.getValue(patrolTypeId);
		if(patrolType1 == null){
			RaidCraft.LOGGER.error("Not found PatrolType id {}", patrolTypeId);
			this.stopped = true;
		}
		this.patrolType = patrolType1;
		this.members = new HashSet<>();
	}
	
	public IPatroller getLeader() {
		return leader;
	}
	
	public void setLeader(IPatroller leader){
		if(!members.contains(leader)){
			return;
		}
		members.remove(leader);
		this.leader = leader;
	}
	
	public Set<IPatroller> getMembers() {
		return members;
	}
	
	public int getNumPatrollers() {
		return this.leader == null ? this.members.size() : this.members.size() + 1;
	}
	
	public Set<IPatroller> getPatrollers() {
		Set<IPatroller> patrollers = new HashSet<>(this.members);
		if (this.leader != null) {
			patrollers.add(this.leader);
		}
		return patrollers;
	}
	
	public int getId() {
		return id;
	}
	
	public void tick() {
		if (isStopped()) return;
		if (noActiveTicks == 300) {
			stop();
		}
		if (leader == null && members.isEmpty()) {
			noActiveTicks++;
		}
	}
	
	public PatrolType getPatrolType() {
		return patrolType;
	}
	
	@Nullable
	public BlockPos getPatrolTarget() {
		return patrolTarget;
	}
	
	public void setPatrolTarget(BlockPos patrolTarget) {
		this.patrolTarget = patrolTarget;
	}
	
	/**
	 * 巡逻队队长试图招募巡逻队队员时调用
	 *
	 * @param patroller
	 * @return
	 */
	public boolean canRecruit(IPatroller patroller) {
		if(members.contains(patroller)){
			return false;
		}
		Optional<IFactionEntity> optional = IFactionEntity.get(patroller.getMob());
		if(optional.isEmpty()){
			return false;
		}
		IFactionEntity factionEntity = optional.get();
		return factionEntity.getFaction() == getPatrolType().getFaction();
	}
	
	public void joinPatrol(IPatroller patroller) {
		this.members.add(patroller);
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void stop() {
		this.stopped = true;
	}
	
	public CompoundTag save(){
		CompoundTag tag = new  CompoundTag();
		tag.putInt("Id", this.id);
		tag.putString("PatrolType", DatapackRegistries.PATROL_TYPES.getKey(this.patrolType).toString());
		if(this.patrolTarget != null){
			tag.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
		}
		return tag;
	}
}
