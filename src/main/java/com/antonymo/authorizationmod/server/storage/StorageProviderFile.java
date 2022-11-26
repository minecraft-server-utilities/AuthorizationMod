package com.antonymo.authorizationmod.server.storage;

import com.antonymo.authorizationmod.utils.SHA256;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
@OnlyIn(Dist.DEDICATED_SERVER)
public class StorageProviderFile implements StorageProvider {
    private final Gson gson = new Gson();
    private final Path path;

    public StorageProviderFile(Path path) {
        this.path = path;
    }

    @Override
    public boolean checkPassword(String username, String password) {
        if (GetEntries().containsKey(username)) {
            var entry = GetEntries().get(username);
            var passwordHash = SHA256.getSHA256(password + entry.salt);
            return entry.passwordHash.equals(passwordHash);
        }
        return false;
    }

    @Override
    public boolean registered(String username) {
        return GetEntries().containsKey(username);
    }


    @Override
    public Collection<String> getAllRegisteredUsername() {
        return new ImmutableList.Builder<String>().addAll(GetEntries().keySet()).build();
    }

    private Map<String, UserEntry> GetEntries() {
        try {
            var buf = gson.fromJson(Files.newBufferedReader(path, StandardCharsets.UTF_8), UserEntry[].class);
            if (buf != null) {
                var entries = new ConcurrentHashMap<String, UserEntry>();
                Arrays.stream(buf).forEach(e -> entries.put(e.username, e));
                return entries;
            } else {
                return new ConcurrentHashMap<>();
            }
        } catch (IOException e) {
            return new ConcurrentHashMap<>();
        }
    }


    private static class UserEntry {
        public final String username;
        public final String passwordHash;
        public final String salt;

        public UserEntry(String username, String passwordHash, String salt) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.salt = salt;
        }
    }
}
