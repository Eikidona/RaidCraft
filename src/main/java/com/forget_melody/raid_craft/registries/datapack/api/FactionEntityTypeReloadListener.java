package com.forget_melody.raid_craft.registries.datapack.api;

import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionEntityTypeReloadListener extends ReloadListener<FactionEntityType> {
	private final Map<EntityType<?>, List<FactionEntityType>> entityTypeListMap = new HashMap<>();
	
	public FactionEntityTypeReloadListener() {
		super("faction_entity_type", FactionEntityType.CODEC);
	}
	
	@Override
	public void register(ResourceLocation name, FactionEntityType factionEntityType) {
		EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(factionEntityType.getEntityTypeLocation());
		if (entityType == null) {
			return;
		}
		super.register(name, factionEntityType);
		List<FactionEntityType> list = entityTypeListMap.computeIfAbsent(entityType, entityType1 -> new ArrayList<>());
		list.add(factionEntityType);
	}
	
	/**
	 * 可能为null
	 *
	 * @param entityType EntityType
	 * @return FactionEntityTypeList
	 */
	@Nullable
	public List<FactionEntityType> getFactionEntityType(EntityType<?> entityType) {
		return entityTypeListMap.get(entityType);
	}
}
