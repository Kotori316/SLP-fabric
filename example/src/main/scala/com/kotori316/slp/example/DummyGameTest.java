package com.kotori316.slp.example;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public final class DummyGameTest implements FabricGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void test(GameTestHelper helper) {
        helper.succeed();
    }
}
