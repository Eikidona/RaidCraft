package com.forget_melody.raid_craft.event.raid;

import com.forget_melody.raid_craft.raid.raid.IRaid;
import com.forget_melody.raid_craft.raid.raid.Raid;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;

public abstract class RaidEvent extends LevelEvent {
	private final IRaid raid;
	public RaidEvent(LevelAccessor level, IRaid raid) {
		super(level);
		this.raid = raid;
	}
	
	public IRaid getRaid() {
		return raid;
	}
}
