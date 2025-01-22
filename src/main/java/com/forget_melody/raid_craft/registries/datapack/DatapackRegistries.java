package com.forget_melody.raid_craft.registries.datapack;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.raid.raider_type.RaiderType;
import com.forget_melody.raid_craft.registries.datapack.api.IReMapRegistry;
import com.forget_melody.raid_craft.registries.datapack.api.Internal.IRegistry;
import com.forget_melody.raid_craft.registries.datapack.api.NormalReloadListener;
import com.forget_melody.raid_craft.registries.datapack.api.ReMapMergeableReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class DatapackRegistries {
	// IRegistry
	public static final IRegistry<RaidType> RAID_TYPES = new NormalReloadListener<>("raid_type", RaidType.CODEC);
	public static final IRegistry<RaiderType> RAIDER_TYPES = new NormalReloadListener<>("raider_type", RaiderType.CODEC);
	public static final IReMapRegistry<Faction, EntityType<?>> FACTIONS = new ReMapMergeableReloadListener<>("faction", Faction.CODEC, (oldFaction, newFaction) -> {
		// 合并模式：将新实例的派系成员和关系成员添加到旧实例
		newFaction.getEntities().forEach(location -> oldFaction.getEntities().add(location));
		newFaction.getFactionRelations().getAllies().forEach(location -> oldFaction.getFactionRelations().getAllies().add(location));
		newFaction.getFactionRelations().getEnemies().forEach(location -> oldFaction.getFactionRelations().getEnemies().add(location));
		return oldFaction;
	}, faction -> faction.getEntities().stream().map(ForgeRegistries.ENTITY_TYPES::getValue).filter(Objects::nonNull).collect(Collectors.toSet()));
//	public static final IRegistry<RaiderType> FACTION_ENTITY_TYPES = new NormalReload<>("faction_entity_type", RaiderType.CODEC);
	
	@SubscribeEvent
	public static void onAddReloadListener(AddReloadListenerEvent event) {
		RaidCraft.LOGGER.info("[RaidCraft] ReloadListenerEvent is listening");
		event.addListener((PreparableReloadListener) RAID_TYPES);
		event.addListener((PreparableReloadListener) RAIDER_TYPES);
		event.addListener((PreparableReloadListener) FACTIONS);
//		event.addListener((PreparableReloadListener) FACTION_ENTITY_TYPES);
	}
}
