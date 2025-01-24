package com.forget_melody.raid_craft.world.spawner;

import com.forget_melody.raid_craft.event.spawner.TickSpawnerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnerHandler {
	@SubscribeEvent
	public static void addSpawner(TickSpawnerEvent event) {
		event.addCustomSpawner(new PatrolSpawner());
	}
}
