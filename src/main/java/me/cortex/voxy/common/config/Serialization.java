package me.cortex.voxy.common.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.cortex.voxy.common.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Serialization {
    public static final Set<Class<?>> CONFIG_TYPES = new HashSet<>();
    public static Gson GSON;

    private static final class GsonConfigSerialization <T> implements TypeAdapterFactory {
        private final String typeField = "TYPE";
        private final Class<T> clz;

        private final Map<String, Class<? extends T>> name2type = new HashMap<>();
        private final Map<Class<? extends T>, String> type2name = new HashMap<>();

        private GsonConfigSerialization(Class<T> clz) {
            this.clz = clz;
        }

        public GsonConfigSerialization<T> register(String typeName, Class<? extends T> cls) {
            if (this.name2type.put(typeName, cls) != null) {
                throw new IllegalStateException("Type name already registered: " + typeName);
            }
            if (this.type2name.put(cls, typeName) != null) {
                throw new IllegalStateException("Class already registered with type name: " + typeName + ", " + cls);
            }
            return this;
        }


        private T deserialize(Gson gson, JsonElement json) {
            var retype = this.name2type.get(json.getAsJsonObject().remove(this.typeField).getAsString());
            return gson.getDelegateAdapter(this, TypeToken.get(retype)).fromJsonTree(json);
        }

        private JsonElement serialize(Gson gson, T value) {
            String name = this.type2name.get(value.getClass());
            if (name == null) {
                name = "UNKNOWN_TYPE_{" + value.getClass().getName() + "}";
            }

            var vjson = gson
                    .getDelegateAdapter(this, TypeToken.get((Class<T>) value.getClass()))
                    .toJsonTree(value);
            //All of this is so that the config_type is at the top :blob_face:
            var json = new JsonObject();
            json.addProperty(this.typeField, name);
            vjson.getAsJsonObject().asMap().forEach(json::add);
            return json;
        }


        @Override
        public <X> TypeAdapter<X> create(Gson gson, TypeToken<X> type) {
            if (this.clz.isAssignableFrom(type.getRawType())) {
                var jsonObjectAdapter = gson.getAdapter(JsonElement.class);

                return (TypeAdapter<X>) new TypeAdapter<T>() {
                    @Override
                    public void write(JsonWriter out, T value) throws IOException {
                        jsonObjectAdapter.write(out, GsonConfigSerialization.this.serialize(gson, value));
                    }

                    @Override
                    public T read(JsonReader in) throws IOException {
                        var obj = jsonObjectAdapter.read(in);
                        return GsonConfigSerialization.this.deserialize(gson, obj);
                    }
                };
            }
            return null;
        }
    }

    public static void init() {
        String BASE_SEARCH_PACKAGE = "me.cortex.voxy";

        Map<Class<?>, GsonConfigSerialization<?>> serializers = new HashMap<>();

        Set<String> clazzs = new LinkedHashSet<>();
        File path = null;
        // Handle different URI schemes
        try {
            java.net.URI uri = Serialization.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (uri.getScheme().equals("file")) {
                // Direct file URI - could be directory or file
                path = new File(uri);
            } else {
                // Non-file URI (jar:, bundle:, etc.) - check if it's a directory-like URI
                java.net.URL url = Serialization.class.getProtectionDomain().getCodeSource().getLocation();
                String urlString = url.toString();
                if (urlString.endsWith("/") || urlString.contains("!/")) {
                    // Directory-like URI or JAR with entry - handle differently
                    // For now, fall back to existing class loader approach
                    clazzs.addAll(collectAllClasses(BASE_SEARCH_PACKAGE));
                } else {
                    // Non-file URI pointing to actual JAR file - copy to temporary file
                    try (java.io.InputStream is = url.openStream()) {
                        path = java.io.File.createTempFile("voxy-", ".jar");
                        path.deleteOnExit(); // Clean up after use
                        byte[] data = is.readAllBytes();
                        java.nio.file.Files.write(path.toPath(), data);
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to get code source location, falling back to class loader approach", e);
            // Fall back to existing class loader approach
            clazzs.addAll(collectAllClasses(BASE_SEARCH_PACKAGE));
        }
        
        // Only call collectAllClasses with path if path is not null
        if (path != null) {
            clazzs.addAll(collectAllClasses(path, BASE_SEARCH_PACKAGE));
        } else {
            // Ensure we still call the class loader approach
            clazzs.addAll(collectAllClasses(BASE_SEARCH_PACKAGE));
        }
        int count = 0;
        outer:
        for (var clzName : clazzs) {
            if (!clzName.toLowerCase().contains("config")) {
                continue;//Only load classes that contain the word config
            }
            if (clzName.contains("mixin")) {
                continue;//Dont want to load mixins
            }
            if (clzName.contains("ModMenuIntegration")) {
                continue;//Dont want to modmenu incase it doesnt exist
            }
            if (clzName.contains("VoxyConfigScreenPages")) {
                continue;//Dont want to modmenu incase it doesnt exist
            }
            if (clzName.endsWith("VoxyConfig")) {
                continue;//Special case to prevent recursive loading pain
            }

            if (clzName.equals(Serialization.class.getName())) {
                continue;//Dont want to load ourselves
            }

            try {
                var clz = Class.forName(clzName);
                if (Modifier.isAbstract(clz.getModifiers())) {
                    //Dont want to register abstract classes as concrete implementations
                    continue;
                }
                var original = clz;
                while ((clz = clz.getSuperclass()) != null) {
                    if (CONFIG_TYPES.contains(clz)) {
                        Method nameMethod = null;
                        try {
                            nameMethod = original.getMethod("getConfigTypeName");
                            nameMethod.setAccessible(true);
                        } catch (NoSuchMethodException e) {}
                        if (nameMethod == null) {
                            Logger.error("WARNING: Config class " + clzName + " doesnt contain a getConfigTypeName and thus wont be serializable");
                            continue outer;
                        }
                        count++;
                        String name = (String) nameMethod.invoke(null);
                        serializers.computeIfAbsent(clz, GsonConfigSerialization::new)
                                .register(name, (Class) original);
                        Logger.info("Registered " + original.getSimpleName() + " as " + name + " for config type " + clz.getSimpleName());
                        break;
                    }
                }
            } catch (Exception e) {
                Logger.error("Error while setting up config serialization", e);
            }
        }
        
        // Now create GsonConfigSerialization instances for all CONFIG_TYPES (including those added during class scanning)
        for (var configType : CONFIG_TYPES) {
            serializers.computeIfAbsent(configType, GsonConfigSerialization::new);
        }

        var builder = new GsonBuilder()
                .setPrettyPrinting();
        for (var entry : serializers.entrySet()) {
            builder.registerTypeAdapterFactory(entry.getValue());
        }

        GSON = builder.create();
        Logger.info("Registered " + count + " config types");
    }

    private static List<String> collectAllClasses(String pack) {
        try {
            InputStream stream = Serialization.class.getClassLoader()
                    .getResourceAsStream(pack.replaceAll("[.]", "/"));
            if (stream == null) {
                // 资源不存在，返回空列表
                return List.of();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            return reader.lines().flatMap(inner -> {
                if (inner.endsWith(".class")) {
                    return Stream.of(pack + "." + inner.replace(".class", ""));
                } else if (!inner.contains(".")) {
                    return collectAllClasses(pack + "." + inner).stream();
                } else {
                    return Stream.of();
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            Logger.error("Failed to collect classes in package: " + pack, e);
            return List.of();
        }
    }
    private static List<String> collectAllClasses(File file, String pack) {
        List<String> classes = new ArrayList<>();
        try {
            if (file.isDirectory()) {
                // Handle directory case
                Path base = file.toPath();
                Path packPath = base.resolve(pack.replaceAll("[.]", "/"));
                if (!Files.exists(packPath)) {
                    return List.of();
                }
                return Files.list(packPath).flatMap(inner -> {
                    if (inner.getFileName().toString().endsWith(".class")) {
                        return Stream.of(pack + "." + inner.getFileName().toString().replace(".class", ""));
                    } else if (Files.isDirectory(inner)) {
                        return collectAllClasses(file, pack + "." + inner.getFileName()).stream();
                    } else {
                        return Stream.of();
                    }
                }).collect(Collectors.toList());
            } else if (file.getName().endsWith(".jar")) {
                // Handle JAR file case
                String packPath = pack.replace('.', '/') + '/';
                try (java.util.jar.JarFile jarFile = new java.util.jar.JarFile(file)) {
                    java.util.Enumeration<java.util.jar.JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        java.util.jar.JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        // Check if entry is a class file and starts with the package path
                        if (entryName.endsWith(".class") && entryName.startsWith(packPath)) {
                            // Convert entry path to fully qualified class name
                            String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                            classes.add(className);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("Failed to collect classes from " + file + ": " + e.getMessage(), e);
        }
        return classes;
    }
    private static List<String> collectAllClasses(Path base, String pack) {
        return collectAllClasses(base.toFile(), pack);
    }
}
