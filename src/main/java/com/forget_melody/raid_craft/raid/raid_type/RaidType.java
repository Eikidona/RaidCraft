package com.forget_melody.raid_craft.raid.raid_type;

import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.raid.raider_type.RaiderType;
import com.forget_melody.raid_craft.registries.DatapackRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RaidType {
	public static final Codec<RaidType> CODEC = RecordCodecBuilder.create(raidTypeInstance -> raidTypeInstance.group(
			ResourceLocation.CODEC.fieldOf("faction").forGetter(RaidType::getFactionId),
			Codec.STRING.optionalFieldOf("name", "event.minecraft.raid").forGetter(RaidType::getName),
			Codec.STRING.optionalFieldOf("victory", "event.minecraft.raid.victory").forGetter(RaidType::getVictory),
			Codec.STRING.optionalFieldOf("defeat", "event.minecraft.raid.defeat").forGetter(RaidType::getDefeat),
			Codec.STRING.optionalFieldOf("color", "red").forGetter(RaidType::getColorString),
			Codec.STRING.optionalFieldOf("overlay", "notched_10").forGetter(RaidType::getOverlayString),
			Codec.INT.optionalFieldOf("strength", 20).forGetter(RaidType::getStrength),
			SoundEvent.CODEC.optionalFieldOf("wave_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getWaveSoundEvent),
			SoundEvent.CODEC.optionalFieldOf("victory_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getVictorySoundEvent),
			SoundEvent.CODEC.optionalFieldOf("defeat_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getDefeatSoundEvent),
			ResourceLocation.CODEC.listOf().optionalFieldOf("raiders", new ArrayList<>()).forGetter(RaidType::getFactionEntityTypeLocations)
	).apply(raidTypeInstance, RaidType::new));
	
	private final String name;
	private final String victory;
	private final String defeat;
	private final String color;
	private final String overlay;
	private final int strength;
	private final ResourceLocation faction;
	private final Holder<SoundEvent> waveSoundEvent;
	private final Holder<SoundEvent> victorySoundEvent;
	private final Holder<SoundEvent> defeatSoundEvent;
	private final List<ResourceLocation> raiderTypes;
	
	public RaidType(
			ResourceLocation faction,
			String name,
			String victory,
			String defeat,
			String color,
			String overlay,
			int strength,
			Holder<SoundEvent> waveSoundEvent,
			Holder<SoundEvent> victorySoundEvent,
			Holder<SoundEvent> defeatSoundEvent,
			List<ResourceLocation> raiderTypes
	) {
		this.name = name;
		this.victory = victory;
		this.defeat = defeat;
		this.color = color;
		this.overlay = overlay;
		this.faction = faction;
		this.strength = strength;
		this.waveSoundEvent = waveSoundEvent;
		this.victorySoundEvent = victorySoundEvent;
		this.defeatSoundEvent = defeatSoundEvent;
		this.raiderTypes = raiderTypes;
	}
	
	public ResourceLocation getFactionId() {
		return faction;
	}
	
	public Faction getFaction() {
		return DatapackRegistries.FACTIONS.getValue(faction);
	}
	
	public String getName() {
		return name;
	}
	
	public MutableComponent getNameComponent() {
		return Component.translatable(name);
	}
	
	public MutableComponent getVictoryComponent() {
		return Component.translatable(victory);
	}
	
	public MutableComponent getDefeatComponent() {
		return Component.translatable(defeat);
	}
	
	public BossEvent.BossBarColor getColor() {
		return BossEvent.BossBarColor.byName(color);
	}
	
	public BossEvent.BossBarOverlay getOverlay() {
		return BossEvent.BossBarOverlay.byName(overlay);
	}
	
	public Holder<SoundEvent> getWaveSoundEvent() {
		return waveSoundEvent;
	}
	
	public Holder<SoundEvent> getVictorySoundEvent() {
		return victorySoundEvent;
	}
	
	public Holder<SoundEvent> getDefeatSoundEvent() {
		return defeatSoundEvent;
	}
	
	public String getVictory() {
		return victory;
	}
	
	public String getDefeat() {
		return defeat;
	}
	
	public String getColorString() {
		return color;
	}
	
	public String getOverlayString() {
		return overlay;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public List<ResourceLocation> getFactionEntityTypeLocations(){
		return raiderTypes;
	}
	
	public List<RaiderType> getFactionEntityTypes() {
		return getFactionEntityTypeLocations().stream().map(DatapackRegistries.RAIDER_TYPES::getValue).filter(Objects::nonNull).toList();
	}
}