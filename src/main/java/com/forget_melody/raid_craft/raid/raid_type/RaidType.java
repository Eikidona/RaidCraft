package com.forget_melody.raid_craft.raid.raid_type;

import com.forget_melody.raid_craft.raid.raider.RaiderType;
import com.forget_melody.raid_craft.registries.api.RaidTypeHelper;
import com.forget_melody.raid_craft.registries.api.RaiderTypeHelper;
import com.forget_melody.raid_craft.utils.weight_table.WeightEntry;
import com.forget_melody.raid_craft.utils.weight_table.WeightTable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;

import java.util.List;
import java.util.Objects;

public class RaidType {
	public static final Codec<RaidType> CODEC = RecordCodecBuilder.create(raidTypeInstance -> raidTypeInstance.group(
			Codec.STRING.optionalFieldOf("raidDisplay", "event.minecraft.raid").forGetter(raidType -> raidType.raidTranslationKey),
			Codec.STRING.optionalFieldOf("winDisplay", "event.minecraft.raid.victory").forGetter(raidType -> raidType.winTranslationKey),
			Codec.STRING.optionalFieldOf("loseDisplay", "event.minecraft.raid.defeat").forGetter(raidType -> raidType.loseTranslationKey),
			Codec.STRING.optionalFieldOf("color", "red").forGetter(raidType -> raidType.bossBarColor),
			Codec.STRING.optionalFieldOf("overlay", "notched_10").forGetter(raidType -> raidType.bossBarOverlay),
			Codec.INT.optionalFieldOf("strength", 20).forGetter(raidType -> raidType.strength),
			SoundEvent.CODEC.optionalFieldOf("wave_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getWaveSoundEvent),
			SoundEvent.CODEC.optionalFieldOf("victory_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getVictorySoundEvent),
			SoundEvent.CODEC.optionalFieldOf("defeat_sound", SoundEvents.RAID_HORN).forGetter(RaidType::getDefeatSoundEvent),
			Codec.list(WeightEntry.createCodec(ResourceLocation.CODEC, "raider_type")).fieldOf("raiders").forGetter(raidType -> raidType.raidersList)
	).apply(raidTypeInstance, RaidType::new));
	
	private String raidTranslationKey;
	private String winTranslationKey;
	private String loseTranslationKey;
	private String bossBarColor;
	private String bossBarOverlay;
	private List<WeightEntry<ResourceLocation>> raidersList;
	
	private final MutableComponent raidDisplay;
	private final MutableComponent winDisplay;
	private final MutableComponent loseDisplay;
	private final BossEvent.BossBarColor color;
	private final BossEvent.BossBarOverlay overlay;
	private final WeightTable<RaiderType> raiders;
	private final int strength;
	private final Holder<SoundEvent> waveSoundEvent;
	private final Holder<SoundEvent> victorySoundEvent;
	private final Holder<SoundEvent> defeatSoundEvent;
	
	public RaidType(String raidTranslationKey, String winTranslationKey, String loseTranslationKey, String bossBarColor, String bossBarOverlay, int strength, Holder<SoundEvent> waveSoundEvent, Holder<SoundEvent> victorySoundEvent, Holder<SoundEvent> defeatSoundEvent, List<WeightEntry<ResourceLocation>> list) {
		this.raidTranslationKey = raidTranslationKey;
		this.winTranslationKey = winTranslationKey;
		this.loseTranslationKey = loseTranslationKey;
		this.bossBarColor = bossBarColor;
		this.bossBarOverlay = bossBarOverlay;
		this.raidersList = list;
		
		this.strength = strength;
		
		this.waveSoundEvent = waveSoundEvent;
		this.victorySoundEvent = victorySoundEvent;
		this.defeatSoundEvent = defeatSoundEvent;
		
		
		this.raidDisplay = Component.translatable(raidTranslationKey);
		this.winDisplay = Component.translatable(winTranslationKey);
		this.loseDisplay = Component.translatable(loseTranslationKey);
		this.color = BossEvent.BossBarColor.byName(bossBarColor);
		this.overlay = BossEvent.BossBarOverlay.byName(bossBarOverlay);
		this.raiders = WeightTable.of(list.stream().map(entry -> {
			if (RaiderTypeHelper.hasKey(entry.get())) {
				return WeightEntry.of(RaiderTypeHelper.get(entry.get()), entry.getWeight());
			}
			return null;
		}).filter(Objects::nonNull).toList());
		
	}
	
	public RaidType(MutableComponent raidDisplay, MutableComponent winDisplay, MutableComponent loseDisplay, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay, int strength, Holder<SoundEvent> waveSoundEvent, Holder<SoundEvent> victorySoundEvent, Holder<SoundEvent> defeatSoundEvent, WeightTable<RaiderType> weightTable) {
		this.raidDisplay = raidDisplay;
		this.winDisplay = winDisplay;
		this.loseDisplay = loseDisplay;
		this.color = color;
		this.overlay = overlay;
		this.raiders = weightTable;
		this.strength = strength;
		this.waveSoundEvent = waveSoundEvent;
		this.victorySoundEvent = victorySoundEvent;
		this.defeatSoundEvent = defeatSoundEvent;
	}
	
	public ResourceLocation getId() {
		return RaidTypeHelper.getKey(this);
	}
	
	public MutableComponent getRaidDisplay() {
		return raidDisplay;
	}
	
	public MutableComponent getWinDisplay() {
		return winDisplay;
	}
	
	public MutableComponent getLoseDisplay() {
		return loseDisplay;
	}
	
	public BossEvent.BossBarColor getColor() {
		return color;
	}
	
	public BossEvent.BossBarOverlay getOverlay() {
		return overlay;
	}
	
	public WeightTable<RaiderType> getRaiderTypes() {
		return raiders;
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
}