package dev.blackgate.questsystem.coin;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.Database;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CoinManagerTests {
    private static ServerMock server;
    private static QuestSystem questSystem;
    private static Database database;
    private static CoinManager coinManager;
    private static Player player;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        questSystem = MockBukkit.load(QuestSystem.class);
        database = new Database();
        coinManager = new CoinManager(database);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void initializePlayer() {
        player = server.addPlayer("TEST-PLAYER");
    }

    @AfterEach
    public void removePlayerAfterTest() {
        coinManager.removePlayer(player).join();
    }

    @Test
    void setCoins() {
        assertDoesNotThrow(() -> coinManager.setCoins(player, 1).join());
    }

    @Test
    void getCoins() {
        coinManager.setCoins(player, 1).join();
        int coins = coinManager.getCoins(player).join();
        assertEquals(1, coins);
    }

    @Test
    void addPlayer() {
        assertDoesNotThrow(() -> coinManager.addPlayer(player).join());
    }

    @Test
    void removePlayer() {
        coinManager.addPlayer(player).join();
        assertDoesNotThrow(() -> coinManager.removePlayer(player).join());
        assertFalse(coinManager.isPlayerInDatabase(player).join());
    }
}
