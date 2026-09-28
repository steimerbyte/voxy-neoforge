package me.cortex.voxy.client.core.gl.shader;

import net.caffeinemc.mods.sodium.client.gl.shader.ShaderConstants;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderParser;

import java.io.InputStream;
import java.util.Scanner;

public class ShaderLoader {
    public static String parse(String id) {
        // 直接读取并预处理shader文件，替换所有#import指令
        String source = getShaderSource(id);
        source = preprocessShaderImports(source, id);
        
        return "#version 460 core\n" + 
               ShaderParser.parseShader("\n" + source + "\n//beans", ShaderConstants.builder().build())
               .replaceAll("\r\n", "\n")
               .replaceFirst("\n#version .+\n", "\n");
    }
    
    // 预处理shader文件，替换所有#import指令
    private static String preprocessShaderImports(String source, String baseId) {
        StringBuilder result = new StringBuilder();
        String[] lines = source.split("\n");
        
        for (String line : lines) {
            if (line.trim().startsWith("#import")) {
                // 提取导入的shader路径
                String importPath = line.trim().replace("#import <", "").replace(">", "").trim();
                // 递归加载导入的shader
                String importedSource = getShaderSource(importPath);
                // 预处理导入的shader
                importedSource = preprocessShaderImports(importedSource, importPath);
                // 添加到结果中
                result.append(importedSource).append("\n");
            } else {
                // 保留其他行
                result.append(line).append("\n");
            }
        }
        
        return result.toString();
    }
    
    // 读取shader文件内容
    private static String getShaderSource(String id) {
        try {
            String resourcePath;
            
            // 如果是完整路径，直接使用
            if (id.startsWith("/assets/")) {
                resourcePath = id;
            } else {
                // 解析id为namespace和path
                int colonIndex = id.indexOf(':');
                if (colonIndex == -1) {
                    // 假设是voxy命名空间下的相对路径
                    resourcePath = "/assets/voxy/shaders/" + id;
                } else {
                    String namespace = id.substring(0, colonIndex);
                    String path = id.substring(colonIndex + 1);
                    resourcePath = "/assets/" + namespace + "/shaders/" + path;
                }
            }
            
            // 使用类加载器读取资源
            InputStream is = ShaderLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new RuntimeException("Shader not found: " + resourcePath);
            }
            
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String content = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
            is.close();
            
            return content;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader: " + id, e);
        }
    }
}
