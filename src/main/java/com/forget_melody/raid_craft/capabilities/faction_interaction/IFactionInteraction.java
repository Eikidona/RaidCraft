package com.forget_melody.raid_craft.capabilities.faction_interaction;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.faction.Faction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IFactionInteraction extends INBTSerializable<CompoundTag> {
	int NEUTRALITY = 0;
	int HOSTILITY = -10;
	int FRIENDLY = 10;
	
	ResourceLocation ID = new ResourceLocation(RaidCraft.MOD_ID, "faction_interaction");
	
	static IFactionInteraction get(ServerPlayer player){
		return player.getCapability(Capabilities.FACTION_INTERACTION).resolve().get();
	}
	
	void adjustedAllianceValue(Faction faction, int value);
	
	void setAllianceValue(Faction faction, int value);
	
	boolean isAlly(Faction faction);
	
	boolean isEnemy(Faction faction);
	
	int getAllianceValue(Faction faction);
	
	ServerPlayer getPlayer();
	
	@Override
	CompoundTag serializeNBT();
	
	@Override
	void deserializeNBT(CompoundTag nbt);
}
