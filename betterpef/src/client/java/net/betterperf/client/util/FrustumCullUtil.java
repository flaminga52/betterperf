package net.betterperf.client.util;

import net.betterperf.BetterPerfConfig;
import net.betterperf.client.mixin.LevelRendererFrustumAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public final class FrustumCullUtil {
	private static final BetterPerfConfig CONFIG = BetterPerfConfig.DEFAULT;

	private FrustumCullUtil() {
	}

	public static boolean isOutsideCameraFrustum(Entity entity) {
		Minecraft minecraft = Minecraft.getInstance();
		LevelRenderer levelRenderer = minecraft.levelRenderer;

		if (levelRenderer == null) {
			return false;
		}

		Frustum frustum = resolveActiveFrustum(levelRenderer);
		if (frustum == null) {
			return false;
		}

		double inflate = CONFIG.entityCullBoxInflate();
		AABB bounds = entity.getBoundingBox().inflate(inflate, inflate, inflate);
		return !frustum.isVisible(bounds);
	}

	private static Frustum resolveActiveFrustum(LevelRenderer levelRenderer) {
		return ((LevelRendererFrustumAccessor) levelRenderer).betterperf$getCullingFrustum();
	}
}
