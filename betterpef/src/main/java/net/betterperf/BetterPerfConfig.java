package net.betterperf;

/**
 * Tunable performance constants. Values are records for immutability and clarity (Java 17).
 */
public record BetterPerfConfig(
		int aiThrottleDistanceBlocks,
		int aiThrottleIntervalTicks,
		double entityCullBoxInflate
) {
	public static final BetterPerfConfig DEFAULT = new BetterPerfConfig(64, 4, 0.5D);

	public double aiThrottleDistanceSq() {
		long blocks = aiThrottleDistanceBlocks;
		return blocks * blocks;
	}
}
