package com.kotori316.slp.example;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public final class DummyGameTest {
    @GameTest()
    public void test(GameTestHelper helper) {
        helper.succeed();
    }
}