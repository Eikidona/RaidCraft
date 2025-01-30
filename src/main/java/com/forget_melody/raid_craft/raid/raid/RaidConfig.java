package com.forget_melody.raid_craft.raid.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;

import java.util.ArrayList;

public class RaidConfig {
	public static final RaidConfig DEFAULT = new RaidConfig("event.minecraft.raid", "event.minecraft.raid.victory", "event.minecraft.raid.defeat", "red", "notched_10", 20, new ArrayList<>(), SoundEvents.RAID_HORN, SoundEvents.RAID_HORN, SoundEvents.RAID_HORN);
	
	public static final Codec<RaidConfig> CODEC = RecordCodecBuilder.create(raidTypeInstance -> raidTypeInstance.group(
			Codec.STRING.optionalFieldOf("name", "event.minecraft.raid").forGetter(RaidConfig::getName),
			Codec.STRING.optionalFieldOf("victory", "event.minecraft.raid.victory").forGetter(RaidConfig::getVictory),
			Codec.STRING.optionalFieldOf("defeat", "event.minecraft.raid.defeat").forGetter(RaidConfig::getDefeat),
			Codec.STRING.optionalFieldOf("color", "red").forGetter(RaidConfig::getColorString),
			Codec.STRING.optionalFieldOf("overlay", "notched_10").forGetter(RaidConfig::getOverlayString),
			Codec.INT.optionalFieldOf("strength", 20).forGetter(RaidConfig::getStrength),
			RaiderType.CODEC.listOf().xmap(ArrayList::new, ArrayList::new).optionalFieldOf("raiders", new ArrayList<>()).forGetter(RaidConfig::getRaiderTypes),
			SoundEvent.CODEC.optionalFieldOf("wave_sound", SoundEvents.RAID_HORN).forGetter(RaidConfig::getWaveSoundEvent),
			SoundEvent.CODEC.optionalFieldOf("victory_sound", SoundEvents.RAID_HORN).forGetter(RaidConfig::getVictorySoundEvent),
			SoundEvent.CODEC.optionalFieldOf("defeat_sound", SoundEvents.RAID_HORN).forGetter(RaidConfig::getDefeatSoundEvent)
	).apply(raidTypeInstance, RaidConfig::new));
	
	private final String name;
	private final String victory;
	private final String defeat;
	private final String color;
	private final String overlay;
	private final int strength;
	private final ArrayList<RaiderType> raiderTypes;
	private final Holder<SoundEvent> waveSoundEvent;
	private final Holder<SoundEvent> victorySoundEvent;
	private final Holder<SoundEvent> defeatSoundEvent;
	
	public RaidConfig(
			String name,
			String victory,
			String defeat,
			String color,
			String overlay,
			int strength,
			ArrayList<RaiderType> raiderTypes,
			Holder<SoundEvent> waveSoundEvent,
			Holder<SoundEvent> victorySoundEvent,
			Holder<SoundEvent> defeatSoundEvent
	) {
		this.name = name;
		this.victory = victory;
		this.defeat = defeat;
		this.color = color;
		this.overlay = overlay;
		this.strength = strength;
		this.raiderTypes = raiderTypes;
		this.waveSoundEvent = waveSoundEvent;
		this.victorySoundEvent = victorySoundEvent;
		this.defeatSoundEvent = defeatSoundEvent;
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
	
	public ArrayList<RaiderType> getRaiderTypes() {
		return raiderTypes;
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
	
}