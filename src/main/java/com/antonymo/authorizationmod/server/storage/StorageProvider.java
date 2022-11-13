package com.antonymo.authorizationmod.server.storage;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;

@ThreadSafe
@OnlyIn(Dist.DEDICATED_SERVER)
public interface StorageProvider {
    boolean checkPassword(String username, String password);

    boolean registered(String username);


    /**
     * Should be immutable
     *
     * @return all registered username
     */
    Collection<String> getAllRegisteredUsername();
}
