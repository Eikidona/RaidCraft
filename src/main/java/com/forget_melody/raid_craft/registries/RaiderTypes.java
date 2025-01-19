package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.IRaidType;
import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raider.Raider;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class RaiderTypes {
	
	public static final ResourceKey<Registry<RaiderType>> RAIDER_TYPE_KEY = ResourceKey.createRegistryKey(new ResourceLocation(RaidCraft.MODID, "raider_type"));
	public static final DeferredRegister<RaiderType> RAIDER_TYPE = DeferredRegister.create(RAIDER_TYPE_KEY.location(), RaidCraft.MODID);
	public static final Supplier<IForgeRegistry<RaiderType>> RAIDER_TYPE_REGISTRY = RAIDER_TYPE.makeRegistry(() -> new RegistryBuilder<RaiderType>().setName(RaidTypes.RAID_TYPE_KEY.location()).setMaxID(1024));
	
	public static void register(IEventBus bus) {
		RAIDER_TYPE.register(bus);
	}
	
	@SubscribeEvent
	public static void createRegistry(DataPackRegistryEvent.NewRegistry event){
		event.dataPackRegistry(ResourceKey.createRegistryKey(new ResourceLocation(RaidCraft.MODID, "raider_type_data")), RaiderType.CODEC);
	}
}
