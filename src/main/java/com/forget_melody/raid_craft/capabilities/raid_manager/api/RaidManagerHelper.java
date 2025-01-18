package com.forget_melody.raid_craft.capabilities.raid_manager.api;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.capabilities.raid_manager.RaidManager;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class RaidManagerHelper {
	@Nullable
	 public static IRaidManager get(ServerLevel level) {
//		IRaidManager manager = level.getCapability(Capabilities.RAID_MANAGER).orElse(RaidManager.EMPTY);
//		return manager == RaidManager.EMPTY ? null : manager;
		return level.getCapability(Capabilities.RAID_MANAGER).orElse(RaidManager.EMPTY);
	}
}
