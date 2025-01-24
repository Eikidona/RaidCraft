package com.forget_melody.raid_craft.raid.patrol_type;

import com.forget_melody.raid_craft.capabilities.patroller.IPatroller;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.Patrol;
import com.forget_melody.raid_craft.raid.patroller_type.PatrollerType;
import com.forget_melody.raid_craft.registries.DatapackRegistries;
import com.forget_melody.raid_craft.utils.weight_table.IWeightEntry;
import com.forget_melody.raid_craft.utils.weight_table.IWeightTable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatrolType {
	public static final Codec<PatrolType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("faction").forGetter(PatrolType::getFactionLocation),
			PatrollerType.CODEC.listOf().fieldOf("patrollers").forGetter(PatrolType::getPatrollerTypeList)
	).apply(instance, PatrolType::new));
	
	private final ResourceLocation factionLocation;
	private final List<PatrollerType> patrollerTypeList;
	
	public PatrolType(ResourceLocation factionLocation, List<PatrollerType> patrollerTypeList) {
		this.factionLocation = factionLocation;
		this.patrollerTypeList = patrollerTypeList;
	}
	
	public ResourceLocation getFactionLocation() {
		return factionLocation;
	}
	
	public Faction getFaction() {
		return DatapackRegistries.FACTIONS.getValue(this.factionLocation);
	}
	
	public ItemStack getBanner() {
		return getFaction().getBanner();
	}
	
	public List<PatrollerType> getPatrollerTypeList() {
		return patrollerTypeList;
	}
	
	@Nullable
	public Patrol createPatrol(int id, ServerLevel level, BlockPos blockpos) {
		if (getPatrollerTypeList().isEmpty()) {
			return null;
		}
		RandomSource random = level.getRandom();
		BlockPos.MutableBlockPos pos = blockpos.mutable();
		boolean isLeader = true;
		IPatroller leader = null;
		Set<IPatroller> members = new HashSet<>();
		int numCurrentSpawn = 0;
		int numTotalSpawn = (int) Math.ceil(level.getCurrentDifficultyAt(pos).getEffectiveDifficulty()) + 1;
		IWeightTable<PatrollerType> weightTable = IWeightTable.of(getPatrollerTypeList().stream().map(patrollerType -> IWeightEntry.of(patrollerType, patrollerType.getWeight())).toList());
		
		IPatroller patroller = null;
		while (numCurrentSpawn < numTotalSpawn) {
			numCurrentSpawn++;
			pos.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY());
			pos.setX(pos.getX() + random.nextInt(5) - random.nextInt(5));
			pos.setZ(pos.getZ() + random.nextInt(5) - random.nextInt(5));
			// 已经检查过列表是否为空 这不需要再判断权重表是否为空
			patroller = weightTable.getElement().spawn(level, pos, this, isLeader);
			if (patroller == null) {
				continue;
			}
			if (isLeader) {
				leader = patroller;
				isLeader = false;
				Mob mob = patroller.getMob();
				ItemStack itemStack = mob.getItemBySlot(EquipmentSlot.HEAD);
				if (!itemStack.isEmpty()) {
					mob.spawnAtLocation(itemStack);
				}
				mob.setItemSlot(EquipmentSlot.HEAD, getBanner());
				mob.setDropChance(EquipmentSlot.HEAD, 2.0F);
			}else {
				members.add(patroller);
			}
		}
		if(leader == null){
			return null;
		}
		return new Patrol(id, this, patroller, members);
	}
}
