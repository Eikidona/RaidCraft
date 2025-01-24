package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.faction_relations.FactionRelations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;

public class Factions {
	public static final Faction DEFAULT = new Faction(false, new CompoundTag(), new HashSet<>(), FactionRelations.DEFAULT, new ResourceLocation(RaidCraft.MODID, "start"));
	
	public static void register(){
		DatapackRegistries.FACTIONS.register(new ResourceLocation(RaidCraft.MODID, "default"), DEFAULT);
	}
}
