package com.forget_melody.raid_craft.datapack.api;

import com.forget_melody.raid_craft.raid.raider.RaiderType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Reloads {
	public static final Reload<RaiderType> RAIDER_TYPE = new Reload<>("raider_type", RaiderType.CODEC);
	
	@SubscribeEvent
	public static void onAddReloadListener(AddReloadListenerEvent event){
		event.addListener(RAIDER_TYPE);
	}
}
