package com.forget_melody.raid_craft.capabilities;

import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class Capabilities {
	public static final Capability<IRaider> RAIDER = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<IRaidManager> RAID_MANAGER = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<IFactionEntity> FACTION_ENTITY = CapabilityManager.get(new CapabilityToken<>() {});
	
}
