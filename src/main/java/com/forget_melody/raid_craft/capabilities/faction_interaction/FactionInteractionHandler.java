package com.forget_melody.raid_craft.capabilities.faction_interaction;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FactionInteractionHandler {
	
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof ServerPlayer player){
			event.addCapability(IFactionInteraction.ID, new FactionInteractionProvider(player));
		}
	}
}
