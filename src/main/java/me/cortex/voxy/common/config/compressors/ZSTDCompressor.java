package me.cortex.voxy.common.config.compressors;

import com.github.luben.zstd.Zstd;
import me.cortex.voxy.common.config.ConfigBuildCtx;
import me.cortex.voxy.common.util.MemoryBuffer;
import me.cortex.voxy.common.util.ThreadLocalMemoryBuffer;
import me.cortex.voxy.common.util.UnsafeUtil;
import me.cortex.voxy.common.world.SaveLoadSystem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ZSTDCompressor implements StorageCompressor {
    private static final ThreadLocalMemoryBuffer SCRATCH = new ThreadLocalMemoryBuffer(SaveLoadSystem.BIGGEST_SERIALIZED_SECTION_SIZE + 1024);

    private final int level;

    public ZSTDCompressor(int level) {
        this.level = level;
    }

    @Override
    public MemoryBuffer compress(MemoryBuffer saveData) {
        // 使用zstd-jni的简化API进行压缩
        // 获取ByteBuffer并设置为小端字节序
        ByteBuffer inputBuffer = saveData.asByteBuffer().order(ByteOrder.nativeOrder());
        
        // 读取数据到byte数组
        byte[] input = new byte[(int)saveData.size];
        inputBuffer.get(input);
        
        // 压缩数据
        byte[] compressed = Zstd.compress(input, this.level);
        
        // 创建输出MemoryBuffer
        MemoryBuffer compressedData = new MemoryBuffer(compressed.length);
        
        // 获取输出ByteBuffer并设置为小端字节序
        ByteBuffer outputBuffer = compressedData.asByteBuffer().order(ByteOrder.nativeOrder());
        outputBuffer.put(compressed);
        
        return compressedData;
    }

    @Override
    public MemoryBuffer decompress(MemoryBuffer saveData) {
        // 使用zstd-jni的简化API进行解压缩
        // 获取ByteBuffer并设置为小端字节序
        ByteBuffer inputBuffer = saveData.asByteBuffer().order(ByteOrder.nativeOrder());
        
        // 读取数据到byte数组
        byte[] input = new byte[(int)saveData.size];
        inputBuffer.get(input);
        
        // 首先获取解压缩后的大小
        long decompressedSize = Zstd.decompressedSize(input);
        
        // 进行解压缩，使用正确的API签名
        byte[] decompressed = new byte[(int)decompressedSize];
        Zstd.decompress(decompressed, input);
        
        // 创建输出MemoryBuffer
        MemoryBuffer result = new MemoryBuffer(decompressed.length);
        
        // 获取输出ByteBuffer并设置为小端字节序
        ByteBuffer outputBuffer = result.asByteBuffer().order(ByteOrder.nativeOrder());
        outputBuffer.put(decompressed);
        
        return result;
    }

    @Override
    public void close() {
        // zstd-jni不需要显式关闭资源
    }

    public static class Config extends CompressorConfig {
        public int compressionLevel;

        @Override
        public StorageCompressor build(ConfigBuildCtx ctx) {
            return new ZSTDCompressor(this.compressionLevel);
        }

        public static String getConfigTypeName() {
            return "ZSTD";
        }
    }
}
