package com.forget_melody.raid_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class EquipmentBoost implements IBoost {
	public static final Codec<EquipmentBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("strength", 5).forGetter(EquipmentBoost::getStrength),
			ItemStack.CODEC.optionalFieldOf("head", ItemStack.EMPTY).forGetter(EquipmentBoost::getHead),
			ItemStack.CODEC.optionalFieldOf("chest", ItemStack.EMPTY).forGetter(EquipmentBoost::getChest),
			ItemStack.CODEC.optionalFieldOf("legs", ItemStack.EMPTY).forGetter(EquipmentBoost::getLegs),
			ItemStack.CODEC.optionalFieldOf("feet", ItemStack.EMPTY).forGetter(EquipmentBoost::getFeet),
			ItemStack.CODEC.optionalFieldOf("mainHand", ItemStack.EMPTY).forGetter(EquipmentBoost::getMainHand),
			ItemStack.CODEC.optionalFieldOf("offHand", ItemStack.EMPTY).forGetter(EquipmentBoost::getOffHand)
	).apply(instance, EquipmentBoost::new));
	private final int strength;
	private final ItemStack head;
	private final ItemStack chest;
	private final ItemStack legs;
	private final ItemStack feet;
	private final ItemStack mainHand;
	private final ItemStack offHand;
	
	public EquipmentBoost(int strength, ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, ItemStack mainHand, ItemStack offHand) {
		this.strength = strength;
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.feet = feet;
		this.mainHand = mainHand;
		this.offHand = offHand;
	}
	
	public ItemStack getHead() {
		return head;
	}
	
	public ItemStack getChest() {
		return chest;
	}
	
	public ItemStack getLegs() {
		return legs;
	}
	
	public ItemStack getFeet() {
		return feet;
	}
	
	public ItemStack getMainHand() {
		return mainHand;
	}
	
	public ItemStack getOffHand() {
		return offHand;
	}
	
	@Override
	public BoostType getType(){
		return BoostType.EQUIPMENT;
	}
	
	@Override
	public void apply(Mob mob) {
		if(head != ItemStack.EMPTY){
			mob.setItemSlot(EquipmentSlot.HEAD, head.copy());
		}
		if(chest != ItemStack.EMPTY){
			mob.setItemSlot(EquipmentSlot.CHEST, chest.copy());
		}
		if(legs != ItemStack.EMPTY){
			mob.setItemSlot(EquipmentSlot.LEGS, legs.copy());
		}
		if(feet != ItemStack.EMPTY){
			mob.setItemSlot(EquipmentSlot.FEET, feet.copy());
		}
		if(mainHand != ItemStack.EMPTY){
			mob.setItemSlot(EquipmentSlot.MAINHAND, mainHand.copy());
		}
		if(offHand != ItemStack.EMPTY){
			mob.setItemSlot(EquipmentSlot.OFFHAND, offHand.copy());
		}
	}
	
	@Override
	public int getStrength() {
		return strength;
	}
}
