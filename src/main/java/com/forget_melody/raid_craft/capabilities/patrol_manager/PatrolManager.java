package com.forget_melody.raid_craft.capabilities.patrol_manager;

import com.forget_melody.raid_craft.raid.Patrol;
import com.forget_melody.raid_craft.raid.patrol_type.PatrolType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PatrolManager implements IPatrolManager {
	private final ServerLevel level;
	private final Map<Integer, Patrol> patrolMap = new HashMap<>();
	
	public PatrolManager(ServerLevel level) {
		this.level = level;
	}
	
	@Override
	public Patrol createPatrol(PatrolType patrolType, BlockPos pos) {
		int id = this.patrolMap.size();
		Patrol patrol = patrolType.createPatrol(id, level, pos);
		if(patrol != null){
			this.patrolMap.put(id, patrol);
		}
		return patrol;
	}
	
	@Override
	public @Nullable Patrol getPatrol(int id){
		return patrolMap.get(id);
	}
	
	@Override
	public Map<Integer, Patrol> getPatrols() {
		return patrolMap;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		ListTag listTag = new ListTag();
		for (Patrol patrol : this.patrolMap.values()) {
			listTag.add(patrol.save());
		}
		nbt.put("Patrols", listTag);
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		nbt.getList("Patrols", Tag.TAG_COMPOUND).forEach(tag -> {
			CompoundTag tag1 = (CompoundTag)tag;
			Patrol patrol = new Patrol(tag1);
			this.patrolMap.put(patrol.getId(), patrol);
		});
	}
}
