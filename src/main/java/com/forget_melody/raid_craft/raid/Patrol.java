package com.forget_melody.raid_craft.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.raid.patrol_type.PatrolType;
import com.forget_melody.raid_craft.registries.DatapackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 巡逻队 检查实体存活性 当队内全部实体都死亡时 该队在300tick后解散
 */
public class Patrol {
	private final ServerLevel level;
	private boolean stopped;
	private int noActiveTicks = 0;
	private final int id;
	private BlockPos patrolTarget;
	private final PatrolType patrolType;
	private IPatroller leader;
	private final List<IPatroller> patrollerList = new ArrayList<>();
	
	public Patrol(int id, ServerLevel level, PatrolType patrolType, List<IPatroller> patrollerList) {
		this.id = id;
		this.level = level;
		this.patrolType = patrolType;
		this.patrollerList.addAll(patrollerList);
		this.leader = this.patrollerList.get(0);
		
		this.leader.setPatrolLeader(true);
		Mob mob = this.leader.getMob();
		ItemStack itemStack = mob.getItemBySlot(EquipmentSlot.HEAD);
		if (!itemStack.isEmpty()) {
			mob.spawnAtLocation(itemStack);
		}
		mob.setItemSlot(EquipmentSlot.HEAD, getBanner());
		mob.setDropChance(EquipmentSlot.HEAD, 2.0F);
		this.patrollerList.forEach(patroller -> patroller.setPatrol(this));
	}
	
	public ItemStack getBanner() {
		return patrolType.getBanner();
	}
	
	public Patrol(ServerLevel level, CompoundTag tag) {
		this.level = level;
		this.id = tag.getInt("Id");
		if (tag.contains("PatrolTarget")) {
			this.patrolTarget = NbtUtils.readBlockPos(tag.getCompound("PatrolTarget"));
		}
		ResourceLocation patrolTypeId = new ResourceLocation(tag.getString("PatrolType"));
		PatrolType patrolType1 = DatapackRegistries.PATROL_TYPES.getValue(patrolTypeId);
		if (patrolType1 == null) {
			RaidCraft.LOGGER.error("Not found PatrolType id {}", patrolTypeId);
			this.stopped = true;
		}
		this.patrolType = patrolType1;
	}
	
	@Nullable
	public IPatroller getLeader() {
		return leader;
	}
	
	public void setLeader(IPatroller leader) {
		this.leader = leader;
	}
	
	public int getNumPatrollers() {
		return patrollerList.size();
	}
	
	public List<IPatroller> getPatrollerList() {
		return patrollerList;
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * 检查实体是否存活 更新Leader字段状态
	 */
	private void updatePatroller() {
		Iterator<IPatroller> iterator = patrollerList.iterator();
		while (iterator.hasNext()) {
			IPatroller patroller = iterator.next();
			if (!patroller.getMob().isAlive()) {
				iterator.remove();
				if (patroller.isPatrolLeader()) {
					leader = null;
				}
			}
		}
	}
	
	public void tick() {
		if (isStopped()) return;
		updatePatroller();
		if (patrollerList.isEmpty()) {
			noActiveTicks++;
		} else {
			noActiveTicks = 0;
		}
		
		if (noActiveTicks == 300) {
			stop();
		}
	}
	
	public PatrolType getPatrolType() {
		return patrolType;
	}
	
	@Nullable
	public BlockPos getPatrolTarget() {
		return patrolTarget;
	}
	
	public void updatePatrolTarget(BlockPos patrolTarget) {
		this.patrolTarget = patrolTarget;
		this.patrollerList.forEach(patroller -> patroller.setPatrolling(true));
	}
	
	public void joinPatrol(IPatroller patroller) {
		this.patrollerList.add(patroller);
	}
	
	public void quitPatrol(IPatroller patroller){
		this.patrollerList.remove(patroller);
		if(patroller.isPatrolLeader()){
			this.leader = null;
		}
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void stop() {
		this.stopped = true;
		RaidCraft.LOGGER.info("巡逻队Stop");
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Id", this.id);
		tag.putString("PatrolType", DatapackRegistries.PATROL_TYPES.getKey(this.patrolType).toString());
		if (this.patrolTarget != null) {
			tag.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
		}
		
		return tag;
	}
}
