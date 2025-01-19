package com.forget_melody.raid_craft.datagen.raid_type;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RaidTypeProvider implements DataProvider {
	private final String modid;
	
	public RaidTypeProvider(String modid) {
		this.modid = modid;
	}
	
	@Override
	public CompletableFuture<?> run(CachedOutput pOutput) {
		return null;
	}
	
	@Override
	public @NotNull String getName() {
		return "Raid Types" + modid;
	}
}
