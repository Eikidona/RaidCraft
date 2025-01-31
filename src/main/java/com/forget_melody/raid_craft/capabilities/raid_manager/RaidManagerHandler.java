package com.forget_melody.raid_craft.capabilities.raid_manager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class RaidManagerHandler {
	
	@SubscribeEvent
	public static void registerCapability(RegisterCapabilitiesEvent event) {
		event.register(IRaidManager.class);
	}
	
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel level) {
			event.addCapability(IRaidManager.ID, new RaidManagerProvider(level));
		}
	}
	
	@SubscribeEvent
	public static void tick(TickEvent.LevelTickEvent event) {
		if(event.level.isClientSide()) return;
		IRaidManager manager = IRaidManager.get((ServerLevel) event.level);
		manager.tick();
	}
}
