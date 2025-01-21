package com.forget_melody.raid_craft.raid.raid_type;

import com.forget_melody.raid_craft.registries.datapack.DatapackRegistries;
import com.forget_melody.raid_craft.faction.Faction;
import com.forget_melody.raid_craft.utils.weight_table.IWeightEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;

import java.util.*;

public class RaidType {
	public static final Codec<RaidType> CODEC = RecordCodecBuilder.create(raidTypeInstance -> raidTypeInstance.group(
			ResourceLocation.CODEC.fieldOf("faction").forGetter(RaidType::getFactionId),
			Codec.STRING.optionalFieldOf("raidName", "event.minecraft.raid").forGetter(RaidType::getRaidName),
			Codec.STRING.optionalFieldOf("victory", "event.minecraft.raid.victory").forGetter(RaidType::getVictory),
			Codec.STRING.optionalFieldOf("defeat", "event.minecraft.raid.defeat").forGetter(RaidType::getDefeat),
			Codec.STRING.optionalFieldOf("color", "red").forGetter(RaidType::getColorId),
			Codec.STRING.optionalFieldOf("overlay", "notched_10").forGetter(RaidType::getOverlayId),
			Codec.INT.optionalFieldOf("strength", 20).forGetter(RaidType::getStrength),
			SoundEvent.CODEC.optionalFieldOf("wave_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getWaveSoundEvent),
			SoundEvent.CODEC.optionalFieldOf("victory_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getVictorySoundEvent),
			SoundEvent.CODEC.optionalFieldOf("defeat_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getDefeatSoundEvent),
			RaiderEntry.CODEC.listOf().optionalFieldOf("raiders", new ArrayList<>()).forGetter(RaidType::getRaiders),
			RaiderTypeEntry.CODEC.listOf().optionalFieldOf("raider_types", new ArrayList<>()).forGetter(RaidType::getRaiderTypes)
	).apply(raidTypeInstance, RaidType::new));
	
	private final String raidName;
	private final String victory;
	private final String defeat;
	private final String colorId;
	private final String overlayId;
	private final int strength;
	private final List<RaiderEntry> raiders;
	private final List<RaiderTypeEntry> raiderTypes;
	private final ResourceLocation factionId;
	private final MutableComponent nameComponent;
	private final MutableComponent victoryComponent;
	private final MutableComponent defeatComponent;
	private final BossEvent.BossBarColor color;
	private final BossEvent.BossBarOverlay overlay;
	private final Holder<SoundEvent> waveSoundEvent;
	private final Holder<SoundEvent> victorySoundEvent;
	private final Holder<SoundEvent> defeatSoundEvent;
	private Faction faction;
	
	public RaidType(
			ResourceLocation factionName,
			String raidName,
			String victory,
			String defeat,
			String colorId,
			String overlayId,
			int strength,
			Holder<SoundEvent> waveSoundEvent,
			Holder<SoundEvent> victorySoundEvent,
			Holder<SoundEvent> defeatSoundEvent,
			List<RaiderEntry> raiders,
			List<RaiderTypeEntry> raiderTypes
	) {
		this.raidName = raidName;
		this.victory = victory;
		this.defeat = defeat;
		this.colorId = colorId;
		this.overlayId = overlayId;
		this.factionId = factionName;
		this.strength = strength;
		this.waveSoundEvent = waveSoundEvent;
		this.victorySoundEvent = victorySoundEvent;
		this.defeatSoundEvent = defeatSoundEvent;
		this.nameComponent = Component.translatable(raidName);
		this.victoryComponent = Component.translatable(victory);
		this.defeatComponent = Component.translatable(defeat);
		this.color = BossEvent.BossBarColor.byName(colorId);
		this.overlay = BossEvent.BossBarOverlay.byName(overlayId);
		this.raiders = raiders;
		this.raiderTypes = raiderTypes;
	}
	
	public ResourceLocation getFactionId() {
		return factionId;
	}
	
	public Faction getFaction() {
		if (faction == null) {
			faction = DatapackRegistries.FACTIONS.getValue(factionId);
		}
		return faction;
	}
	
	public String getRaidName() {
		return raidName;
	}
	
	public MutableComponent getNameComponent() {
		return nameComponent;
	}
	
	public MutableComponent getVictoryComponent() {
		return victoryComponent;
	}
	
	public MutableComponent getDefeatComponent() {
		return defeatComponent;
	}
	
	public BossEvent.BossBarColor getColor() {
		return color;
	}
	
	public BossEvent.BossBarOverlay getOverlay() {
		return overlay;
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
	
	public String getColorId() {
		return colorId;
	}
	
	public String getOverlayId() {
		return overlayId;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public List<RaiderEntry> getRaiders() {
		return raiders;
	}
	
	public List<RaiderTypeEntry> getRaiderTypes() {
		return raiderTypes;
	}
	
	public record RaiderEntry(ResourceLocation entityType, int weight) {
		public static final Codec<RaiderEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("entity").forGetter(RaiderEntry::entityType),
				Codec.INT.fieldOf("weight").forGetter(RaiderEntry::weight)
		).apply(instance, RaiderEntry::new));
		
	}
	
	public record RaiderTypeEntry(ResourceLocation raiderType, int weight) {
		public static final Codec<RaiderTypeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("raider_type").forGetter(RaiderTypeEntry::raiderType),
				Codec.INT.fieldOf("weight").forGetter(RaiderTypeEntry::weight)
		).apply(instance, RaiderTypeEntry::new));
		
	}
	
}