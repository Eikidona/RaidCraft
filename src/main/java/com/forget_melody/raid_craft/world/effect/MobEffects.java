package com.forget_melody.raid_craft.world.effect;

import com.forget_melody.raid_craft.RaidCraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MobEffects {
	public static final DeferredRegister<MobEffect> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RaidCraft.MODID);
	
	public static final RegistryObject<MobEffect> BAD_OMEN = register("bad_omen", BadOmenEffect::new);
	
	private static RegistryObject<MobEffect> register(String name, Supplier<MobEffect> supplier) {
		return DEFERRED_REGISTER.register(name, supplier);
	}
	
	public static void register(IEventBus eventBus) {
		DEFERRED_REGISTER.register(eventBus);
	}
}
