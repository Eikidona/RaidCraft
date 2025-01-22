package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.raid.raider_type.RaiderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Raid extends AbstractRaid {
	protected final Map<RaiderType, Integer> factionEntityTypeCountMap = new HashMap<>();
	
	public Raid(ServerLevel level, int id, RaidType raidType, BlockPos center) {
		super(level, id, raidType, center);
	}
	
	public Raid(ServerLevel level, CompoundTag tag) {
		super(level, tag);
	}
	
	@Override
	protected void updateCenter() {
	
	}
	
	@Override
	protected void spawnWave() {
		spawnedWave++;
		int cumulativeStrength = 0;
		BlockPos pos = findRandomPos(1, 20);
		if (pos == null) {
			RaidCraft.LOGGER.error("Failed to find a valid position.");
			return;
		}
		
		List<RaiderType> raiderTypes = raiderTypeList.stream()
													 .filter(Objects::nonNull)
													 .filter(factionEntityType -> factionEntityType.getMinWave() <= spawnedWave && factionEntityType.getMaxWave() >= spawnedWave)
													 .filter(factionEntityType -> {
														 int count = factionEntityTypeCountMap.getOrDefault(factionEntityType, 0);
														 return count < factionEntityType.getMinSpawned() || count < factionEntityType.getMaxSpawned();
													 })
													 .toList();
		
		if (raiderTypes.isEmpty()) {
			RaidCraft.LOGGER.error("No faction entity types available for this wave.");
			return;
		}
		
		int totalWeight = raiderTypes.stream().mapToInt(RaiderType::getWeight).sum();
		if (totalWeight == 0) {
			RaidCraft.LOGGER.error("Total weight is zero. No entities can be spawned.");
			return;
		}
		
		while (cumulativeStrength < strength) {
			int randomWeight = (int) (Math.random() * totalWeight);
			int cumulativeWeight = 0;
			for (RaiderType raiderType : raiderTypes) {
				cumulativeWeight += raiderType.getWeight();
				if (cumulativeWeight > randomWeight) {
					IRaider raider = raiderType.spawn(level, pos);
					if (raider == null) {
						RaidCraft.LOGGER.error("Failed to spawn entity: {}", raiderType);
						continue;
					}
					// 增加计数
					if (factionEntityTypeCountMap.containsKey(raiderType)) {
						factionEntityTypeCountMap.put(raiderType, factionEntityTypeCountMap.get(raiderType) + 1);
					} else {
						factionEntityTypeCountMap.put(raiderType, 1);
					}
					cumulativeStrength += raiderType.getStrength();
					joinRaid(raider);
					break;
				}
			}
		}
		
		if (raidType.getWaveSoundEvent() != null) {
			playSound(center, raidType.getWaveSoundEvent());
		} else {
			RaidCraft.LOGGER.error("Wave sound event is null. No sound will be played.");
		}
	}
	
	@Override
	public void joinRaid(IRaider raider) {
		super.joinRaid(raider);
		if (factionEntityTypeCountMap.containsKey(raider.getRaiderType())) {
			factionEntityTypeCountMap.put(raider.getRaiderType(), factionEntityTypeCountMap.get(raider.getRaiderType()) + 1);
		} else {
			factionEntityTypeCountMap.put(raider.getRaiderType(), 1);
		}
	}
}