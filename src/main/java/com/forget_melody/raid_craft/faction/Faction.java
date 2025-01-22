package com.forget_melody.raid_craft.faction;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.faction_relations.FactionRelations;
import com.forget_melody.raid_craft.registries.datapack.api.Internal.Replaceable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.*;

public class Faction implements IFaction {
	public static final Codec<Faction> CODEC = RecordCodecBuilder.create(factionInstance -> factionInstance.group(
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(Faction::isReplace),
			CompoundTag.CODEC.optionalFieldOf("banner", new CompoundTag()).forGetter(Faction::getBannerTag),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, ArrayList::new).optionalFieldOf("entities", new HashSet<>()).forGetter(Faction::getEntities),
			FactionRelations.CODEC.optionalFieldOf("relations", FactionRelations.DEFAULT).forGetter(Faction::getFactionRelations),
			ResourceLocation.CODEC.optionalFieldOf("activation_advancement", new ResourceLocation(RaidCraft.MODID, "start")).forGetter(Faction::getActivationAdvancement)
	).apply(factionInstance, Faction::new));
	
	private final boolean replace;
	private final CompoundTag banner;
	private final HashSet<ResourceLocation> entities;
	private final FactionRelations factionRelations;
	private final ResourceLocation activationAdvancement;
	
	public Faction(boolean replace,
				   CompoundTag banner,
				   HashSet<ResourceLocation> entities,
				   FactionRelations relations,
				   ResourceLocation activationAdvancement
	) {
		this.replace = replace;
		this.banner = banner;
		this.entities = entities;
		this.factionRelations = relations;
		this.activationAdvancement = activationAdvancement;
	}
	
	@Override
	public boolean isReplace() {
		return replace;
	}
	
	public CompoundTag getBannerTag() {
		return banner;
	}
	
	@Override
	public ItemStack getBanner() {
		ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
		BlockItem.setBlockEntityData(itemstack, BlockEntityType.BANNER, getBannerTag());
		return itemstack;
	}
	
	@Override
	public HashSet<ResourceLocation> getEntities() {
		return entities;
	}
	
	@Override
	public FactionRelations getFactionRelations() {
		return factionRelations;
	}
	
	@Override
	public ResourceLocation getActivationAdvancement() {
		return activationAdvancement;
	}
}
