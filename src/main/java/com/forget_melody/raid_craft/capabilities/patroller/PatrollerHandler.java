package com.forget_melody.raid_craft.capabilities.patroller;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PatrollerHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof Mob mob){
			event.addCapability(IPatroller.ID, new PatrollerProvider(mob));
		}
	}
	@SubscribeEvent
	public static void addEffectToKiller(LivingDeathEvent event){
		if(event.getEntity() instanceof Mob mob && event.getSource().getEntity() instanceof ServerPlayer player){
			IPatroller patroller = IPatroller.get(mob);
			if(patroller.isPatrolLeader()){
				/**
				 * todo {BadOmen}
				 */
			}
		}
	}
}
