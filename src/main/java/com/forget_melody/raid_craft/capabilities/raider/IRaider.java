package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.raid.Raid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IRaider extends INBTSerializable<CompoundTag> {
	Mob get();
	Raid getRaid();
	void setRaid(Raid raid);
	void setWave(Integer wave);
	int getWave();
	boolean hasActiveRaid();
	void updateRaidGoals();
	
}
