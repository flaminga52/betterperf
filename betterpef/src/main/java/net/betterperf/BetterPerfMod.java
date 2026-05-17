package net.betterperf;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterPerfMod implements ModInitializer {
	public static final String MOD_ID = "betterperf";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("BetterPerf loaded — entity culling (client) and AI throttling (server) active.");
	}
}
