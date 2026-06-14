package com.kotori316.slp.example;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.CanEqual;
import scala.util.Properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;

public final class DummyGameTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyGameTest.class);

    @GameTest()
    public void test(GameTestHelper helper) {
        LOGGER.info("Game test started from {}", getClass().getName());
        var scala3Location = CanEqual.class.getProtectionDomain().getCodeSource().getLocation();
        String scala3Version;
        try (
            var fileInputStream = new FileInputStream(scala3Location.getPath());
            var jarStream = new JarInputStream(fileInputStream)
        ) {
            scala3Version = jarStream.getManifest().getMainAttributes().getValue("Implementation-Version");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Java {}, Scala2 {}, Scala3 {}",
            Properties.javaVersion(),
            Properties.versionString(),
            scala3Version
        );
        helper.succeed();
    }
}
