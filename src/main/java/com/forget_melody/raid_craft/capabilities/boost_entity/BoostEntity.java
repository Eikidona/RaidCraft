package com.forget_melody.raid_craft.capabilities.boost_entity;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.boost.IBoost;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class BoostEntity implements IBoostEntity {
	private final Mob mob;
	private List<IBoost> boosts = new ArrayList<>();
	
	public BoostEntity(Mob mob) {
		this.mob = mob;
	}
	
	@Override
	public Mob getMob() {
		return mob;
	}
	
	@Override
	public void setBoost(List<IBoost> boosts) {
		this.boosts = boosts;
	}
	
	@Override
	public List<IBoost> getBoosts() {
		return boosts;
	}
	
	@Override
	public void addBoost(IBoost boost) {
		boosts.add(boost);
	}
	
	@Override
	public void addBoosts(List<IBoost> boosts) {
		this.boosts.addAll(boosts);
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		ListTag listTag = new ListTag();
		boosts.forEach(boost -> {
			listTag.add(StringTag.valueOf(DataPackRegistries.BOOSTS.getKey(boost).toString()));
		});
		tag.put("Boosts", listTag);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		ListTag listTag = nbt.getList("Boosts", StringTag.TAG_STRING);
		listTag.forEach(tag -> {
			ResourceLocation id = new ResourceLocation(tag.getAsString());
			IBoost boost = DataPackRegistries.BOOSTS.getValue(id);
			if(boost != null){
				boosts.add(boost);
			}else {
				RaidCraft.LOGGER.error("Not found boost: {}", id);
			}
		});
	}
}
