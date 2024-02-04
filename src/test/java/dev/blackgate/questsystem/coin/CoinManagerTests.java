package dev.blackgate.questsystem.coin;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.database.DatabaseCredentials;
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
        DatabaseCredentials credentials = new DatabaseCredentials()
                .setHost("168.100.163.69")
                .setPort(3306)
                .setUsername("u249_l74SP9M2pT")
                .setPassword("xwWwOiaGjmF@=53A^r3n72mn")
                .setDatabaseName("s249_db");
        database = new Database(credentials);
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
