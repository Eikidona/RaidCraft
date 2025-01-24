package com.forget_melody.raid_craft.raid.raider_type;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class RaiderType {
	public static final Codec<RaiderType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("entity_type").forGetter(RaiderType::getEntityType),
			CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(RaiderType::getTag),
			Codec.INT.optionalFieldOf("weight", 5).forGetter(RaiderType::getWeight),
			Codec.INT.optionalFieldOf("strength", 5).forGetter(RaiderType::getStrength),
			Codec.INT.optionalFieldOf("minWave", 0).forGetter(RaiderType::getMinWave),// 最小生成波次
			Codec.INT.optionalFieldOf("maxWave", 999).forGetter(RaiderType::getMaxWave), // 最大生成波次
			Codec.INT.optionalFieldOf("minSpawned", 5).forGetter(RaiderType::getMinSpawned), // 波次内最少生成数
			Codec.INT.optionalFieldOf("maxSpawned", 999).forGetter(RaiderType::getMaxSpawned) // 波次内最大生成数
	).apply(instance, RaiderType::new));
	
	private final ResourceLocation entityType;
	private final CompoundTag tag;
	private final int weight;
	private final int strength;
	private final int minWave;
	private final int maxWave;
	private final int minSpawned;
	private final int maxSpawned;
	
	public RaiderType(ResourceLocation entityType, CompoundTag tag, int weight, int strength, int minWave, int maxWave, int minSpawned, int maxWaveSpawned) {
		this.entityType = entityType;
		this.tag = tag;
		this.weight = weight;
		this.strength = strength;
		this.minWave = minWave;
		this.maxWave = maxWave;
		this.minSpawned = minSpawned;
		this.maxSpawned = maxWaveSpawned;
	}
	
	public ResourceLocation getEntityType() {
		return entityType;
	}
	
	public CompoundTag getTag() {
		return tag;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public int getMinWave() {
		return minWave;
	}
	
	public int getMinSpawned() {
		return minSpawned;
	}
	
	public int getMaxWave() {
		return maxWave;
	}
	
	public int getMaxSpawned() {
		return maxSpawned;
	}
	
	@Nullable
	public IRaider spawn(ServerLevel level, BlockPos pos) {
		EntityType<?> entityType1 = ForgeRegistries.ENTITY_TYPES.getValue(entityType);
		if(entityType1 == null){
			return null;
		}
		Entity entity = entityType1.spawn(level, pos, MobSpawnType.EVENT);
		if (entity instanceof Mob) {
			IRaider raider = IRaider.getRaider((Mob) entity).get();
			raider.setRaiderType(this);
			return raider;
		}
		return null;
	}
}
