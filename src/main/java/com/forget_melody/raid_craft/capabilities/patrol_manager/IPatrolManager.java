package com.forget_melody.raid_craft.capabilities.patrol_manager;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.patrol.Patrol;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@AutoRegisterCapability
public interface IPatrolManager extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "patrol_manager");
	
	static Optional<IPatrolManager> get(ServerLevel level) {
		return level.getCapability(Capabilities.PATROLLER_MANAGER).resolve();
	}
	
	Patrol createPatrol(Faction patrolType, BlockPos pos);
	
	@Nullable Patrol getPatrol(int id);
	
	Map<Integer, Patrol> getPatrols();
	
	void tick();
	
	@Override
	CompoundTag serializeNBT();
	
	@Override
	void deserializeNBT(CompoundTag nbt);
}
