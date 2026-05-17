package net.betterperf.util;

import net.betterperf.BetterPerfConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class PerformanceUtil {
	private PerformanceUtil() {
	}

	public static boolean noPlayerNearby(Entity entity, double maxDistance) {
		Level level = entity.level();
		Player nearest = level.getNearestPlayer(entity, maxDistance);
		return nearest == null;
	}

	public static boolean shouldThrottleAiTick(Entity entity, BetterPerfConfig config) {
		return noPlayerNearby(entity, config.aiThrottleDistanceBlocks())
				&& entity.tickCount % config.aiThrottleIntervalTicks() != 0;
	}
}
