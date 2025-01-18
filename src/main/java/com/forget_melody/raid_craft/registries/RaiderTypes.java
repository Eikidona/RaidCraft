package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.IRaidType;
import com.forget_melody.raid_craft.RaidCraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class RaiderTypes {
	
	public static final ResourceKey<Registry<IRaidType>> RAIDER_TYPE_KEY = ResourceKey.createRegistryKey(new ResourceLocation(RaidCraft.MODID, "raider_type"));
	public static final DeferredRegister<IRaidType> RAIDER_TYPE = DeferredRegister.create(RAIDER_TYPE_KEY.location(), RaidCraft.MODID);
	public static final Supplier<IForgeRegistry<IRaidType>> RAIDER_TYPE_REGISTRY = RAIDER_TYPE.makeRegistry(() -> new RegistryBuilder<IRaidType>().setName(RaidTypes.RAID_TYPE_KEY.location()).setMaxID(1024));
	
	public static void register(IEventBus bus) {
		RAIDER_TYPE.register(bus);
	}
}
