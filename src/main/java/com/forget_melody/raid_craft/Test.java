package com.forget_melody.raid_craft;

import com.forget_melody.raid_craft.capabilities.patrol_manager.IPatrolManager;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.raid.RaidType;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Test {
	@SubscribeEvent
	public static void test(ServerChatEvent event) {
		if (event.getMessage().contains(Component.literal("village"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Try Create Village Target Raid"));
			IRaidManager.get(event.getPlayer().serverLevel()).createRaid(event.getPlayer().blockPosition(), DataPackRegistries.FACTIONS.getValue(new ResourceLocation(RaidCraft.MOD_ID, "undead")), RaidType.VILLAGE);
		}
		if (event.getMessage().contains(Component.literal("player"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Try Create Village Destroy Raid"));
			IRaidManager.get(event.getPlayer().serverLevel()).createRaid(event.getPlayer().blockPosition(), DataPackRegistries.FACTIONS.getValue(new ResourceLocation(RaidCraft.MOD_ID, "undead")), RaidType.PLAYER);
		}
		if (event.getMessage().contains(Component.literal("raids"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Raids" + IRaidManager.get(event.getPlayer().serverLevel()).getRaids().size()));
		}
		if (event.getMessage().contains(Component.literal("patrol"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Try Spawn Patrol"));
			IPatrolManager.get(event.getPlayer().serverLevel()).createPatrol(DataPackRegistries.FACTIONS.getValue(new ResourceLocation(RaidCraft.MOD_ID, "undead")), event.getPlayer().blockPosition());
		}
		if (event.getMessage().contains(Component.literal("patrols"))) {
			int count = IPatrolManager.get(event.getPlayer().serverLevel()).getPatrols().size();
			event.getPlayer().sendSystemMessage(Component.literal("Patrols: " + count));
		}
		if (event.getMessage().contains(Component.literal("spawn"))) {
			ServerLevel level = event.getPlayer().serverLevel();
			BlockPos pos = event.getPlayer().blockPosition();
			event.getPlayer().sendSystemMessage(Component.literal("Spawn FactionEntityType"));
			DataPackRegistries.Faction_ENTITY_TYPES.getValue(new ResourceLocation(RaidCraft.MOD_ID, "undead/zombie")).spawn(level, pos, MobSpawnType.EVENT);
		}
	}
	
}
