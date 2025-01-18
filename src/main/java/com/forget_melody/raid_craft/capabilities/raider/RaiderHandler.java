package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RaiderHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof Mob){
			event.addCapability(Raider.ID, new Raider.Provider((Mob) event.getObject()));
		}
	}
	// 测试
	@SubscribeEvent
	public static void playerEntity(PlayerInteractEvent.EntityInteract event){
		if(event.getHand() != InteractionHand.MAIN_HAND || event.getLevel().isClientSide()) return;
		if(event.getTarget() instanceof  Mob){
			IRaider raider =  event.getTarget().getCapability(Capabilities.RAIDER).orElse(Raider.EMPTY);
			if(raider.getRaid() != null){
				event.getEntity().sendSystemMessage(Component.literal("Raid %d".formatted(raider.getRaid().getId())));
			}else {
				event.getEntity().sendSystemMessage(Component.literal("Raid is null"));
			}
		}
	}
}
