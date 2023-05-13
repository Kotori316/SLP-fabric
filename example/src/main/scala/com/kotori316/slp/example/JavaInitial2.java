package com.kotori316.slp.example;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaInitial2 implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaInitial.class);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello from {}", getClass().getName());
    }
}
