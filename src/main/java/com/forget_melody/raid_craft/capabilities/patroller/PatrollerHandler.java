package com.forget_melody.raid_craft.capabilities.patroller;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.capabilities.faction_interaction.IFactionInteraction;
import com.forget_melody.raid_craft.capabilities.raid_interaction.IRaidInteraction;
import com.forget_melody.raid_craft.faction.Faction;
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
				RaidCraft.LOGGER.info("Effect Handler isPatrolLeader");
				/**
				 * todo {BadOmen}
				 */
				Faction faction = IFactionEntity.get(patroller.getMob()).getFaction();
				IFactionInteraction factionInteraction = IFactionInteraction.get(player);
				IRaidInteraction raidInteraction = IRaidInteraction.get(player);
				raidInteraction.addBadOmen(faction, 6000);
				factionInteraction.adjustedAllianceValue(faction, -10);
//				if(factionInteraction.getAllianceValue(faction) <= IFactionInteraction.HOSTILITY){
//					IRaidManager.get(player.serverLevel()).createRaid(player.blockPosition(), faction, RaidType.PLAYER);
//				}
			}
		}
	}
}
