package com.forget_melody.raid_craft.raid.raid.raid_target;

import com.ibm.icu.text.Collator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public enum RaidTarget {
	
	PLAYER("player"),
	VILLAGE("village");
	
	private String name;
	
	RaidTarget(String name) {
		this.name = name;
	}
	
	public static RaidTarget byName(String name) {
		for (RaidTarget target : RaidTarget.values()) {
			if (target.name.equals(name)) {
				return target;
			}
		}
		return PLAYER;
	}
	
	public String getName() {
		return name;
	}
	
	public static final Codec<RaidTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("raid_target").forGetter(RaidTarget::getName)
	).apply(instance, RaidTarget::byName));
}
