package com.forget_melody.raid_craft.capabilities.raider;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RaiderHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof Mob){
			event.addCapability(Raider.ID, new RaiderProvider((Mob) event.getObject()));
		}
	}
}
