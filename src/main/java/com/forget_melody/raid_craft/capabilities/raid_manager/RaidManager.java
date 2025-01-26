package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.raid.Raid;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Iterator;

public class RaidManager implements IRaidManager, INBTSerializable<CompoundTag> {
	private final ServerLevel level;
	private final HashMap<Integer, Raid> raidMap = new HashMap<>();
	
	public RaidManager(ServerLevel level) {
		this.level = level;
	}
	
	@Override
	public ServerLevel getLevel() {
		return level;
	}
	
	@Override
	public void tick() {
		Iterator<Raid> iterator = raidMap.values().iterator();
		if (iterator.hasNext()) {
			Raid raid = iterator.next();
			if (raid.isStopped()) {
//				RaidCraft.LOGGER.info("raid is stopped");
				iterator.remove();
			} else {
//				RaidCraft.LOGGER.info("raid is tick");
				raid.tick();
			}
		}
	}
	
	@Override
	public Raid getRaid(int id) {
		return raidMap.get(id);
	}
	
	public HashMap<Integer, Raid> getRaids() {
		return raidMap;
	}
	
	@Override
	public Raid getRaidAtPos(BlockPos blockPos) {
		Iterator<Raid> raidIterator = raidMap.values().iterator();
		Raid raid = null;
		while (raidIterator.hasNext()) {
			raid = raidIterator.next();
			if (raid.getCenter().distSqr(blockPos) <= Raid.RAID_REMOVAL_THRESHOLD_SQR) {
				break;
			}
		}
		return raid;
	}
	
	// 创建一个袭击
	@Override
	public void createRaid(BlockPos blockPos, Faction faction) {
		Raid raid = getRaidAtPos(blockPos);
		if (raid == null) {
			if(faction != null){
				int id = raidMap.size();
				Raid raid1 = new Raid(level, id, faction, blockPos);
				raidMap.put(id, raid1);
			}else {
				RaidCraft.LOGGER.error("RaidType is Null!");
			}
		}
	}
	
	@Override
	public void createRaid(BlockPos blockPos, ResourceLocation faction) {
		if (DataPackRegistries.FACTIONS.containsKey(faction)) {
			createRaid(blockPos, DataPackRegistries.FACTIONS.getValue(faction));
		} else {
			RaidCraft.LOGGER.error("Not found {} RaidType id", faction.toString());
		}
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag compoundTag = new CompoundTag();
		ListTag listTag = new ListTag();
		raidMap.values().forEach(raid -> listTag.add(raid.save()));
		compoundTag.put("Raids", listTag);
		return compoundTag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("Raids")) {
			nbt.getList("Raids", ListTag.TAG_COMPOUND)
			   .forEach(tag -> {
				   Raid raid = new Raid(level, (CompoundTag) tag);
				   raidMap.put(raid.getId(), raid);
				   RaidCraft.LOGGER.info("deserializeNBT raid");
			   });
		}
	}
	
	
}
