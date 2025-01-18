package com.forget_melody.raid_craft.registries;

import com.forget_melody.raid_craft.IRaidType;
import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.RaidType;
import com.forget_melody.raid_craft.RaiderType;
import com.forget_melody.raid_craft.utils.weight_table.WeightEntry;
import com.forget_melody.raid_craft.utils.weight_table.WeightTable;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.function.Supplier;


public class RaidTypes {
	public static final ResourceKey<Registry<IRaidType>> RAID_TYPE_KEY = ResourceKey.createRegistryKey(new ResourceLocation(RaidCraft.MODID, "raid_type"));
	public static final DeferredRegister<IRaidType> RAID_TYPE = DeferredRegister.create(RAID_TYPE_KEY.location(), RaidCraft.MODID);
	public static final Supplier<IForgeRegistry<IRaidType>> RAID_TYPE_REGISTRY = RAID_TYPE.makeRegistry(() -> new RegistryBuilder<IRaidType>().setName(RAID_TYPE_KEY.location()).setMaxID(1024));
	
	public static final RegistryObject<IRaidType> DEFAULT;
	
	static {
		DEFAULT = register("default", () -> new RaidType(Component.literal("袭击"), Component.literal("袭击-胜利"), Component.literal("袭击-失败"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10, new WeightTable<>(List.of(new WeightEntry<>(new RaiderType(5, ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("minecraft", "husk"))), 5)))));
	}
	
	public static <T extends IRaidType> RegistryObject<T> register(String name, Supplier<T> supplier) {
		RaidCraft.LOGGER.log(Level.INFO, "正在注册%s".formatted(name));
		return RAID_TYPE.register(name, supplier);
	}
	
	public static void register(IEventBus bus){
		RaidCraft.LOGGER.log(Level.INFO, "Mod RaidCraft 注册表正在注册");
		RAID_TYPE.register(bus);
	}
}
