package dev.blackgate.questsystem.progression;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.database.DatabaseCredentials;
import dev.blackgate.questsystem.quest.Quest;
import dev.blackgate.questsystem.quest.QuestManager;
import dev.blackgate.questsystem.quest.enums.QuestType;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProgressionDatabaseManagerTests {
    private ServerMock server;
    private QuestSystem questSystem;
    private Player player;
    private QuestManager questManager;

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        questSystem = MockBukkit.load(QuestSystem.class);
        DatabaseCredentials credentials = new DatabaseCredentials()
                .setHost("168.100.163.69")
                .setPort(3306)
                .setDatabaseName("s249_db")
                .setUsername("u249_l74SP9M2pT")
                .setPassword("c5Z1lYj9des.HaGkb7B3OtKv");
        questSystem.setDatabase(new Database(credentials));
        questManager = new QuestManager(questSystem);
        Quest quest = new Quest("TEST", "TEST-DESC", "TEST-PERMISSION", QuestType.KILL_ENTITIES, Collections.emptyList(), "ZOMBIE", 1);
        questManager.registerQuest(quest).join();
        player = server.addPlayer("TEST-PLAYER");
    }

    @AfterEach
    public void removeQuest() {
        QuestManager questManager = new QuestManager(questSystem);
        questManager.resetTables();
    }

    @Test
    void testAddQuest() {
        assertEquals(1, questManager.getQuests().size());
    }

}
