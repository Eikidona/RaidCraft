package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

@AutoRegisterCapability
public interface IPatroller extends INBTSerializable<CompoundTag> {
	
	ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "patroller");
	
	static Optional<IPatroller> get(Mob mob) {
		return mob.getCapability(Capabilities.PATROLLER).resolve();
	}
	
	void setPatrolTarget(BlockPos blockPos);
	
	BlockPos getPatrolTarget();
	
	boolean isPatrolLeader();
	
	boolean isPatrolling();
	
	void setPatrolling(boolean patrolling);
	
	boolean hasPatrolTarget();
	
	void findPatrolTarget();
	
	boolean canJoinPatrol(Mob mob);
	
	@Override
	CompoundTag serializeNBT();
	
	@Override
	void deserializeNBT(CompoundTag nbt);
}
