package com.forget_melody.raid_craft.capabilities.boost_entity;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.boost.IBoost;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IBoostEntity extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MOD_ID, "boost_entity");
	
	static IBoostEntity get(Mob mob) {
		return mob.getCapability(Capabilities.BOOST_ENTITY).resolve().get();
	}
	
	Mob getMob();
	
	void setBoost(List<IBoost> boosts);
	
	List<IBoost> getBoosts();
	
	void addBoost(IBoost boost);
	
	void addBoosts(List<IBoost> boosts);
}
