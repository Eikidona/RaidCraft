package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.Factions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class FactionReloadListener extends ReloadListener<Faction> {
	private final Map<EntityType<?>, Faction> entityTypeFactionMap = new HashMap<>();
	
	public FactionReloadListener() {
		super("faction", Faction.CODEC);
	}
	
	@Override
	public void register(ResourceLocation name, Faction faction) {
		if (loadedData.containsKey(name)) {
			if (faction.isReplace()) {
				loadedData.put(name, faction);
			} else {
				Faction originalFaction = loadedData.get(name);
				faction.getEntities().forEach(resourceLocation -> originalFaction.getEntities().add(resourceLocation));
				faction.getFactionRelations().getAllies().forEach(resourceLocation -> originalFaction.getFactionRelations().getAllies().add(resourceLocation));
				faction.getFactionRelations().getEnemies().forEach(resourceLocation -> originalFaction.getFactionRelations().getEnemies().add(resourceLocation));
			}
		} else {
			loadedData.put(name, faction);
		}
		Faction faction1 = loadedData.get(name);
		faction1.getEntities().forEach(resourceLocation -> {
			EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
			if (entityType != null) {
				entityTypeFactionMap.put(entityType, faction1);
			}
		});
	}
	
	/**
	 * 具有默认值
	 *
	 * @param entityType EntityType
	 * @return Faction
	 */
	public Faction getFaction(EntityType<?> entityType) {
		Faction faction = entityTypeFactionMap.get(entityType);
		return faction == null ? Factions.DEFAULT : faction;
	}
}
