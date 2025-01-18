package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.Raid;
import com.forget_melody.raid_craft.RaidType;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.capabilities.raid_manager.api.RaidManagerHelper;
import com.forget_melody.raid_craft.registries.RaidTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;


@Mod.EventBusSubscriber
public class RaidManagerHandler {
	
	@SubscribeEvent
	public static void registerCapability(RegisterCapabilitiesEvent event) {
		event.register(IRaidManager.class);
	}
	
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel) {
			event.addCapability(RaidManager.ID, new RaidManager.Provider((ServerLevel) event.getObject()));
		}
	}
	
	@SubscribeEvent
	public static void tick(TickEvent.LevelTickEvent event) {
		if (event.level.isClientSide()) return;
		IRaidManager manager = event.level.getCapability(Capabilities.RAID_MANAGER).orElse(RaidManager.EMPTY);
		if (manager != RaidManager.EMPTY && manager.getLevel() == event.level) {
			manager.tick();
		}
	}

	@SubscribeEvent
	public static void test(ServerChatEvent event){
		if(event.getMessage().contains(Component.literal("raid"))){
			event.getPlayer().sendSystemMessage(Component.literal(String.valueOf((RaidTypes.DEFAULT.get() == null))));
			RaidManagerHelper.get(event.getPlayer().serverLevel()).createRaid(event.getPlayer().serverLevel(), event.getPlayer().blockPosition(), RaidTypes.DEFAULT.get());
		}
		if(event.getMessage().contains(Component.literal("raids"))){
			HashMap<Integer, Raid> map = RaidManagerHelper.get(event.getPlayer().serverLevel()).getRaids();
			event.getPlayer().sendSystemMessage(Component.literal(String.valueOf(map.size())));
		}
	}
}
