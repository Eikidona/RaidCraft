package com.forget_melody.raid_craft.capabilities.raid_interaction;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.faction.Faction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IRaidInteraction extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MOD_ID, "raid_interaction");
	static IRaidInteraction get(ServerPlayer player) {
		return player.getCapability(Capabilities.RAID_INTERACTION).resolve().get();
	}
	
	int getStrength();
	
	Faction getFaction();
	
	ServerPlayer getPlayer();
	
	int getBadOmenLevel();
	
	void addBadOmen(Faction faction, int duration, int amplifier);
	
	void addBadOmen(Faction faction, int duration);
	
	void clearBadOmen();
	
	@Override
	CompoundTag serializeNBT();
	
	@Override
	void deserializeNBT(CompoundTag nbt);
}
