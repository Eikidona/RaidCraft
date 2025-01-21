package com.forget_melody.raid_craft.raid.raid;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import com.forget_melody.raid_craft.utils.weight_table.IWeightTable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.registries.ForgeRegistries;

public class Raid extends AbstractRaid {
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
		BlockPos pos = null;
		for (int i = 1; i < 2; i++) {
			pos = findRandomPos(i, 20);
		}
		while (cumulativeStrength < strength) {
			if (pos == null) {
				RaidCraft.LOGGER.info("Raid pos is null");
				break;
			}
			EntityType<?> entityType = entityTypeTable.getElement();
			if (entityType == null) {
				RaidCraft.LOGGER.info("entityType is null");
				break;
			}
			ResourceLocation entityTypeId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
			IWeightTable<RaiderType> weightTable = raiderTypeTable.filter(raiderType -> raiderType.canApply(entityTypeId));
			RaiderType raiderType = weightTable.getElement();
			RaidCraft.LOGGER.info("正在生成 {} 波, 生物 {}", spawnedWave, entityTypeId);
			Entity entity = entityType.spawn(level, pos, MobSpawnType.EVENT);
			cumulativeStrength += 2;
			if (entity instanceof Mob) {
				IRaider raider = IRaider.getRaider((Mob) entity);
				if (raiderType != null) {
					raider.setRaiderType(raiderType);
					cumulativeStrength += raiderType.getStrength();
				}
				joinRaid(raider);
			}
		}
		playSound(center, raidType.getWaveSoundEvent());
	}
}