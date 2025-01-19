package com.forget_melody.raid_craft.capabilities.raid_manager.api;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public class RaidManagerHelper {
	public static Optional<IRaidManager> get(ServerLevel level) {
		return level.getCapability(Capabilities.RAID_MANAGER).resolve();
	}
}
