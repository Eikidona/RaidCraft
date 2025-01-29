package com.forget_melody.raid_craft.raid.patrol;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public class PatrolConfig {
	public static final PatrolConfig DEFAULT = new PatrolConfig(List.of(PatrollerType.DEFAULT));
	
	public static final Codec<PatrolConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PatrollerType.CODEC.listOf().optionalFieldOf("patrollers", new ArrayList<>()).forGetter(PatrolConfig::getPatrollerTypes)
	).apply(instance, PatrolConfig::new));
	
	private final List<PatrollerType> patrollerTypeList;
	
	public PatrolConfig(List<PatrollerType> patrollerTypeList) {
		this.patrollerTypeList = patrollerTypeList;
	}
	
	public List<PatrollerType> getPatrollerTypes() {
		return patrollerTypeList;
	}
	
}
