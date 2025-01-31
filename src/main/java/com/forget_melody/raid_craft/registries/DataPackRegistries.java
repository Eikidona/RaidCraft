package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.registries.datapack.api.Boosts;
import com.forget_melody.raid_craft.registries.datapack.api.FactionEntityTypes;
import com.forget_melody.raid_craft.registries.datapack.api.Factions;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DataPackRegistries {
	public static final FactionEntityTypes Faction_ENTITY_TYPES = new FactionEntityTypes();
	public static final Factions FACTIONS = new Factions();
	public static final Boosts BOOSTS = new Boosts();
	
	@SubscribeEvent
	public static void onAddReloadListener(AddReloadListenerEvent event) {
		event.addListener(Faction_ENTITY_TYPES);
		event.addListener(FACTIONS);
		event.addListener(BOOSTS);
	}
}
