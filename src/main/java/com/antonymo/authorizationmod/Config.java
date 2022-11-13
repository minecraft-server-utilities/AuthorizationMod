package com.antonymo.authorizationmod;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public final class Config {
    public static class Server {
        public final ForgeConfigSpec.IntValue loginTimeout;

        public final ForgeConfigSpec.ConfigValue<String> storageProvider;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> plugins;

        Server(ForgeConfigSpec.Builder builder) {
            builder.push("server");

            loginTimeout = builder
                    .comment("Login Timeout(s)")
                    .defineInRange("loginTimeout", 600, 0, 1200);

            storageProvider = builder
                    .comment("Which storage provider to use")
                    .comment("authorizationmod provides to available providers by default:")
                    .comment("authorizationmod:file -> file based storage")
                    .comment("authorizationmod:sqlite -> sqlite based storage")
                    .comment("Note that you need to add JDBC-sqlite yourself if you want to use sqlite")
                    .define("storageProvider", "authorizationmod:file");

            plugins = builder
                    .comment("Player login handler plugins to load")
                    .comment("authorizationmod:protect_coord is disabled by default, add to here to enable coord protect feature")
                    .defineList("plugins",
                            Arrays.asList(
                                    "authorizationmod:resend_request",
                                    "authorizationmod:restrict_game_type",
                                    "authorizationmod:restrict_movement",
                                    "authorizationmod:timeout"
                            ),
                            o -> o instanceof String);

            builder.pop();
        }
    }

    static final ForgeConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }
}
