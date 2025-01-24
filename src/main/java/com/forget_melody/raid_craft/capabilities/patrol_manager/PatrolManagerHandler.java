package com.forget_melody.raid_craft.capabilities.patrol_manager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class PatrolManagerHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Level> event){
		if(event.getObject() instanceof ServerLevel){
			event.addCapability(IPatrolManager.ID, new PatrolManagerProvider((ServerLevel) event.getObject()));
		}
	}
}