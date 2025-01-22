package com.forget_melody.raid_craft.faction;

import com.forget_melody.raid_craft.faction.faction_relations.FactionRelations;
import com.forget_melody.raid_craft.registries.datapack.api.Internal.Replaceable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;

public interface IFaction extends Replaceable {
	boolean isReplace();
	
	ItemStack getBanner();
	
	HashSet<ResourceLocation> getEntities();
	
	FactionRelations getFactionRelations();
	
	ResourceLocation getActivationAdvancement();
}
