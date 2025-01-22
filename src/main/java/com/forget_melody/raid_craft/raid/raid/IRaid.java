package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.faction.IFaction;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public interface IRaid {
	int RAID_REMOVAL_THRESHOLD_SQR = 12544;
	
	int getStrength();
	
	void setStrength(int strength);
	
	void joinRaid(IRaider raider);
	
	void start();
	
	void setLeaderRaider(IRaider raider);
	
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
	
	IFaction getFaction();
	
	ItemStack getBanner();
	
	CompoundTag save();
	
	int getId();
	
	ServerLevel getLevel();
	
	RaidType getRaidType();
	
	IRaider getLeader();
}
