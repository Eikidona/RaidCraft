package com.forget_melody.raid_craft.raid.raid_type;


import com.forget_melody.raid_craft.IRaidType;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import com.forget_melody.raid_craft.registries.RaidTypes;
import com.forget_melody.raid_craft.utils.weight_table.WeightTable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

public class RaidType implements IRaidType {
	private MutableComponent raidDisplay;
	private MutableComponent winDisplay;
	private MutableComponent loseDisplay;
	private BossEvent.BossBarColor color;
	private BossEvent.BossBarOverlay overlay;
	private WeightTable<RaiderType> raiders;
	
	public RaidType(MutableComponent raidDisplay, MutableComponent winDisplay, MutableComponent loseDisplay, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay, WeightTable<RaiderType> raiders) {
		this.raidDisplay = raidDisplay;
		this.winDisplay = winDisplay;
		this.loseDisplay = loseDisplay;
		this.color = color;
		this.overlay = overlay;
		this.raiders = raiders;
	}
	
	@Override
	public ResourceLocation getId() {
		return RaidTypes.RAID_TYPE_REGISTRY.get().getKey(this);
	}
	
	@Override
	public MutableComponent getRaidDisplay() {
		return raidDisplay;
	}
	
	@Override
	public MutableComponent getWinDisplay() {
		return winDisplay;
	}
	
	@Override
	public MutableComponent getLoseDisplay() {
		return loseDisplay;
	}
	
	@Override
	public BossEvent.BossBarColor getColor() {
		return color;
	}
	
	@Override
	public BossEvent.BossBarOverlay getOverlay() {
		return overlay;
	}
	
	@Override
	public WeightTable<RaiderType> getRaiderTypes() {
		return raiders;
	}
	
}
