package com.forget_melody.raid_craft.registries;

import net.minecraftforge.eventbus.api.IEventBus;

public class Registries {
	
	public static void register(IEventBus eventBus){
		Factions.register();
		RaidTargets.register(eventBus);
	}
}
