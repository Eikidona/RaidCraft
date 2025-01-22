package com.forget_melody.raid_craft.event.raid;

import com.forget_melody.raid_craft.raid.raid.IRaid;
import net.minecraft.world.level.LevelAccessor;

public class RaidComputeStrengthEvent extends RaidEvent {
	
	public RaidComputeStrengthEvent(LevelAccessor level, IRaid raid) {
		super(level, raid);
	}
	
	public int getStrength() {
		return getRaid().getStrength();
	}
	
	public void setStrength(int strengthModifier) {
		getRaid().setStrength(strengthModifier);
	}
	
	public void addStrength(int strengthModifier) {
		getRaid().setStrength(getStrength() + strengthModifier);
	}
}