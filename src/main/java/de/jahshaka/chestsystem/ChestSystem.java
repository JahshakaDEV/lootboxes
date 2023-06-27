package de.jahshaka.chestsystem;

import de.jahshaka.chestsystem.commands.LootboxCMD;
import de.jahshaka.chestsystem.commands.SetBalanceCMD;
import de.jahshaka.chestsystem.database.SqlManager;
import de.jahshaka.chestsystem.inventory.GUIListener;
import de.jahshaka.chestsystem.inventory.GUIManager;
import de.jahshaka.chestsystem.listener.PlayerConnectionListener;
import de.jahshaka.chestsystem.utils.config.ConfigManager;
import de.jahshaka.chestsystem.lootbox.LootboxManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestSystem extends JavaPlugin implements Listener {

    private static ChestSystem instance;

    public static GUIManager guiManager;
    public static ConfigManager configManager;
    private SqlManager sqlManager;

    public SqlManager getSqlManager() {
        return sqlManager;
    }

    public static ChestSystem getPlugin() {
        return instance;
    }

    public static LootboxManager lootboxManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        saveDefaultConfig();
        initDatabase();
        setupCommands();
        setupListener();
        setupConfigs();
        setupInventories();
        lootboxManager = new LootboxManager();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        sqlManager.closePool();
    }

    private void setupCommands() {
        this.getCommand("lootbox").setExecutor(new LootboxCMD());
        this.getCommand("setbalance").setExecutor(new SetBalanceCMD());
    }

    private void setupInventories() {
        guiManager = new GUIManager();
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
    }

    private void setupListener() {
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
    }

    private void setupConfigs() {
        configManager.newConfig("lootboxes");
        configManager.newConfig("items");
    }

    protected void initDatabase() {
        sqlManager = new SqlManager(this);
    }
}
