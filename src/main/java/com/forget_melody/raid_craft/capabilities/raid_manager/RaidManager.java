package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.raid.raid.IRaid;
import com.forget_melody.raid_craft.raid.raid.Raid;
import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
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
	private final HashMap<Integer, IRaid> raidMap = new HashMap<Integer, IRaid>();
	
	public RaidManager(ServerLevel level) {
		this.level = level;
	}
	
	@Override
	public ServerLevel getLevel() {
		return level;
	}
	
	@Override
	public void tick() {
		Iterator<IRaid> iterator = raidMap.values().iterator();
		if (iterator.hasNext()) {
			IRaid raid = iterator.next();
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
	public IRaid getRaid(int id) {
		return raidMap.get(id);
	}
	
	public HashMap<Integer, IRaid> getRaids() {
		return raidMap;
	}
	
	@Override
	public IRaid getRaidAtPos(BlockPos blockPos) {
		Iterator<IRaid> raidIterator = raidMap.values().iterator();
		IRaid raid = null;
		while (raidIterator.hasNext()) {
			raid = raidIterator.next();
			if (raid.getCenter().distSqr(blockPos) <= IRaid.RAID_REMOVAL_THRESHOLD_SQR) {
				break;
			}
		}
		return raid;
	}
	
	// 创建一个袭击
	@Override
	public void createRaid(BlockPos blockPos, RaidType raidType) {
		IRaid raid = getRaidAtPos(blockPos);
		if (raid == null) {
			RaidCraft.LOGGER.error("create new raid");
			int id = raidMap.size();
			IRaid raid1 = new Raid(level, id, raidType, blockPos);
			raidMap.put(id, raid1);
		}
		RaidCraft.LOGGER.error("has raid, create is failed");
	}
	
	@Override
	public void createRaid(BlockPos blockPos, ResourceLocation raidType) {
		if(DatapackRegistries.RAID_TYPES.containsKey(raidType)){
			createRaid(blockPos, DatapackRegistries.RAID_TYPES.getValue(raidType));
		}else {
			RaidCraft.LOGGER.error("[RaidManager] Not found {} RaidType", raidType.toString());
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
				   IRaid raid = new Raid(level, (CompoundTag) tag);
				   raidMap.put(raid.getId(), raid);
				   RaidCraft.LOGGER.info("deserializeNBT raid");
			   });
		}
	}
	
	
}
