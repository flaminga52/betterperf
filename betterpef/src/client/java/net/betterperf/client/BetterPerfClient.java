package net.betterperf.client;

import net.fabricmc.api.ClientModInitializer;
import net.betterperf.BetterPerfMod;

public class BetterPerfClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BetterPerfMod.LOGGER.info("BetterPerf client initialized — entity frustum culling enabled.");
	}
}
