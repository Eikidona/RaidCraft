package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class Factions extends ReloadListener<Faction> {
	private final Map<EntityType<?>, Faction> entityTypeFactionMap = new HashMap<>();
	
	public Factions() {
		super("faction", Faction.CODEC, Faction.DEFAULT);
	}
	
	@Override
	public void register(ResourceLocation name, Faction faction) {
		if (loadedData.containsKey(name)) {
			if(name.equals(RaidCraft.DEFAULT_KEY)){
				RaidCraft.LOGGER.warn("The default faction cannot be modified");
				return;
			}
			if (faction.isReplace()) {
				loadedData.put(name, faction);
			} else {
				Faction originalFaction = loadedData.get(name);
				faction.getEntities().forEach(resourceLocation -> originalFaction.getEntities().add(resourceLocation));
				faction.getFactionRelations().getAllies().forEach(resourceLocation -> originalFaction.getFactionRelations().getAllies().add(resourceLocation));
				faction.getFactionRelations().getEnemies().forEach(resourceLocation -> originalFaction.getFactionRelations().getEnemies().add(resourceLocation));
				faction.getRaidConfig().getRaiderTypes().forEach(raiderType -> originalFaction.getRaidConfig().getRaiderTypes().add(raiderType));
				faction.getPatrolConfig().getPatrollerTypes().forEach(patrollerType -> originalFaction.getPatrolConfig().getPatrollerTypes().add(patrollerType));
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
		return faction == null ? Faction.DEFAULT : faction;
	}
}
