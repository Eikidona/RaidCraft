package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.raid.raid.Raid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IRaider extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MOD_ID, "raider");
	
	static IRaider get(Mob mob) {
		return mob.getCapability(Capabilities.RAIDER).resolve().get();
	}
	
	boolean hasActiveRaid();
	
	void setWave(int wave);
	
	boolean isLeader();
	
	Mob getMob();
	
	Raid getRaid();
	
	boolean canJoinRaid();
	
	void setRaid(Raid raid);
	
	void setLeader(boolean leader);
	
	int getWave();
}
