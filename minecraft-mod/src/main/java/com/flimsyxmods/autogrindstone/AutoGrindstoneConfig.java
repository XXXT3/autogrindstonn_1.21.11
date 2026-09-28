package com.flimsyxmods.autogrindstone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AutoGrindstoneConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("auto_grindstone.json");

    public int actionsPerSecond = 4;
    public boolean syncToServerTps = true;
    public boolean includeEnchantedBooks = true;

    public static AutoGrindstoneConfig load() {
        AutoGrindstoneConfig config;
        try {
            if (Files.exists(FILE)) {
                config = GSON.fromJson(Files.readString(FILE), AutoGrindstoneConfig.class);
                if (config == null) {
                    config = new AutoGrindstoneConfig();
                }
            } else {
                config = new AutoGrindstoneConfig();
            }
        } catch (IOException | RuntimeException exception) {
            config = new AutoGrindstoneConfig();
        }

        config.actionsPerSecond = clamp(config.actionsPerSecond, 1, 20);
        save(config);
        return config;
    }

    public static void save(AutoGrindstoneConfig config) {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(config));
        } catch (IOException exception) {
            // The mod remains usable with defaults if the config cannot be persisted.
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}