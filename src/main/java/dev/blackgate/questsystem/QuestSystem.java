package dev.blackgate.questsystem;

import dev.blackgate.questsystem.coin.CoinDatabaseManager;
import dev.blackgate.questsystem.coin.listeners.PlayerCoinJoinListener;
import dev.blackgate.questsystem.commands.CommandManager;
import dev.blackgate.questsystem.commands.impl.CreateQuestSubCommand;
import dev.blackgate.questsystem.commands.impl.HelpSubCommand;
import dev.blackgate.questsystem.commands.impl.ResetQuestsSubCommand;
import dev.blackgate.questsystem.commands.impl.ViewQuestsSubCommand;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.database.DatabaseCredentials;
import dev.blackgate.questsystem.database.ProgressionDatabaseManager;
import dev.blackgate.questsystem.quest.QuestManager;
import dev.blackgate.questsystem.quest.creation.QuestCreationManager;
import dev.blackgate.questsystem.quest.creation.listeners.QuestGuiListener;
import dev.blackgate.questsystem.quest.listeners.PlayerJoinQuestInfoListener;
import dev.blackgate.questsystem.util.Logger;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import dev.blackgate.questsystem.util.inventory.InventoryManager;
import dev.blackgate.questsystem.util.inventory.ItemPDC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class QuestSystem extends JavaPlugin {
    private CommandManager commandManager;
    private Database database;
    private ConfigHelper configHelper;
    private PluginManager pluginManager;
    private CoinDatabaseManager coinDatabaseManager;
    private QuestCreationManager questCreationManager;
    private InventoryManager inventoryManager;
    private QuestManager questManager;
    private ProgressionDatabaseManager progressionDatabaseManager;
    private ItemPDC itemPDC;

    @Override
    public void onEnable() {
        // Order is very important
        saveDefaultConfig();
        Logger.info("Developled by JuicySeals");
        Logger.info("Connecting to database");
        try {
            initDatabase();
        } catch (Exception e) {
            Logger.severe("Failed to connect to database!");
        }
        Logger.info("Registering utility");
        registerUtil();
        Logger.info("Registering managers");
        registerManagers();
        Logger.info("Registering commands");
        registerCommands();
        Logger.info("Registering listeners");
        registerListeners();
        Logger.info("Registering subcommands");
        registerSubCommands();
        Logger.info("Finished");
    }

    @Override
    public void onDisable() {
        Logger.info("Shutting down");
    }

    private void registerListeners() {
        pluginManager.registerEvents(new PlayerCoinJoinListener(this), this);
        pluginManager.registerEvents(new QuestGuiListener(this), this);
        pluginManager.registerEvents(new PlayerJoinQuestInfoListener(this), this);
    }

    private void registerManagers() {
        commandManager = new CommandManager(this);
        coinDatabaseManager = new CoinDatabaseManager(database);
        pluginManager = Bukkit.getPluginManager();
        questCreationManager = new QuestCreationManager();
        inventoryManager = new InventoryManager();
        questManager = new QuestManager(this);
        progressionDatabaseManager = new ProgressionDatabaseManager(this);
    }

    private void registerCommands() {
        getCommand("quests").setExecutor(commandManager);
    }

    private void registerUtil() {
        configHelper = new ConfigHelper(this);
        itemPDC = new ItemPDC(this);
    }

    private void registerSubCommands() {
        commandManager.registerSubCommand(new CreateQuestSubCommand(this));
        commandManager.registerSubCommand(new HelpSubCommand(this));
        commandManager.registerSubCommand(new ViewQuestsSubCommand(this));
        commandManager.registerSubCommand(new ResetQuestsSubCommand(this));
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }

    private void initDatabase() {
        FileConfiguration fc = getConfig();
        DatabaseCredentials credentials = new DatabaseCredentials()
                .setHost(fc.getString("database.host"))
                .setPort(fc.getInt("database.port"))
                .setDatabaseName(fc.getString("database.name"))
                .setUsername(fc.getString("database.username"))
                .setPassword(fc.getString("database.password"));
        database = new Database(credentials);
    }

    public Database getDatabase() {
        return database;
    }

    public CoinDatabaseManager getCoinManager() {
        return coinDatabaseManager;
    }

    public QuestCreationManager getQuestCreationManager() {
        return questCreationManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public ItemPDC getItemPDC() {
        return itemPDC;
    }
    public ProgressionDatabaseManager getProgressionManager() {return progressionDatabaseManager;}
}
