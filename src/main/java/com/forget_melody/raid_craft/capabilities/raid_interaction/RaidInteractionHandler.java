package com.forget_melody.raid_craft.capabilities.raid_interaction;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class RaidInteractionHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof ServerPlayer) {
			event.addCapability(IRaidInteraction.ID, new RaidInteractionProvider((ServerPlayer) event.getObject()));
		}
	}
	
	@SubscribeEvent
	public static void clearBadOmen(MobEffectEvent.Remove event) {
		// 状态效果清除同时清空IRaidInteraction所记录的
		if (!(event.getEntity() instanceof ServerPlayer)) return;
		if (event.getEffect() == MobEffects.BAD_OMEN.get()) {
			IRaidInteraction raidInteraction = IRaidInteraction.get((ServerPlayer) event.getEntity()).get();
			raidInteraction.clearBadOmen();
		}
	}
}
