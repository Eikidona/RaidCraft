package com.forget_melody.raid_craft.compat.jade;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.faction_entity.IFactionEntity;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class FactionEntityJadePlugin implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
	
	@Override
	public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
		if (entityAccessor.getServerData().contains("Faction")) {
			iTooltip.add(Component.translatable(
					"jade" + "." + RaidCraft.MODID + "." + "faction",
					entityAccessor.getServerData().getString("Faction")
			));
		}
	}
	
	@Override
	public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
		Faction faction = IFactionEntity.get((Mob) entityAccessor.getEntity()).get().getFaction();
		ResourceLocation id = DataPackRegistries.FACTIONS.getKey(faction);
		compoundTag.putString("Faction", id.toLanguageKey("faction"));
	}
	
	@Override
	public ResourceLocation getUid() {
		return JadePlugin.FACTION_ENTITY;
	}
}
