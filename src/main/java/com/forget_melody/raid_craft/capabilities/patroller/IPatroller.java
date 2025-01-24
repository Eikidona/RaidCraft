package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.raid.Patrol;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@AutoRegisterCapability
public interface IPatroller extends INBTSerializable<CompoundTag> {
	
	ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "patroller");
	
	static Optional<IPatroller> get(Mob mob) {
		return mob.getCapability(Capabilities.PATROLLER).resolve();
	}
	
	Mob getMob();
	
	@Nullable
	Patrol getPatrol();
	
	void setPatrol(Patrol patrol);
	
	boolean isPatrolLeader();
	
	void setPatrolLeader(boolean leader);
	
	boolean isPatrolling();
	
	@Override
	CompoundTag serializeNBT();
	
	@Override
	void deserializeNBT(CompoundTag nbt);
	
	
}
