package com.forget_melody.raid_craft.api.event.raid;

import com.forget_melody.raid_craft.raid.Raid;
import net.minecraft.world.level.LevelAccessor;

public class RaidSpawnMobEvent extends RaidEvent{
	public RaidSpawnMobEvent(LevelAccessor level, Raid raid) {
		super(level, raid);
	}
}
