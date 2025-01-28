package com.forget_melody.raid_craft.capabilities.raid_interaction;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.faction.Faction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.Optional;

@AutoRegisterCapability
public interface IRaidInteraction extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "raid_interaction");
	static Optional<IRaidInteraction> get(ServerPlayer player) {
		return player.getCapability(Capabilities.RAID_INTERACTION).resolve();
	}
	
	Faction getFaction();
	
	ServerPlayer getPlayer();
	
	int getBadOmenLevel();
	
	void addBadOmen(Faction faction, int duration, int amplifier);
	
	void clearBadOmen();
	
	@Override
	CompoundTag serializeNBT();
	
	@Override
	void deserializeNBT(CompoundTag nbt);
}
