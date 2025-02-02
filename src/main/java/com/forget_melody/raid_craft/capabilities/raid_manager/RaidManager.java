package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.Raid;
import com.forget_melody.raid_craft.raid.RaidFactory;
import com.forget_melody.raid_craft.raid.RaidType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RaidManager implements IRaidManager, INBTSerializable<CompoundTag> {
	private final ServerLevel level;
	private final Map<Integer, Raid> raidMap = new HashMap<>();
	
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
				iterator.remove();
			} else {
				raid.tick();
			}
		}
	}
	
	@Override
	public Raid getRaid(int id) {
		return raidMap.get(id);
	}
	
	public Map<Integer, Raid> getRaids() {
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
	public void createRaid(BlockPos blockPos, Faction faction, RaidType type) {
		if (getRaidAtPos(blockPos) == null) {
			if (faction != null) {
				int id = raidMap.size();
				Raid raid = RaidFactory.createRaid(id, level, faction, blockPos, type);
				raidMap.put(id, raid);
			} else {
				RaidCraft.LOGGER.error("Faction is Null!");
			}
		}else {
			RaidCraft.LOGGER.error("has Raid at this");
		}
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag compoundTag = new CompoundTag();
		ListTag listTag = new ListTag();
		raidMap.values().forEach(raid -> {
			CompoundTag raidTag = raid.save();
			listTag.add(raidTag);
			RaidCraft.LOGGER.info("serializeNBT Factions: {}", raidTag);
		});
		compoundTag.put("Raids", listTag);
		return compoundTag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("Raids")) {
			nbt.getList("Raids", ListTag.TAG_COMPOUND)
			   .forEach(tag -> {
				   RaidCraft.LOGGER.info("deserializeNBT Factions: {}", tag);
				   Raid raid = RaidFactory.createRaid(level, (CompoundTag) tag);
				   raidMap.put(raid.getId(), raid);
			   });
		}
	}
	
	
}
