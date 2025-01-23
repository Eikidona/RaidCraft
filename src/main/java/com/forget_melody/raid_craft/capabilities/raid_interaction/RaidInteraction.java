package com.forget_melody.raid_craft.capabilities.raid_interaction;

import com.forget_melody.raid_craft.raid.raid_type.RaidType;
import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
import com.forget_melody.raid_craft.world.effect.BadOmenEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public class RaidInteraction implements IRaidInteraction {
	private final ServerPlayer player;
	private int badOmenLevel = 0;
	private RaidType raidType;
	
	public RaidInteraction(ServerPlayer player) {
		this.player = player;
	}
	
	@Override
	public RaidType getRaidType() {
		return raidType;
	}
	
	@Override
	public ServerPlayer getPlayer() {
		return player;
	}
	
	@Override
	public int getBadOmenLevel() {
		return badOmenLevel;
	}
	
	@Override
	public void addBadOmen(RaidType raidType, int amplifier) {
		this.raidType = raidType;
		this.badOmenLevel = amplifier;
		this.player.addEffect(new MobEffectInstance(new BadOmenEffect(), amplifier));
	}
	
	@Override
	public void clearBadOmen() {
		badOmenLevel = 0;
		this.raidType = null;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("BadOmenLevel", this.badOmenLevel);
		if (this.raidType != null) {
			tag.putString("RaidType", DatapackRegistries.RAID_TYPES.getKey(this.raidType).toString());
		}
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.badOmenLevel = nbt.getInt("BadOmenLevel");
		if (nbt.contains("RaidType")) {
			this.raidType = DatapackRegistries.RAID_TYPES.getValue(new ResourceLocation(nbt.getString("RaidType")));
		}
		
	}
}
