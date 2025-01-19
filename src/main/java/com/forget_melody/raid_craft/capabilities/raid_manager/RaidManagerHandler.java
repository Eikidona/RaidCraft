package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.raid.Raid;
import com.forget_melody.raid_craft.capabilities.raid_manager.api.RaidManagerHelper;
import com.forget_melody.raid_craft.registries.RaidTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;

import java.util.HashMap;
import java.util.Optional;


@Mod.EventBusSubscriber
public class RaidManagerHandler {
	
	@SubscribeEvent
	public static void registerCapability(RegisterCapabilitiesEvent event) {
		event.register(IRaidManager.class);
	}
	
	@SubscribeEvent
	public static void addCapability(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel) {
			event.addCapability(RaidManager.ID, new RaidManagerProvider((ServerLevel) event.getObject()));
		}
	}
	
	@SubscribeEvent
	public static void tick(TickEvent.LevelTickEvent event) {
		if (event.level.isClientSide()) return;
		Optional<IRaidManager> optional = RaidManagerHelper.get((ServerLevel) event.level);
		optional.ifPresent(IRaidManager::tick);
		
	}
	
	@SubscribeEvent
	public static void test(ServerChatEvent event) {
		if (event.getMessage().contains(Component.literal("raid"))) {
			Optional<IRaidManager> optional = RaidManagerHelper.get(event.getPlayer().serverLevel());
			optional.ifPresent(iRaidManager -> iRaidManager.createRaid(event.getPlayer().blockPosition(), RaidTypes.DEFAULT.get()));
			
		}
		if (event.getMessage().contains(Component.literal("raids"))) {
			Optional<IRaidManager> optional = RaidManagerHelper.get(event.getPlayer().serverLevel());
			optional.ifPresent(iRaidManager -> {
				HashMap<Integer, Raid> map = iRaidManager.getRaids();
				event.getPlayer().sendSystemMessage(Component.literal(String.valueOf(map.size())));
			});
			
		}
		if(event.getMessage().contains(Component.literal("raider"))){
			event.getPlayer().sendSystemMessage(Component.literal("%b".formatted(RegistryManager.ACTIVE.getRegistry(new ResourceLocation(RaidCraft.MODID, "raider_type_data")) == null)));
		}
	}
}
