package dev.blackgate.questsystem.coin;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.coin.CoinManager;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.util.UUIDConverter;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CoinManagerTests {
    private ServerMock server;
    private QuestSystem questSystem;
    private Database database;
    private CoinManager coinManager;
    private Player player;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        questSystem = MockBukkit.load(QuestSystem.class);
        database = new Database("168.100.163.69", 3306, "u249_l74SP9M2pT", "xwWwOiaGjmF@=53A^r3n72mn", "s249_db");
        coinManager = new CoinManager(database);
        player = server.addPlayer("TEST-PLAYER");
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        coinManager.removePlayer(player);
    }

    @Test
    void testSetCoins() {
        assertTrue(coinManager.setCoins(player, 1));
    }

    @Test
    void testGetCoins() {
        coinManager.addPlayer(player);
        coinManager.setCoins(player, 1);
        assertEquals(1, coinManager.getCoins(player));
    }

    @Test
    void testAddPlayer() {
        assertTrue(coinManager.addPlayer(player));
        assertEquals(0, coinManager.getCoins(player));
    }

    @Test
    void removePlayer() {
        coinManager.addPlayer(player);
        assertTrue(coinManager.removePlayer(player));
        assertEquals(-1, coinManager.getCoins(player)); // tests to confirm player isnt in db
    }

}
