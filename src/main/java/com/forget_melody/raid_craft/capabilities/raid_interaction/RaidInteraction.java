package com.forget_melody.raid_craft.capabilities.raid_interaction;

import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.forget_melody.raid_craft.world.effect.BadOmenEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class RaidInteraction implements IRaidInteraction {
	private final ServerPlayer player;
	private int badOmenLevel = 0;
	private Faction faction;
	
	public RaidInteraction(ServerPlayer player) {
		this.player = player;
	}
	
	@Override
	public Faction getFaction() {
		return faction;
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
	public void addBadOmen(Faction faction, int duration, int amplifier) {
		this.faction = faction;
		this.badOmenLevel = amplifier;
		this.player.addEffect(new MobEffectInstance(new BadOmenEffect(),duration, amplifier));
	}
	
	@Override
	public void clearBadOmen() {
		badOmenLevel = 0;
		this.faction = null;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("BadOmenLevel", this.badOmenLevel);
		if (this.faction != null) {
			tag.putString("Faction", DataPackRegistries.FACTIONS.getKey(this.faction).toString());
		}
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.badOmenLevel = nbt.getInt("BadOmenLevel");
		if (nbt.contains("Faction")) {
			this.faction = DataPackRegistries.FACTIONS.getValue(new ResourceLocation(nbt.getString("Faction\"")));
		}
		
	}
}
