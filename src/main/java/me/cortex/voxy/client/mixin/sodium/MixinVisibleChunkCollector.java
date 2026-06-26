package me.cortex.voxy.client.mixin.sodium;

import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.VisibleChunkCollector;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VisibleChunkCollector.class, remap = false)
public class MixinVisibleChunkCollector {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void voxy$injectVisibleStreamReset(CallbackInfo ci) {
        var vrs = IGetVoxyRenderSystem.getNullable();
        if (vrs != null) {
            vrs.visbleSectionStream.reset();
        }
    }

    //Use redirect for performance
    //1.21.1/Sodium 0.6.x visit(RenderSection) has no RenderRegionManager call, it reads the section's region via
    //RenderSection.getRegion() instead, so that is the ported redirect target (same semantics: record every section
    //that becomes visible this frame into the streamed bound store)
    @Redirect(method = "visit", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSection;getRegion()Lnet/caffeinemc/mods/sodium/client/render/chunk/region/RenderRegion;"), remap = false)
    private RenderRegion voxy$injectVisibleSectionGather(RenderSection instance) {
        var vrs = IGetVoxyRenderSystem.getNullable();
        if (vrs != null) {
            vrs.visbleSectionStream.put(SectionPos.asLong(instance.getChunkX(), instance.getChunkY(), instance.getChunkZ()));
        }
        return instance.getRegion();
    }
}
