package me.cortex.voxy;

import me.cortex.voxy.client.VoxyClient;
import me.cortex.voxy.client.VoxyCommands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

// Stub entry point — the original patch referenced this class but did not ship it.
// Only the Voxy client-side code is wired up; the server/common side is out of scope for this build.
//
// Event-bus wiring (important):
//   - All subscribed events here live on the GAME bus (NeoForge.EVENT_BUS), which is correct
//     for both RegisterClientCommandsEvent and ClientTickEvent.
//   - We deliberately do NOT use FMLClientSetupEvent here. That event fires BEFORE
//     GL.createCapabilities() is invoked by Minecraft, so VoxyClient.initVoxyClient() cannot
//     touch GL capabilities / run GL shader tests (Capabilities.<init> requires a current GL ctx).
//     The bug surfaced as `IllegalStateException: No GLCapabilities instance set` on every fresh
//     client launch (xvfb + real GPU alike). Fix: defer init to ClientTickEvent.Pre, which runs
//     on the render thread after the GL context has been created.
//   - The init is idempotent (guarded by a static flag) so repeat ticks are a no-op.
@Mod(value = "voxy", dist = Dist.CLIENT)
public class NeoVoxyMod {
    private static volatile boolean voxyInitialized = false;

    public NeoVoxyMod() {
        // Game bus: runtime events (RegisterClientCommandsEvent + ClientTickEvent).
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        VoxyCommands.register(event);
    }

    @SubscribeEvent
    public void onClientTickPre(ClientTickEvent.Pre event) {
        if (voxyInitialized) {
            return;
        }
        VoxyClient.initVoxyClient();
        voxyInitialized = true;
    }
}
