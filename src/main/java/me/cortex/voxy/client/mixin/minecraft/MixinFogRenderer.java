package me.cortex.voxy.client.mixin.minecraft;

import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

@Mixin(value = FogRenderer.class, priority = 900)//We must execute before sodium
public class MixinFogRenderer {
    @Inject(
        method = "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZF)V",
        at = @At("TAIL"),
        cancellable = true
    )
    private static void voxy$overrideFog(
        Camera camera,
        FogMode fogMode,
        float viewDistance,
        boolean thickFog,
        float tickDelta,
        CallbackInfo ci
    ) {
        if (!VoxyConfig.CONFIG.isRenderingEnabled()) return;

        var vrs = (IGetVoxyRenderSystem) Minecraft.getInstance().levelRenderer;
        var mode = VoxyConfig.CONFIG.getFogMode();

        //1.21.1 has no FogData return to rewrite; the vanilla env-fog removal this fork already does via the
        //RenderSystem statics below is the equivalent, now gated on the new FogMode's removesVanillaEnvFog flag
        //(OFF/FADE remove it, FOG/FOG_AND_FADE keep vanilla's environmental fog in place).
        boolean fogIsDamnClose = RenderSystem.getShaderFogEnd() < 10;
        if (VoxyConfig.CONFIG.renderVanillaFog || !mode.removesVanillaEnvFog || fogIsDamnClose
                || vrs == null || vrs.voxy$getRenderSystem() == null) {
            RenderSystem.setShaderFogEnd(viewDistance);
        } else {
            RenderSystem.setShaderFogStart(999999999);
            RenderSystem.setShaderFogEnd(999999999);
        }
    }
}
