package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.raid.raid.target.IRaidTarget;
import com.forget_melody.raid_craft.raid.raid.target.PlayerRaidTarget;
import com.forget_melody.raid_craft.raid.raid.target.VillageRaidTarget;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RaidTargets {
	public static final DeferredRegister<IRaidTarget> DEFERRED_REGISTER = DeferredRegister.create(new ResourceLocation(RaidCraft.MOD_ID, "raid_target"), RaidCraft.MOD_ID);
	public static final Supplier<IForgeRegistry<IRaidTarget>> RAID_TARGETS = DEFERRED_REGISTER.makeRegistry(() -> new RegistryBuilder<IRaidTarget>().setMaxID(1024));
	
	public static final RegistryObject<IRaidTarget> VILLAGE = register("village", new VillageRaidTarget());
	public static final RegistryObject<IRaidTarget> PLAYER = register("player", new PlayerRaidTarget());
	
	public static RegistryObject<IRaidTarget> register(String name, IRaidTarget raidTarget){
		return DEFERRED_REGISTER.register(name, () -> raidTarget);
	}
	
	public static void register(IEventBus bus){
		DEFERRED_REGISTER.register(bus);
	}
}
