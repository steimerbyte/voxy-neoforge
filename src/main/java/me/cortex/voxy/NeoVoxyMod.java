package me.cortex.voxy;

import me.cortex.voxy.client.VoxyClient;
import me.cortex.voxy.client.VoxyCommands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

// Stub entry point — the original patch referenced this class but did not ship it.
// Only the Voxy client-side code is wired up; the server/common side is out of scope for this build.
//
// Event-bus wiring (important):
//   - FMLClientSetupEvent is a MOD event (lifecycle), MUST be on the mod event bus
//   - RegisterClientCommandsEvent is a GAME event (NeoForge bus)
// Mixing them up crashes the entire mod loader with:
//   "IModBusEvent events are not allowed on the common NeoForge bus!"
@Mod(value = "voxy", dist = Dist.CLIENT)
public class NeoVoxyMod {
    public NeoVoxyMod() {
        // Game bus: runtime events (RegisterClientCommandsEvent etc.)
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        VoxyCommands.register(event);
    }

    // Mod bus: lifecycle/setup events.
    // @Mod.EventBusSubscriber with bus=MOD auto-registers this class to the mod event bus.
    @EventBusSubscriber(modid = "voxy", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            VoxyClient.initVoxyClient();
        }
    }
}
