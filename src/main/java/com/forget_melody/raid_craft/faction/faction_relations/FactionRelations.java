package com.forget_melody.raid_craft.faction.faction_relations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;

public class FactionRelations {
	public static final Codec<FactionRelations> CODEC = RecordCodecBuilder.create(factionRelationsInstance -> factionRelationsInstance.group(
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, ArrayList::new).optionalFieldOf("allies", new HashSet<>()).forGetter(FactionRelations::getAllies),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, ArrayList::new).optionalFieldOf("enemies", new HashSet<>()).forGetter(FactionRelations::getEnemies)
	).apply(factionRelationsInstance, FactionRelations::new));
	
	public static final FactionRelations DEFAULT = new FactionRelations(new HashSet<>(), new HashSet<>());
	
	private final HashSet<ResourceLocation> allies;
	private final HashSet<ResourceLocation> enemies;
	
	public FactionRelations(HashSet<ResourceLocation> allies, HashSet<ResourceLocation> enemies) {
		this.allies = allies;
		this.enemies = enemies;
	}
	
	public HashSet<ResourceLocation> getAllies() {
		return allies;
	}
	
	public HashSet<ResourceLocation> getEnemies() {
		return enemies;
	}
}
