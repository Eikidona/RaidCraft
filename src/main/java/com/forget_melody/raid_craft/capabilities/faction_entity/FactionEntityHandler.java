package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FactionEntityHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Mob mob) {
			event.addCapability(IFactionEntity.ID, new FactionEntityProvider(mob));
		}
	}
	
	@SubscribeEvent
	public static void joinFaction(EntityJoinLevelEvent event) {
		if(event.getLevel().isClientSide()) return;
		if (event.getEntity() instanceof Mob mob) {
			IFactionEntity factionEntity = IFactionEntity.get(mob);
			Faction faction = DataPackRegistries.FACTIONS.getFaction(mob.getType());
			if (faction != null) {
				factionEntity.setFaction(faction);
			}
		}
	}
	
	@SubscribeEvent
	public static void stopAttackAlly(LivingChangeTargetEvent event){
		if(event.getEntity() instanceof Mob mob && event.getNewTarget() instanceof Mob target){
			IFactionEntity factionEntity = IFactionEntity.get(mob);
			if(factionEntity.isFriendly(target)){
				event.setCanceled(true);
			}
		}
	}
}
