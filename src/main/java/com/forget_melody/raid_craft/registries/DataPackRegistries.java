package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.registries.datapack.api.FactionEntityTypeReloadListener;
import com.forget_melody.raid_craft.registries.datapack.api.FactionReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DataPackRegistries {
	public static final FactionEntityTypeReloadListener Faction_ENTITY_TYPES = new FactionEntityTypeReloadListener();
	public static final FactionReloadListener FACTIONS = new FactionReloadListener();
	
	@SubscribeEvent
	public static void onAddReloadListener(AddReloadListenerEvent event) {
		event.addListener(Faction_ENTITY_TYPES);
		event.addListener(FACTIONS);
	}
}
