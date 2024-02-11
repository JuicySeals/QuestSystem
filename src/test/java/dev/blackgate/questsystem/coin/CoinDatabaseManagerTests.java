package dev.blackgate.questsystem.coin;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.blackgate.questsystem.QuestSystem;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.database.DatabaseCredentials;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CoinDatabaseManagerTests {
    private static ServerMock server;
    private static CoinDatabaseManager coinDatabaseManager;
    private static Player player;

    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        MockBukkit.load(QuestSystem.class);
        DatabaseCredentials credentials = new DatabaseCredentials()
                .setHost("168.100.163.69")
                .setPort(3306)
                .setDatabaseName("s249_db")
                .setUsername("u249_l74SP9M2pT")
                .setPassword("c5Z1lYj9des.HaGkb7B3OtKv");
        Database database = new Database(credentials);
        coinDatabaseManager = new CoinDatabaseManager(database);
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
        coinDatabaseManager.removePlayer(player).join();
    }

    @Test
    void setCoins() {
        assertDoesNotThrow(() -> coinDatabaseManager.setCoins(player, 1).join());
    }

    @Test
    void getCoins() {
        coinDatabaseManager.addPlayer(player).join();
        coinDatabaseManager.setCoins(player, 1).join();
        int coins = coinDatabaseManager.getCoins(player).join();
        assertEquals(1, coins);
    }

    @Test
    void addPlayer() {
        assertDoesNotThrow(() -> coinDatabaseManager.addPlayer(player).join());
    }

    @Test
    void removePlayer() {
        coinDatabaseManager.addPlayer(player).join();
        assertDoesNotThrow(() -> coinDatabaseManager.removePlayer(player).join());
        assertFalse(coinDatabaseManager.isPlayerInDatabase(player).join());
    }
}
