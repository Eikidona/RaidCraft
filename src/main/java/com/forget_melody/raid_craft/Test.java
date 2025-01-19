package com.forget_melody.raid_craft;

import com.forget_melody.raid_craft.capabilities.raid_manager.api.RaidManagerHelper;
import com.forget_melody.raid_craft.datapack.Reloads;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Test {
	@SubscribeEvent
	public static void test(ServerChatEvent event) {
		if(event.getMessage().contains(Component.literal("raid_type"))){
			event.getPlayer().sendSystemMessage(Component.literal("Size %d".formatted(Reloads.RAID_TYPE.getLoadedData().size())));
		}
		if(event.getMessage().contains(Component.literal("raid"))){
			event.getPlayer().sendSystemMessage(Component.literal("Try Create Raid"));
			RaidManagerHelper.get(event.getPlayer().serverLevel()).get().createRaid(event.getPlayer().blockPosition(), new ResourceLocation("raid_craft", "default"));
		}
	}
}
