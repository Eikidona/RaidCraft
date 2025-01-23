package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.Capabilities;
import com.forget_melody.raid_craft.raid.raid.IRaid;
import com.forget_melody.raid_craft.raid.raider_type.RaiderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@AutoRegisterCapability
public interface IRaider extends INBTSerializable<CompoundTag> {
	ResourceLocation ID = new ResourceLocation(RaidCraft.MODID, "raider");
	
	static Optional<IRaider> getRaider(Mob mob) {
		return mob.getCapability(Capabilities.RAIDER).resolve();
	}
	
	void setRaiderType(RaiderType raiderType);
	
	boolean hasActiveRaid();
	
	@Nullable RaiderType getRaiderType();
	
	boolean isLeader();
	
	Mob getMob();
	
	@Nullable IRaid getRaid();
	
	boolean canJoinRaid();
	
	void setRaid(IRaid raid);
	
	void setLeader(boolean leader);
	
	int getWave();
}
