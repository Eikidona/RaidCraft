package com.forget_melody.raid_craft.capabilities.raid_manager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;


@Mod.EventBusSubscriber
public class RaidManagerHandler {
	
	@SubscribeEvent
	public static void registerCapability(RegisterCapabilitiesEvent event) {
		event.register(IRaidManager.class);
	}
	
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel) {
			event.addCapability(RaidManagerProvider.ID, new RaidManagerProvider((ServerLevel) event.getObject()));
		}
	}
	
	@SubscribeEvent
	public static void tick(TickEvent.LevelTickEvent event) {
		if (event.level.isClientSide()) return;
		Optional<IRaidManager> optional = RaidManagerHelper.get((ServerLevel) event.level);
		optional.ifPresent(IRaidManager::tick);
		
	}
}
