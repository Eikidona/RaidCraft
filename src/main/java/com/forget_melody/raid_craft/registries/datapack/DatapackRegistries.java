package com.forget_melody.raid_craft.registries.datapack;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.registries.datapack.api.Internal.IRegistry;
import com.forget_melody.raid_craft.registries.datapack.api.MergableReload;
import com.forget_melody.raid_craft.registries.datapack.api.NormalReload;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DatapackRegistries {
	// IRegistry
	public static final IRegistry<RaiderType> RAIDER_TYPES = new NormalReload<>("raider_type", RaiderType.CODEC);
	public static final IRegistry<RaidType> RAID_TYPES = new NormalReload<>("raid_type", RaidType.CODEC);
	public static final IRegistry<Faction> FACTIONS = new MergableReload<>("faction", Faction.CODEC, (oldFaction, newFaction) -> {
		// 合并模式：将新实例的派系成员和关系成员添加到旧实例
		newFaction.getEntities().forEach(resourceLocation -> oldFaction.getEntities().add(resourceLocation));
		newFaction.getFactionRelations().getAllies().forEach(location -> oldFaction.getFactionRelations().getAllies().add(location));
		newFaction.getFactionRelations().getEnemies().forEach(location -> oldFaction.getFactionRelations().getEnemies().add(location));
		return oldFaction;
	});
	
	@SubscribeEvent
	public static void onAddReloadListener(AddReloadListenerEvent event) {
		RaidCraft.LOGGER.info("[RaidCraft] ReloadListenerEvent is listening");
		event.addListener((PreparableReloadListener) RAIDER_TYPES);
		event.addListener((PreparableReloadListener) RAID_TYPES);
		event.addListener((PreparableReloadListener) FACTIONS);
	}
}
