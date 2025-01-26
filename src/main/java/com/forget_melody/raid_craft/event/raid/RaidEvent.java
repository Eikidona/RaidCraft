package com.forget_melody.raid_craft.event.raid;

import com.forget_melody.raid_craft.raid.raid.Raid;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;

public abstract class RaidEvent extends LevelEvent {
	private final Raid raid;
	public RaidEvent(LevelAccessor level, Raid raid) {
		super(level);
		this.raid = raid;
	}
	
	public Raid getRaid() {
		return raid;
	}
}
