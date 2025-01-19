package com.forget_melody.raid_craft.capabilities.raid_manager;

import com.forget_melody.raid_craft.raid.Raid;
import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.registries.api.RaidTypeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;

public class RaidManager implements IRaidManager, INBTSerializable<CompoundTag> {
	public static ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "raid_manager");
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
			if (raid.isStop()) {
				iterator.remove();
			} else {
				RaidCraft.LOGGER.info("Raid ticking");
				raid.tick();
			}
		}
	}
	
	@Nullable
	@Override
	public Raid getRaid(int id) {
		return raidMap.get(id);
	}
	
	public HashMap<Integer, Raid> getRaids() {
		return raidMap;
	}
	
	@Nullable
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
	public Raid createRaid(BlockPos blockPos, RaidType raidType) {
		Raid raid = getRaidAtPos(blockPos);
		if (raid != null) {
			return raid;
		} else {
			int id = raidMap.size();
			Raid raid1 = new Raid(id, level, blockPos, raidType);
			raidMap.put(id, raid1);
			return raid1;
		}
	}
	
	@Nullable
	@Override
	public Raid createRaid(BlockPos blockPos, ResourceLocation raidType) {
		if(RaidTypeHelper.hasKey(raidType)){
			return createRaid(blockPos, RaidTypeHelper.get(raidType));
		}else {
			RaidCraft.LOGGER.error("[RaidManager] Not found {} RaidType", raidType.toString());
			return null;
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
			   });
		}
	}
	
	
}
