package com.forget_melody.raid_craft;

import com.forget_melody.raid_craft.capabilities.patrol_manager.IPatrolManager;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.config.Config;
import com.forget_melody.raid_craft.raid.patrol_type.PatrolType;
import com.forget_melody.raid_craft.registries.DatapackRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Test {
	@SubscribeEvent
	public static void test(ServerChatEvent event) {
//		if(event.getMessage().contains(Component.literal("raid_type"))){
//			event.getPlayer().sendSystemMessage(Component.literal("Size %d".formatted(Reloads.RAID_TYPES.getLoadedData().size())));
//		}
		if (event.getMessage().contains(Component.literal("raid"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Try Create Raid"));
			IRaidManager.get(event.getPlayer().serverLevel()).get().createRaid(event.getPlayer().blockPosition(), new ResourceLocation("raid_craft", "default"));
		}
		if (event.getMessage().contains(Component.literal("config"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Config: " + Config.PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get()));
		}
		if (event.getMessage().contains(Component.literal("patrol"))) {
			event.getPlayer().sendSystemMessage(Component.literal("Try Spawn Patrol"));
			ResourceLocation id = new ResourceLocation(RaidCraft.MODID, "default");
			PatrolType patrolType = DatapackRegistries.PATROL_TYPES.getValue(id);
			if (patrolType == null) {
				RaidCraft.LOGGER.error("Not found PatrolType id {}", id);
				return;
			}
			IPatrolManager.get(event.getPlayer().serverLevel()).get().createPatrol(patrolType, event.getPlayer().blockPosition());
		}
		if (event.getMessage().contains(Component.literal("patrols"))) {
			int count = IPatrolManager.get(event.getPlayer().serverLevel()).get().getPatrols().size();
			event.getPlayer().sendSystemMessage(Component.literal("Patrols: " + count));
		}
	}
}
