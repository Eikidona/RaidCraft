package com.forget_melody.raid_craft.faction;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.faction.faction_entity_type.FactionEntityType;
import com.forget_melody.raid_craft.faction.faction_relations.FactionRelations;
import com.forget_melody.raid_craft.raid.patrol.PatrolConfig;
import com.forget_melody.raid_craft.raid.raid.RaidConfig;
import com.forget_melody.raid_craft.registries.DataPackRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Faction {
	public static final Codec<Faction> CODEC = RecordCodecBuilder.create(factionInstance -> factionInstance.group(
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(Faction::isReplace),
			CompoundTag.CODEC.optionalFieldOf("banner", new CompoundTag()).forGetter(Faction::getBannerTag),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, ArrayList::new).optionalFieldOf("entities", new HashSet<>()).forGetter(Faction::getEntities),
			RaidConfig.CODEC.optionalFieldOf("raid_config", RaidConfig.DEFAULT).forGetter(Faction::getRaidConfig),
			PatrolConfig.CODEC.optionalFieldOf("patrol_config", PatrolConfig.DEFAULT).forGetter(Faction::getPatrolConfig),
			FactionRelations.CODEC.optionalFieldOf("relations", FactionRelations.DEFAULT).forGetter(Faction::getFactionRelations),
			ResourceLocation.CODEC.optionalFieldOf("activation_advancement", new ResourceLocation(RaidCraft.MODID, "start")).forGetter(Faction::getActivationAdvancement)
	).apply(factionInstance, Faction::new));
	
	private final boolean replace;
	private final CompoundTag banner;
	private final RaidConfig raidConfig;
	private final PatrolConfig patrolConfig;
	private final List<FactionEntityType> raiderTypeList = new ArrayList<>();
	private final HashSet<ResourceLocation> entities;
	private final FactionRelations factionRelations;
	private final ResourceLocation activationAdvancement;
	
	public Faction(boolean replace,
				   CompoundTag banner,
				   HashSet<ResourceLocation> entities,
				   RaidConfig raidConfig,
				   PatrolConfig patrolConfig,
				   FactionRelations relations,
				   ResourceLocation activationAdvancement
	) {
		this.replace = replace;
		this.banner = banner;
		this.entities = entities;
		this.raidConfig = raidConfig;
		this.patrolConfig = patrolConfig;
		this.factionRelations = relations;
		this.activationAdvancement = activationAdvancement;
	}
	
	public boolean isReplace() {
		return replace;
	}
	
	public CompoundTag getBannerTag() {
		return banner;
	}
	
	public ItemStack getBanner() {
		ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
		BlockItem.setBlockEntityData(itemstack, BlockEntityType.BANNER, getBannerTag());
		return itemstack;
	}
	
	public HashSet<ResourceLocation> getEntities() {
		return entities;
	}
	
	public RaidConfig getRaidConfig() {
		return raidConfig;
	}
	
	public PatrolConfig getPatrolConfig() {
		return patrolConfig;
	}
	
	/**
	 * 在注册完成之前不要调用 注册完成后它的内容不会改变
	 *
	 * @return entities -> FactionEntityTypeList
	 */
	public List<FactionEntityType> getFactionEntityTypes() {
		if(!raiderTypeList.isEmpty()){
			return raiderTypeList;
		}
		for (ResourceLocation entity : entities) {
			EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entity);
			if (entityType != null) {
				List<FactionEntityType> raiderTypes = DataPackRegistries.Faction_ENTITY_TYPES.getFactionEntityType(entityType);
				if (raiderTypes != null) {
					raiderTypeList.addAll(raiderTypes);
				}
			}
		}
		return raiderTypeList;
	}
	
	public FactionRelations getFactionRelations() {
		return factionRelations;
	}
	
	public ResourceLocation getActivationAdvancement() {
		return activationAdvancement;
	}
	
	public boolean isAlly(Faction targetFaction){
		return getFactionRelations().getAllies().contains(DataPackRegistries.FACTIONS.getKey(targetFaction).toString());
	}
	
	public boolean isEnemy(Faction targetFaction){
		return getFactionRelations().getEnemies().contains(DataPackRegistries.FACTIONS.getKey(targetFaction).toString());
	}
}
