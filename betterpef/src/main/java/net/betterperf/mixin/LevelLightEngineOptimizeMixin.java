package net.betterperf.mixin;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.betterperf.mixin.accessor.LightEngineAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips redundant light updates when the block at a position has not changed.
 * <p>
 * Hooks {@link LevelLightEngine#checkBlock(BlockPos)} instead of the abstract
 * {@link LightEngine#checkNode(long)} so the injection has a concrete method body.
 * Caching the last block id per position avoids re-running block and sky light
 * propagation for static farm builds where lighting is already settled.
 */
@Mixin(LevelLightEngine.class)
public class LevelLightEngineOptimizeMixin {
	private static final int CACHE_CAPACITY = 16_384;

	@Shadow
	@Final
	private LightEngine<?, ?> blockEngine;

	@Unique
	private static final Long2IntOpenHashMap betterperf$blockStateCache = new Long2IntOpenHashMap();

	@Inject(method = "checkBlock", at = @At("HEAD"), cancellable = true)
	private void betterperf$skipUnchangedLightCheck(BlockPos pos, CallbackInfo ci) {
		BlockState state = ((LightEngineAccessor) this.blockEngine).betterperf$getState(pos);
		int stateId = Block.getId(state);
		long key = pos.asLong();

		int previous = betterperf$blockStateCache.getOrDefault(key, -1);
		if (previous == stateId) {
			ci.cancel();
			return;
		}

		if (betterperf$blockStateCache.size() >= CACHE_CAPACITY) {
			betterperf$blockStateCache.clear();
		}

		betterperf$blockStateCache.put(key, stateId);
	}
}
