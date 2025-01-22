package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.IFaction;
import com.forget_melody.raid_craft.faction.faction_relations.FactionRelations;
import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;

public class Factions {
	public static final IFaction GAIA = new Faction(false, new CompoundTag(), new HashSet<>(), FactionRelations.DEFAULT, new ResourceLocation(RaidCraft.MODID, "start"));
	
	public static void register(){
		DatapackRegistries.FACTIONS.register(new ResourceLocation(RaidCraft.MODID, "gaia"), (Faction) GAIA);
	}
}
