package com.antonymo.authorizationmod.client;

import com.antonymo.authorizationmod.AuthorizationMod;
import com.google.gson.Gson;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public final class PasswordHolder {
    private static PasswordHolder INSTANCE;

    public static PasswordHolder instance() {
        if (INSTANCE == null) {
            INSTANCE = new PasswordHolder();
        }
        return INSTANCE;
    }

    private static final Path PASSWORD_FILE_PATH = Paths.get(".", ".am_passwords");

    private final Gson gson = new Gson();
    private final Map<String, PasswordEntry> passwords = new HashMap<>();
    private boolean initialized = false;

    private PasswordHolder() {
        initialized = true;

        if (Files.exists(PASSWORD_FILE_PATH)) {
            read();
        }

        set("test", "test");
    }

    private void read() {
        try {
            var buf = gson.fromJson(Files.newBufferedReader(PASSWORD_FILE_PATH, StandardCharsets.UTF_8), PasswordEntry[].class);
            if (buf != null) {
                Arrays.stream(buf).forEach(pe -> passwords.put(pe.ip, pe));
            }
        } catch (IOException e) {
            AuthorizationMod.LOGGER.error("Failed to load password", e);
        }
    }

    private void save() {
        try {
            Files.writeString(PASSWORD_FILE_PATH, gson.toJson(passwords.values().toArray()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            AuthorizationMod.LOGGER.error("Failed to save password", e);
        }
    }

    public Optional<String> get(String ip) {
        if (!initialized) throw new IllegalStateException();
        return Optional.ofNullable(passwords.getOrDefault(ip, null)).map(e -> e.password);
    }

    public void set(String ip, String password) {
        passwords.put(ip, new PasswordEntry(ip, password));
        save();
    }

    private static class PasswordEntry {
        public String ip;
        public String password;

        public PasswordEntry(String ip, String password) {
            this.ip = ip;
            this.password = password;
        }
    }
}
