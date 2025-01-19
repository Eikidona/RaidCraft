package com.forget_melody.raid_craft.entity.ai.goal;

import com.forget_melody.raid_craft.capabilities.raider.IRaider;
import com.forget_melody.raid_craft.capabilities.raider.api.RaiderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InvadeHomeGoal extends Goal {
	private final Mob mob;
	private final double speedModifier;
	private BlockPos poiPos;
	private final List<BlockPos> visited = Lists.newArrayList();
	private final int distanceToPoi;
	private boolean stuck;
	
	public InvadeHomeGoal(Mob mob, double speedModifier, int distanceToPoi) {
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.distanceToPoi = distanceToPoi;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}
	
	private void updateVisited() {
		if (this.visited.size() > 2) {
			this.visited.remove(0);
		}
	}
	
	private boolean hasNotVisited(BlockPos blockPos) {
		for (BlockPos pos : this.visited) {
			if (Objects.equals(blockPos, pos)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isValidRaid() {
		Optional<IRaider> raider = RaiderHelper.getRaider(mob);
		if (!raider.isPresent()) {
			return false;
		}
		return raider.get().hasActiveRaid();
	}
	
	private boolean hasSuitablePoi() {
		ServerLevel level = (ServerLevel) mob.level();
		BlockPos pos = mob.blockPosition();
		Optional<BlockPos> optional = level.getPoiManager().getRandom(poiTypeHolder -> poiTypeHolder.is(PoiTypes.HOME), this::hasNotVisited, PoiManager.Occupancy.ANY, pos, 48, mob.getRandom());
		if (!optional.isPresent()) {
			return false;
		} else {
			poiPos = optional.get().immutable();
			return true;
		}
	}
	
	@Override
	public boolean canContinueToUse() {
		if (mob.getNavigation().isDone()) {
			return false;
		} else {
			return mob.getTarget() == null && !poiPos.closerThan(mob.blockPosition(), (mob.getBbWidth() + distanceToPoi)) && !stuck;
		}
	}
	
	@Override
	public void stop() {
		if (poiPos.closerThan(mob.blockPosition(), distanceToPoi)) {
			visited.add(poiPos);
		}
	}
	
	@Override
	public void start() {
		super.start();
		mob.setNoActionTime(0);
		mob.getNavigation().moveTo(poiPos.getX(), poiPos.getY(), poiPos.getZ(), speedModifier);
		stuck = false;
	}
	
	@Override
	public void tick() {
		if (mob.getNavigation().isDone()) {
			Vec3 vec3 = Vec3.atBottomCenterOf(poiPos);
			Vec3 vec31 = null;
			for (int i = 1; i < 2; i++) {
				vec31 = DefaultRandomPos.getPosTowards((PathfinderMob) mob, 8, 7, vec3, Math.PI * 2.0F);
				if (vec31 != null) {
					break;
				}
			}
			if (vec31 == null) {
				stuck = true;
				return;
			}
			mob.getNavigation().moveTo(vec31.x, vec31.y, vec31.z, speedModifier);
			
		}
	}
	
	@Override
	public boolean canUse() {
		return false;
	}
}
