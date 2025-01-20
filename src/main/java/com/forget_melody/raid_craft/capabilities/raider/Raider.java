package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.raid.Raid;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.capabilities.raid_manager.RaidManagerHelper;
import com.forget_melody.raid_craft.level.entity.ai.goal.InvadeHomeGoal;
import com.forget_melody.raid_craft.level.entity.ai.goal.MoveTowardsRaidGoal;
import com.forget_melody.raid_craft.level.entity.ai.goal.RaidOpenDoorGoal;
import com.forget_melody.raid_craft.raid.raider.RaiderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Raider implements IRaider, INBTSerializable<CompoundTag> {
	private final Mob mob;
	private Raid raid;
	private int wave;
	private final InvadeHomeGoal invadeHomeGoal;
	private RaidOpenDoorGoal raidOpenDoorGoal;
	private final MoveTowardsRaidGoal<Mob> moveTowardsRaidGoal;
	private RaiderType raiderType;
	
	public Raider(Mob mob) {
		this.mob = mob;
		invadeHomeGoal = new InvadeHomeGoal(mob, 1.05F, 1);
		moveTowardsRaidGoal = new MoveTowardsRaidGoal<>(mob);
		if (GoalUtils.hasGroundPathNavigation(mob)) {
			raidOpenDoorGoal = new RaidOpenDoorGoal(mob);
		}
	}
	
	@Override
	public Mob get() {
		return mob;
	}
	
	@Nullable
	@Override
	public Raid getRaid() {
		return raid;
	}
	
	@Override
	public void setRaid(Raid raid) {
		this.raid = raid;
		updateRaidGoals();
	}
	
	@Override
	public void setWave(Integer wave) {
		this.wave = wave;
	}
	
	@Override
	public int getWave() {
		return wave;
	}
	
	@Override
	public boolean hasActiveRaid() {
		return raid != null && raid.isActive();
	}
	
	@Override
	public void updateRaidGoals() {
		if (raid != null) {
			if (raidOpenDoorGoal != null) {
				mob.goalSelector.addGoal(2, raidOpenDoorGoal);
			}
			mob.goalSelector.addGoal(3, moveTowardsRaidGoal);
			mob.goalSelector.addGoal(4, invadeHomeGoal);
		} else {
			mob.goalSelector.removeGoal(moveTowardsRaidGoal);
			mob.goalSelector.removeGoal(invadeHomeGoal);
			mob.goalSelector.removeGoal(raidOpenDoorGoal);
		}
	}
	
	@Override
	public void setRaiderType(RaiderType raiderType) {
	
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag compoundTag = new CompoundTag();
		if (raid != null) {
			compoundTag.putInt("Raid", raid.getId());
		}
		return compoundTag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("Raid")) {
			Optional<IRaidManager> optional = RaidManagerHelper.get((ServerLevel) mob.level());
			optional.ifPresent(iRaidManager -> {
				this.raid = iRaidManager.getRaid(nbt.getInt("Raid"));
				if (this.raid != null) {
					this.raid.addWaveMob(this.wave, mob);
				}
			});
			updateRaidGoals();
		}
	}
}
