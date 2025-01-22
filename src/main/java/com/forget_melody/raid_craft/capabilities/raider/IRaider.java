package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.raid.raid.IRaid;
import com.forget_melody.raid_craft.raid.raider_type.RaiderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@AutoRegisterCapability
public interface IRaider extends INBTSerializable<CompoundTag> {
	static Optional<IRaider> getRaider(Mob mob) {
		return mob.getCapability(Capabilities.RAIDER).resolve();
	}
	
	int getWave();
	
	boolean hasActiveRaid();
	
	@Nullable RaiderType getRaiderType();
	
	boolean isLeader();
	
	Mob getMob();
	
	@Nullable IRaid getRaid();
	
	void setRaid(IRaid raid);
	
	void setWave(Integer wave);
	
	void updateRaidGoals();
	
	void setLeader(boolean leader);
	
	void setRaiderType(RaiderType raiderType);
	
}
