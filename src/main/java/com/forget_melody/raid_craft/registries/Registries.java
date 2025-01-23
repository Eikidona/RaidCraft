package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.registries.datapack.api.Factions;
import net.minecraftforge.eventbus.api.IEventBus;

public class Registries {
	
	public static void register(IEventBus eventBus){
		Factions.register();
	}
}
