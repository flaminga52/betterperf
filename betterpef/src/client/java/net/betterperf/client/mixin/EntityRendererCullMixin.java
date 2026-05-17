package net.betterperf.client.mixin;

import net.betterperf.client.util.FrustumCullUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels entity rendering when the entity's bounds fall outside the active camera frustum.
 * <p>
 * The GPU still benefits when draw calls are skipped early: dense farms and spawn platforms
 * can have hundreds of entities inside simulation distance but outside the player's field
 * of view. Frustum culling avoids skinning, layer setup, and buffer uploads for entities the
 * player cannot see — the largest client-side win in heavy entity scenarios.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererCullMixin<T extends Entity> {

	@Inject(
			method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void betterperf$cullOutsideFrustum(
			T entity,
			float entityYaw,
			float partialTicks,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			CallbackInfo ci
	) {
		if (FrustumCullUtil.isOutsideCameraFrustum(entity)) {
			ci.cancel();
		}
	}
}
