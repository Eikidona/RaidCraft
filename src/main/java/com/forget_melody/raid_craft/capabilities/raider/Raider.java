package com.forget_melody.raid_craft.capabilities.raider;

import com.forget_melody.raid_craft.RaidCraft;
import com.forget_melody.raid_craft.capabilities.raid_manager.IRaidManager;
import com.forget_melody.raid_craft.raid.Raid;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class Raider implements IRaider, INBTSerializable<CompoundTag> {
	private final Mob mob;
	private Raid raid = null;
	private int wave = 0;
	private final List<Pair<Integer, Goal>> raiderGoals = new ArrayList<>();
//	private final InvadeHomeGoal invadeHomeGoal;
//	private RaidOpenDoorGoal raidOpenDoorGoal;
//	private final MoveTowardsRaidGoal<Mob> moveTowardsRaidGoal;
//	private final ObtainRaidLeaderBannerGoal obtainRaidLeaderBannerGoal;
	private boolean leader;
	
	public Raider(Mob mob) {
		this.mob = mob;
//		invadeHomeGoal = new InvadeHomeGoal(mob, 1.05F, 1);
//		moveTowardsRaidGoal = new MoveTowardsRaidGoal<>(mob);
//		if (GoalUtils.hasGroundPathNavigation(mob)) {
//			raidOpenDoorGoal = new RaidOpenDoorGoal(mob);
//		}
//		obtainRaidLeaderBannerGoal = new ObtainRaidLeaderBannerGoal(mob);
	}
	
	@Override
	public Mob getMob() {
		return mob;
	}
	
	@Override
	public Raid getRaid() {
		return raid;
	}
	
	@Override
	public boolean canJoinRaid() {
		return this.raid == null;
	}
	
	@Override
	public void setRaid(Raid raid) {
		this.raid = raid;
		updateRaidGoals();
	}
	
	@Override
	public boolean hasActiveRaid() {
//		RaidCraft.LOGGER.info("Raider: raid != null: {}", raid != null);
//		if (raid != null) {
//			RaidCraft.LOGGER.info("Raider: raidIsActive: {}", raid.isActive());
//		}
		return raid != null && raid.isActive();
	}
	
	private void updateRaidGoals() {
		if (raid != null) {
//			mob.goalSelector.addGoal(2, obtainRaidLeaderBannerGoal);
//			if (raidOpenDoorGoal != null) {
//				mob.goalSelector.addGoal(3, raidOpenDoorGoal);
//			}
//			mob.goalSelector.addGoal(4, invadeHomeGoal);
//			mob.goalSelector.addGoal(5, moveTowardsRaidGoal);
//			RaidCraft.LOGGER.info("Add Raid Goals Pre: {}", mob.goalSelector.getAvailableGoals().size());
//			raid.getRaidTarget().addGoal(this);
//			RaidCraft.LOGGER.info("Add Raid Goals: {}", mob.goalSelector.getAvailableGoals().size());
		} else {
//			RaidCraft.LOGGER.info("Remove Raid Goals");
			removeAllGoals();
//			mob.goalSelector.removeGoal(moveTowardsRaidGoal);
//			mob.goalSelector.removeGoal(invadeHomeGoal);
//			mob.goalSelector.removeGoal(raidOpenDoorGoal);
//			mob.goalSelector.removeGoal(obtainRaidLeaderBannerGoal);
		}
	}
	
	@Override
	public void setLeader(boolean leader) {
		this.leader = leader;
	}
	
	@Override
	public int getWave() {
		return wave;
	}
	
	@Override
	public void setWave(int wave) {
		this.wave = wave;
	}
	
	@Override
	public boolean isLeader() {
		return leader;
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		if (raid != null) {
			tag.putInt("Raid", raid.getId());
		}
		if (wave != 0) {
			tag.putInt("Wave", this.wave);
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("Wave")) {
			this.wave = nbt.getInt("Wave");
		}
		if (nbt.contains("Raid")) {
			IRaidManager manager = IRaidManager.get((ServerLevel) mob.level());
			Raid raid1 = manager.getRaid(nbt.getInt("Raid"));
			if (raid1 != null) {
				raid1.joinRaid(this, false);
			} else {
				RaidCraft.LOGGER.error("Not found valid raid id {}", nbt.getInt("Raid"));
			}
		}
	}
	
	@Override
	public void addAllGoals(List<Pair<Integer, Goal>> goals){
		raiderGoals.addAll(goals);
		goals.forEach(pair -> {
			mob.goalSelector.addGoal(pair.getFirst(), pair.getSecond());
		});
	}
	
	@Override
	public void addGoal(int priority, Goal goal){
		raiderGoals.add(new Pair<>(priority, goal));
		mob.goalSelector.addGoal(priority, goal);
	}
	
	@Override
	public <T extends Goal> void removeGoal(Class<T> goalClass) {
		raiderGoals.removeIf(pair -> {
			if(goalClass.isInstance(pair.getSecond())){
				mob.goalSelector.removeGoal(pair.getSecond());
				return true;
			}
			return false;
		});
	}
	
	@Override
	public void removeAllGoals(){
		for(Pair<Integer, Goal> pair: raiderGoals){
			mob.goalSelector.removeGoal(pair.getSecond());
		}
		raiderGoals.clear();
	}
}
