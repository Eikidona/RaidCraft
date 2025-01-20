package com.forget_melody.raid_craft.faction;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.registries.datapack.api.Internal.Replaceable;
import com.forget_melody.raid_craft.faction.faction_relations.FactionRelations;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class Faction implements Replaceable {
	public static final Codec<Faction> CODEC = RecordCodecBuilder.create(factionInstance -> factionInstance.group(
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(Faction::isReplace),
			CompoundTag.CODEC.optionalFieldOf("banner", new CompoundTag()).forGetter(Faction::getBanner),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, ArrayList::new).optionalFieldOf("entities", new HashSet<>()).forGetter(Faction::getEntities),
			FactionRelations.CODEC.optionalFieldOf("relations", FactionRelations.DEFAULT).forGetter(Faction::getFactionRelations)
	).apply(factionInstance, Faction::new));
	
	private boolean replace;
	private CompoundTag banner;
	private HashSet<ResourceLocation> entities;
	private List<EntityType<?>> entityTypes;
	private FactionRelations factionRelations;
	
	public Faction(boolean replace, CompoundTag banner, HashSet<ResourceLocation> entities, FactionRelations factionRelations) {
		this.replace = replace;
		this.banner = banner;
		this.entities = entities;
		this.factionRelations = factionRelations;
		
	}
	
	@Override
	public boolean isReplace() {
		return replace;
	}
	
	public CompoundTag getBanner() {
		return banner;
	}
	
	public FactionRelations getFactionRelations() {
		return factionRelations;
	}
	
	public HashSet<ResourceLocation> getEntities() {
		return entities;
	}
	
	public List<EntityType<?>> getEntityTypes() {
		if(entityTypes == null){
			entityTypes = new ArrayList<>();
			entities.forEach(location -> {
				if(ForgeRegistries.ENTITY_TYPES.containsKey(location)){
					entityTypes.add(ForgeRegistries.ENTITY_TYPES.getValue(location));
				}else {
					RaidCraft.LOGGER.warn("[RaidCraft] Not found {} entity type", location.toString());
				}
			});
		}
		return entityTypes;
	}
	
	public void setReplace(boolean replace) {
		this.replace = replace;
	}
	
	public void setBanner(CompoundTag banner) {
		this.banner = banner;
	}
	
	public void setEntities(HashSet<ResourceLocation> entities) {
		this.entities = entities;
	}
	
	public void setFactionRelations(FactionRelations factionRelations) {
		this.factionRelations = factionRelations;
	}
	
	public EntityType<?> getRandomEntityType() {
		List<EntityType<?>> list = getEntityTypes();
		int index = (int) (Math.random() * list.size());
		return list.get(index);
	}
}
