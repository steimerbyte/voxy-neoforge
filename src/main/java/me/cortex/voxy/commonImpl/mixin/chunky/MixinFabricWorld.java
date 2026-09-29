package me.cortex.voxy.commonImpl.mixin.chunky;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.cortex.voxy.common.world.service.VoxelIngestService;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
//import org.popcraft.chunky.mixin.ServerChunkCacheMixin;
import org.popcraft.chunky.platform.NeoForgeWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(NeoForgeWorld.class)
public class MixinFabricWorld {
    @WrapOperation(method = "getChunkAtAsync", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;getChunkFutureMainThread(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<ChunkResult<ChunkAccess>> captureGeneratedChunk(ServerChunkCache instance, int x, int z, ChunkStatus t, boolean chunkStatus, Operation<CompletableFuture<ChunkResult<ChunkAccess>>> original) {
        var future = original.call(instance, x, z,t, chunkStatus);
        if (false) {//TODO: ADD SERVER CONFIG THING
            return future;
        } else {
            return future.thenApply(res -> {
                res.ifSuccess(chunk -> {
                    if (chunk instanceof LevelChunk worldChunk) {
                        VoxelIngestService.tryAutoIngestChunk(worldChunk);
                    }
                });
                return res;
            });
        }
    }
}
