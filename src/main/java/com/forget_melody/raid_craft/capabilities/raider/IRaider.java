package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.raid.raid.IRaid;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IRaider extends INBTSerializable<CompoundTag> {
	static IRaider getRaider(Mob mob) {
		return mob.getCapability(Capabilities.RAIDER).resolve().get();
	}
	
	Mob getEntity();
	
	IRaid getRaid();
	
	void setRaid(IRaid raid);
	
	void setWave(Integer wave);
	
	int getWave();
	
	boolean hasActiveRaid();
	
	void updateRaidGoals();
	
	void setRaiderType(RaiderType raiderType);
	
	void setLeader(boolean leader);
	
	boolean isLeader();
}
