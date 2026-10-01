package me.cortex.voxy.client;

import me.cortex.voxy.client.core.gl.Capabilities;
import me.cortex.voxy.client.core.model.bakery.BudgetBufferRenderer;
import me.cortex.voxy.client.core.rendering.util.SharedIndexBuffer;
import me.cortex.voxy.common.Logger;
import me.cortex.voxy.commonImpl.VoxyCommon;
import me.cortex.voxy.NeoVoxyMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.minecraft.client.Minecraft;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.util.HashSet;

public class VoxyClient {
    private static final HashSet<String> FREX = new HashSet<>();
    private static FileLock EXCLUSIVE_LOCK;
    public static void initVoxyClient() {
        Capabilities.init();//Ensure clinit is called

        var caps = Capabilities.INSTANCE;

        if (caps.hasBrokenDepthSampler) {
            Logger.error("AMD broken depth sampler detected, voxy does not work correctly and has been disabled, this will hopefully be fixed in the future");
        }

        // GL_ARB_gpu_shader_int64 is required by 7 of the core LoD shaders
        // (cmdgen.comp, buildtranslucents.comp, quads2/3.vert, cull/raster.vert,
        //  quad_format.glsl, test/raw.vert). Intel iGPUs report GL 4.6 but do not
        // expose the extension, so every one of those programs fails to compile
        // and each dispatch then raises GL_INVALID_OPERATION. Gate on it here so
        // unsupported hardware disables voxy once instead of spamming the log.
        boolean int64Supported = caps.INT64_t
                || System.getProperty("voxy.forceInt64", "false").equalsIgnoreCase("true");

        boolean systemSupported = caps.compute && caps.indirectParameters
                && !caps.hasBrokenDepthSampler && int64Supported;

        if (!systemSupported) {
            if (!int64Supported) {
                Logger.error("GPU does not support GL_ARB_gpu_shader_int64 (uint64_t in GLSL).");
                Logger.error("Voxy's LoD shaders require it and cannot run on this GPU. Disabling voxy.");
                Logger.error("This is a hardware limitation, not a configuration problem. "
                        + "Intel integrated GPUs do not implement the extension even at GL 4.6.");
            } else {
                Logger.error("Voxy is unsupported on your system.");
            }
        }

        if (systemSupported && System.getProperty("voxy.exclusiveLock", "false").equalsIgnoreCase("true")) {
            //Try acquire the lock file
            var vf = Minecraft.getInstance().gameDirectory.toPath().resolve(".voxy");
            if (!vf.toFile().isDirectory()) {
                vf.toFile().mkdir();
            }
            try {
                FileOutputStream fis = new FileOutputStream(vf.resolve("voxy.lock").toFile());
                EXCLUSIVE_LOCK = fis.getChannel().lock(0, Long.MAX_VALUE, false);
            } catch (NonWritableChannelException | IOException e) {
                //If some error write to log and unsupport
                Logger.error("Failed to acquire exclusive voxy lock file, mod will be disabled");
                systemSupported = false;
            }

        }

        if (systemSupported) {

            SharedIndexBuffer.INSTANCE.id();
            BudgetBufferRenderer.init();

            VoxyCommon.setInstanceFactory(VoxyClientInstance::new);

            if (!caps.subgroup) {
                Logger.warn("GPU does not support subgroup operations, expect some performance degradation");
            }

        }
    }

    public static void onInitializeClientNeoForge() {
        // NeoForge client initialization
    }

    // Command registration is handled in NeoVoxyMod.java

    public static boolean isFrexActive() {
        return !FREX.isEmpty();
    }

    public static int getOcclusionDebugState() {
        return 0;
    }

    public static boolean disableSodiumChunkRender() {
        return false;// getOcclusionDebugState() != 0;
    }
}