package com.forget_melody.raid_craft.capabilities.raid_interaction;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class RaidInteractionHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof ServerPlayer player) {
			event.addCapability(IRaidInteraction.ID, new RaidInteractionProvider(player));
		}
	}
}
