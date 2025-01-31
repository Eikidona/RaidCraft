package com.forget_melody.raid_craft.capabilities.faction_entity;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IFactionEntity extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MOD_ID, "faction_entity");
	
	static IFactionEntity get(Mob mob) {
		return mob.getCapability(Capabilities.FACTION_ENTITY).resolve().get();
	}
	
	Faction getFaction();
	
	FactionEntityType getFactionEntityType();
	
	Mob getMob();
	
	boolean isEnemy(Faction faction);
	
	boolean isAlly(Faction faction);
	
	boolean isFriendly(Mob mob);
	
	boolean isHostility(Mob mob);
	
	void setFaction(Faction faction);
	
	void setFactionEntityType(FactionEntityType factionEntityType);
}
