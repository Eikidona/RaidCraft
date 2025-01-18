package com.forget_melody.raid_craft;

import com.forget_melody.raid_craft.utils.weight_table.WeightTable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

import java.util.List;

public interface IRaidType {
	ResourceLocation getId();
	Component getRaidDisplay();
	Component getWinDisplay();
	Component getLoseDisplay();
	BossEvent.BossBarColor getColor();
	BossEvent.BossBarOverlay getOverlay();
	WeightTable<RaiderType> getRaiderTypes();
}
