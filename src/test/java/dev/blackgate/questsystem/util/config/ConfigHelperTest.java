package dev.blackgate.questsystem.util.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.blackgate.questsystem.QuestSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ConfigHelperTest {
    private ServerMock server;
    private QuestSystem questSystem;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        questSystem = MockBukkit.load(QuestSystem.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testNoPermissionMessage() {
        try {
            assertNotNull(questSystem.getConfigHelper().getNoPermission());
        }catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testConsoleRanMessage() {
        try {
            assertNotNull(questSystem.getConfigHelper().getConsoleRan());
        }catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void testIncorrectUsageMessage() {
        try {
            assertNotNull(questSystem.getConfigHelper().getIncorrectUsage("TEST"));
        }catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
