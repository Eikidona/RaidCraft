package com.forget_melody.raid_craft.raid.raider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

public class RaiderType {
	public static final Codec<RaiderType> CODEC = RecordCodecBuilder.create(raiderTypeInstance -> raiderTypeInstance.group(
			Codec.INT.optionalFieldOf("strength", 5).forGetter(RaiderType::getStrength),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, ArrayList::new).optionalFieldOf("black_list", new HashSet<>()).forGetter(RaiderType::getBlackList),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, ArrayList::new).optionalFieldOf("white_list", new HashSet<>()).forGetter(RaiderType::getBlackList)
	).apply(raiderTypeInstance, RaiderType::new));
	
	private final int strength;
	private final HashSet<ResourceLocation> blackList;
	private final HashSet<ResourceLocation> whiteList;
	
	public RaiderType(int strength, HashSet<ResourceLocation> blackList, HashSet<ResourceLocation> whiteList) {
		this.strength = strength;
		this.blackList = blackList;
		this.whiteList = whiteList;
	}
	
	public int getStrength() {
		return strength;
	}
	
	@Nullable
	public HashSet<ResourceLocation> getBlackList() {
		return blackList;
	}
	
	private boolean inBlackList(ResourceLocation name) {
		if (blackList == null) {
			return false;
		} else {
			return blackList.contains(name);
		}
	}
	
	private boolean inWhiteList(ResourceLocation name) {
		if (whiteList == null) {
			return false;
		} else {
			return whiteList.contains(name);
		}
	}
	
	public boolean canApply(ResourceLocation name) {
		if(inWhiteList(name)){
			return true;
		}else {
			if(!inBlackList(name)){
				return true;
			}
		}
		return false;
	}
}