package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.RaidCraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PatrollerHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof Mob){
			event.addCapability(IPatroller.ID, new PatrollerProvider((Mob) event.getObject()));
		}
	}
}
