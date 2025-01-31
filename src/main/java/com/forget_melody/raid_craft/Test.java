package com.forget_melody.raid_craft;

import com.forget_melody.raid_craft.capabilities.patrol_manager.IPatrolManager;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.config.Config;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.forget_melody.raid_craft.registries.RaidTargets;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Test {
	@SubscribeEvent
	public static void test(ServerChatEvent event) {
		if (event.getMessage().contains(Component.literal("raid"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Try Create Raid"));
			IRaidManager.get(event.getPlayer().serverLevel()).createRaid(event.getPlayer().blockPosition(), Faction.DEFAULT, RaidTargets.VILLAGE.get());
		}
		if (event.getMessage().contains(Component.literal("raids"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Raids" + IRaidManager.get(event.getPlayer().serverLevel()).getRaids().size()));
		}
		if (event.getMessage().contains(Component.literal("config"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Config: " + Config.PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get()));
		}
		if (event.getMessage().contains(Component.literal("patrol"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Try Spawn Patrol"));
			IPatrolManager.get(event.getPlayer().serverLevel()).createPatrol(Faction.DEFAULT, event.getPlayer().blockPosition());
		}
		if (event.getMessage().contains(Component.literal("patrols"))) {
			int count = IPatrolManager.get(event.getPlayer().serverLevel()).getPatrols().size();
			event.getPlayer().sendSystemMessage(Component.literal("Patrols: " + count));
		}
		if (event.getMessage().contains(Component.literal("spawn"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Try Spawn FactionEntityType"));
			DataPackRegistries.Faction_ENTITY_TYPES.getValue(new ResourceLocation(RaidCraft.MOD_ID, "zombie")).spawn(event.getPlayer().serverLevel(), event.getPlayer().blockPosition(), MobSpawnType.EVENT);
		}
	}
	
}
