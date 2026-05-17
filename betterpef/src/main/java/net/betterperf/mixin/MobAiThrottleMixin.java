package net.betterperf.mixin;

import net.betterperf.BetterPerfConfig;
import net.betterperf.util.PerformanceUtil;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips most server-side mob AI when no player is within 64 blocks.
 * <p>
 * Passive mobs and farm entities repeatedly scan for the nearest player, update goals,
 * and refresh pathfinding every tick. In spawn chunks and mob farms hundreds of entities
 * do this work even though no player is nearby. Throttling {@code serverAiStep} (Brain)
 * and {@code customServerAiStep} (classic goals) to every 4th tick cuts server CPU time
 * with negligible gameplay impact beyond render distance.
 */
@Mixin(Mob.class)
public abstract class MobAiThrottleMixin {
	private static final BetterPerfConfig CONFIG = BetterPerfConfig.DEFAULT;

	@Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
	private void betterperf$throttleBrainAi(CallbackInfo ci) {
		this.betterperf$cancelIfThrottled(ci);
	}

	@Inject(method = "customServerAiStep", at = @At("HEAD"), cancellable = true)
	private void betterperf$throttleLegacyAi(CallbackInfo ci) {
		this.betterperf$cancelIfThrottled(ci);
	}

	private void betterperf$cancelIfThrottled(CallbackInfo ci) {
		Mob self = (Mob) (Object) this;

		if (self.level().isClientSide) {
			return;
		}

		if (PerformanceUtil.shouldThrottleAiTick(self, CONFIG)) {
			ci.cancel();
		}
	}
}
