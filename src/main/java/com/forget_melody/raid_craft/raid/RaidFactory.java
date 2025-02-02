package com.forget_melody.raid_craft.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;

public class RaidFactory {
	public static VillageRaid createVillageRaid(int id, ServerLevel level, Faction faction, BlockPos pos){
		return new VillageRaid(id, level, faction, pos);
	}
	
	public static VillageRaid createVillageRaid(ServerLevel level, CompoundTag tag){
		return new VillageRaid(level, tag);
	}
	
	public static PlayerRaid createPlayerRaid(int id, ServerLevel level, Faction faction, BlockPos pos){
		return new PlayerRaid(id, level, faction, pos);
	}
	
	public static PlayerRaid createPlayerRaid(ServerLevel level, CompoundTag tag){
		return new PlayerRaid(level, tag);
	}
	
	public static Raid createRaid(int id, ServerLevel level, Faction faction, BlockPos pos, RaidType type){
		switch (type){
			case VILLAGE -> {
				return createVillageRaid(id, level, faction, pos);
			}
			case PLAYER -> {
				return createPlayerRaid(id, level, faction, pos);
			}
		}
		return null;
	}
	
	public static Raid createRaid(ServerLevel level, CompoundTag tag){
		RaidType type = RaidType.byName(tag.getString("RaidType"));
		RaidCraft.LOGGER.info("RaidFactory: List: {}", tag.getList("Factions", Tag.TAG_STRING).size());
		switch (type){
			case VILLAGE -> {
				return createVillageRaid(level, tag);
			}
			case PLAYER -> {
				return createPlayerRaid(level, tag);
			}
		}
		return null;
	}
}
