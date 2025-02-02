package com.forget_melody.raid_craft.capabilities.patrol_manager;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.patrol.Patrol;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@AutoRegisterCapability
public interface IPatrolManager extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MOD_ID, "patrol_manager");
	
	static IPatrolManager get(ServerLevel level) {
		return level.getCapability(Capabilities.PATROLLER_MANAGER).resolve().get();
	}
	
	Patrol createPatrol(Faction faction, BlockPos pos);
	
	@Nullable Patrol getPatrol(int id);
	
	Map<Integer, Patrol> getPatrols();
	
	void tick();
	
	@Override
	CompoundTag serializeNBT();
	
	@Override
	void deserializeNBT(CompoundTag nbt);
}
