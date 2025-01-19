package com.forget_melody.raid_craft.datapack;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.datapack.api.Reload;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Reloads {
	public static final Reload<RaiderType> RAIDER_TYPE = new Reload<>("raider_type", RaiderType.CODEC);
	public static final Reload<RaidType> RAID_TYPE = new Reload<>("raid_type", RaidType.CODEC);
	
	
	@SubscribeEvent
	public static void onAddReloadListener(AddReloadListenerEvent event){
		RaidCraft.LOGGER.info("[RaidCraft] ReloadListenerEvent is listening");
		event.addListener(RAIDER_TYPE);
		event.addListener(RAID_TYPE);
	}
}
