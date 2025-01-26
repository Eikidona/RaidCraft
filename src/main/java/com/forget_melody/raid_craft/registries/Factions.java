package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.faction_relations.FactionRelations;
import com.forget_melody.raid_craft.raid.patrol.PatrolConfig;
import com.forget_melody.raid_craft.raid.raid.RaidConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class Factions {
	public static final Faction DEFAULT = new Faction(false, new CompoundTag(), new HashSet<ResourceLocation>(Set.of(new ResourceLocation("minecraft", "husk"))), RaidConfig.DEFAULT, PatrolConfig.DEFAULT, FactionRelations.DEFAULT, new ResourceLocation(RaidCraft.MODID, "start"));
	
	public static void register() {
		DataPackRegistries.FACTIONS.register(new ResourceLocation(RaidCraft.MODID, "default"), DEFAULT);
	}
}
