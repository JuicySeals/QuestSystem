package dev.blackgate.questsystem;

import dev.blackgate.questsystem.coin.CoinManager;
import dev.blackgate.questsystem.coin.listeners.PlayerJoinListener;
import dev.blackgate.questsystem.commands.CommandManager;
import dev.blackgate.questsystem.commands.subcommands.CreateQuestSubCommand;
import dev.blackgate.questsystem.commands.subcommands.HelpSubCommand;
import dev.blackgate.questsystem.database.Database;
import dev.blackgate.questsystem.quest.creation.QuestCreationManager;
import dev.blackgate.questsystem.quest.creation.listeners.QuestCoinGuiListener;
import dev.blackgate.questsystem.quest.creation.listeners.QuestItemsGuiListener;
import dev.blackgate.questsystem.quest.creation.listeners.QuestTypeListener;
import dev.blackgate.questsystem.quest.creation.listeners.QuestXpGuiListener;
import dev.blackgate.questsystem.util.Logger;
import dev.blackgate.questsystem.util.config.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class QuestSystem extends JavaPlugin {
    private CommandManager commandManager;
    private Database database;
    private ConfigHelper configHelper;
    private PluginManager pluginManager;
    private CoinManager coinManager;
    private QuestCreationManager questCreationManager;
    @Override
    public void onEnable() {
        // Order is very important
        saveDefaultConfig();
        Logger.info("Registering utility");
        registerUtil();
        Logger.info("Registering managers");
        registerManagers();
        Logger.info("Developled by JuicySeals");
        Logger.info("Registering listeners");
        registerListeners();
        Logger.info("Registering commands");
        registerCommands();
        Logger.info("Registering subcommands");
        registerSubCommands();
        Logger.info("Connecting to database");
        try {
            initDatabase();
        }catch (Exception e) {
            Logger.severe("Failed to connect to database!");
        }
        Logger.info("Finished");
    }

    @Override
    public void onDisable() {
        Logger.info("Shutting down");
    }

    private void registerListeners() {
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new QuestTypeListener(this), this);
        pluginManager.registerEvents(new QuestXpGuiListener(this), this);
        pluginManager.registerEvents(new QuestCoinGuiListener(this), this);
        pluginManager.registerEvents(new QuestItemsGuiListener(this), this);
    }

    private void registerManagers() {
        commandManager = new CommandManager(this);
        getCommand("quests").setExecutor(commandManager);
        coinManager = new CoinManager(this);
        pluginManager = Bukkit.getPluginManager();
        questCreationManager = new QuestCreationManager();
    }

    private void registerCommands() {

    }

    private void registerUtil() {
        configHelper = new ConfigHelper(this);
    }

    private void registerSubCommands() {
        commandManager.registerSubCommand(new CreateQuestSubCommand(this));
        commandManager.registerSubCommand(new HelpSubCommand(this));
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ConfigHelper getConfigHelper() {
        return configHelper;
    }

    private void initDatabase() {
        FileConfiguration fc = getConfig();
        String host = fc.getString("database.host");
        int port = fc.getInt("database.port");
        String username = fc.getString("database.username");
        String password = fc.getString("database.password");
        String databaseName = fc.getString("database.name");
        database = new Database(host,port,username,password,databaseName);
    }

    public Database getDatabase() {
        return database;
    }
    public CoinManager getCoinManager() {return coinManager;}

    public QuestCreationManager getQuestCreationManager() {
        return questCreationManager;
    }
}
