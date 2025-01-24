package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.raid.raid.IRaid;
import com.forget_melody.raid_craft.raid.raider_type.RaiderType;
import com.forget_melody.raid_craft.registries.DatapackRegistries;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.InvadeHomeGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.MoveTowardsRaidGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.ObtainRaidLeaderBannerGoal;
import com.forget_melody.raid_craft.world.entity.ai.goal.raider.RaidOpenDoorGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Raider implements IRaider, INBTSerializable<CompoundTag> {
	private final Mob mob;
	private IRaid raid = null;
	private RaiderType raiderType = null;
	private int wave = 0;
	private final InvadeHomeGoal invadeHomeGoal;
	private RaidOpenDoorGoal raidOpenDoorGoal;
	private final MoveTowardsRaidGoal<Mob> moveTowardsRaidGoal;
	private final ObtainRaidLeaderBannerGoal<Mob> obtainRaidLeaderBannerGoal;
	private boolean leader;
	
	public Raider(Mob mob) {
		this.mob = mob;
		invadeHomeGoal = new InvadeHomeGoal(mob, 1.05F, 1);
		moveTowardsRaidGoal = new MoveTowardsRaidGoal<>(mob);
		if (GoalUtils.hasGroundPathNavigation(mob)) {
			raidOpenDoorGoal = new RaidOpenDoorGoal(mob);
		}
		obtainRaidLeaderBannerGoal = new ObtainRaidLeaderBannerGoal<>(mob);
	}
	
	@Override
	public Mob getMob() {
		return mob;
	}
	
	@Override
	public IRaid getRaid() {
		return raid;
	}
	
	@Override
	public boolean canJoinRaid() {
		return this.raid == null;
	}
	
	@Override
	public void setRaid(IRaid raid) {
		this.raid = raid;
		updateWave();
		updateRaidGoals();
	}
	
	private void updateWave() {
		if (this.raid == null) {
			this.wave = 0;
		} else {
			this.wave = this.raid.getSpawnedWave();
		}
	}
	
	@Override
	public void setRaiderType(RaiderType raiderType) {
		this.raiderType = raiderType;
	}
	
	@Override
	public boolean hasActiveRaid() {
		return raid != null && raid.isActive();
	}
	
	private void updateRaidGoals() {
		if (raid != null) {
			mob.goalSelector.addGoal(2, obtainRaidLeaderBannerGoal);
			if (raidOpenDoorGoal != null) {
				mob.goalSelector.addGoal(3, raidOpenDoorGoal);
			}
			mob.goalSelector.addGoal(4, invadeHomeGoal);
			mob.goalSelector.addGoal(5, moveTowardsRaidGoal);
		} else {
			mob.goalSelector.removeGoal(moveTowardsRaidGoal);
			mob.goalSelector.removeGoal(invadeHomeGoal);
			mob.goalSelector.removeGoal(raidOpenDoorGoal);
			mob.goalSelector.removeGoal(obtainRaidLeaderBannerGoal);
		}
	}
	
	@Override
	public void setLeader(boolean leader) {
		this.leader = leader;
		this.mob.setItemSlot(EquipmentSlot.HEAD, raid.getBanner());
		this.mob.setDropChance(EquipmentSlot.HEAD, 2.0F);
	}
	
	@Override
	public int getWave() {
		return wave;
	}
	
	@Override
	public @Nullable RaiderType getRaiderType() {
		return this.raiderType;
	}
	
	@Override
	public boolean isLeader() {
		return leader;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag compoundTag = new CompoundTag();
		if (raid != null) {
			compoundTag.putInt("Raid", raid.getId());
		}
		if (raiderType != null) {
			compoundTag.putString("RaidType", DatapackRegistries.RAIDER_TYPES.getKey(raiderType).toString());
		}
		return compoundTag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("Raid")) {
			Optional<IRaidManager> optional = IRaidManager.get((ServerLevel) mob.level());
			optional.ifPresent(iRaidManager -> {
				raid = iRaidManager.getRaid(nbt.getInt("Raid"));
				if (raid != null) {
					raid.joinRaid(this);
				}
			});
			updateRaidGoals();
		}
		if (nbt.contains("RaiderType")) {
			raiderType = DatapackRegistries.RAIDER_TYPES.getValue(new ResourceLocation(nbt.getString("RaiderType")));
		}
	}
}
