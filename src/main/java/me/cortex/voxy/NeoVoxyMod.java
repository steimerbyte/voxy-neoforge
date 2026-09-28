package me.cortex.voxy;

import me.cortex.voxy.client.VoxyClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

// Stub entry point — the original patch referenced this class but did not ship it.
// Only the Voxy client-side code is wired up; the server/common side is out of scope for this build.
@Mod(value = "voxy", dist = Dist.CLIENT)
public class NeoVoxyMod {
    public NeoVoxyMod() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        VoxyClient.initVoxyClient();
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        me.cortex.voxy.client.VoxyCommands.register(event);
    }
}
