package com.forget_melody.raid_craft.capabilities.raid_interaction;

import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.forget_melody.raid_craft.world.effect.MobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RaidInteraction implements IRaidInteraction {
	private final ServerPlayer player;
	private int badOmenLevel = 0;
	private Faction faction;
	
	public RaidInteraction(ServerPlayer player) {
		this.player = player;
	}
	
	@Override
	public int getStrength() {
		int baseStrength = 0;
		AttributeInstance armorInstance = player.getAttribute(Attributes.ARMOR);
		AttributeInstance damageInstance = player.getAttribute(Attributes.ATTACK_DAMAGE);
		AttributeInstance maxHealthInstance = player.getAttribute(Attributes.MAX_HEALTH);
		double armor = armorInstance == null ? 0.0D : armorInstance.getValue();
		double damage = damageInstance == null ? 0.0D : damageInstance.getValue();
		double maxHealth = maxHealthInstance == null ? 0.0D : maxHealthInstance.getValue() - 20;
		baseStrength += (int) (armor + damage + maxHealth);
		return baseStrength;
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
		this.player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN.get(), duration, amplifier));
	}
	
	@Override
	public void addBadOmen(Faction faction, int duration) {
		addBadOmen(faction, duration, computeBadOmenLevel());
	}
	
	private int computeBadOmenLevel(){
		ServerLevel level = player.serverLevel();
		RandomSource randomSource = level.getRandom();
		Difficulty difficulty = level.getDifficulty();
		switch (difficulty){
			case EASY -> {
				return randomSource.nextInt(3);
			}
			case NORMAL -> {
				return randomSource.nextInt(4);
			}
			case HARD -> {
				return randomSource.nextInt(5);
			}
		}
		return 0;
	}
	
	@Override
	public void clearBadOmen() {
		badOmenLevel = 0;
		this.faction = null;
		player.removeEffect(MobEffects.BAD_OMEN.get());
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
