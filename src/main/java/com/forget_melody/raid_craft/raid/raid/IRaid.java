package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;

public interface IRaid {
	int RAID_REMOVAL_THRESHOLD_SQR = 12544;
	
	default void joinRaid(Mob mob) {
		joinRaid(IRaider.getRaider(mob));
	}
	
	int getStrength();
	
	void setStrength(int strength);
	
	void joinRaid(IRaider raider);
	
	void start();
	
	void tick();
	
	void stop();
	
	void victory();
	
	void defeat();
	
	boolean isStopped();
	
	boolean isOver();
	
	boolean isStarted();
	
	boolean isVictory();
	
	boolean isDefeat();
	
	boolean isActive();
	
	BlockPos getCenter();
	
	CompoundTag save();
	
	int getId();
	
	ServerLevel getLevel();
	
	RaidType getRaidType();
}
