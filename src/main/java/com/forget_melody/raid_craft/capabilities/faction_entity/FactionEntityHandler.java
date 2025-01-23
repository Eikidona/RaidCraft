package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.faction.IFaction;
import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FactionEntityHandler {
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Mob) {
			event.addCapability(IFactionEntity.ID, new FactionEntityProvider((Mob) event.getObject()));
		}
	}
	
	@SubscribeEvent
	public static void joinFaction(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof Mob) {
			IFactionEntity factionEntity = IFactionEntity.getFactionEntity((Mob) event.getEntity()).get();
			IFaction faction = DatapackRegistries.FACTIONS.getReMapValue(event.getEntity().getType());
			if (faction != null) {
				factionEntity.setFaction(faction);
			}
		}
	}
}
