package com.xisumavoid.xpd.skulls;

import com.xisumavoid.xpd.skulls.utils.SkullsUtils;
import java.util.logging.Level;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Autom
 */
public class Main extends JavaPlugin {

    public static Main instance;
    public static Plugin vault = null;

    public static Permission permission = null;

    @Override
    public void onEnable() {
        /**
         * Instantiate the variables
         */
        instance = this;

        /**
         * Setup plugin hooks
         */
        setupHooks();
        /**
         * Register commands
         */
        registerCommands();

        /**
         * Config stuffs
         */
        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        SkullsUtils.loadSkulls();
    }

    @Override
    public void onDisable() {
        SkullsUtils.unloadSkulls();

        instance = null;
    }

    /**
     * Used to register commands for the plugin
     */
    public void registerCommands() {
        getCommand("specialskull").setExecutor(new SkullsCommand());
        getCommand("specialskull").setTabCompleter(new SkullsTabCompleter());
    }

    /**
     * Setup plugin hooks
     */
    public void setupHooks() {
        /**
         * Vault
         */
        vault = getPlugin("Vault");
        if (vault != null) {
            setupPermissions();
        }
    }

    /**
     * Setup permissions
     *
     * @return True: Setup correctly, Didn't setup correctly
     */
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        if (permission == null) {
            getLogger().log(Level.WARNING, "Could not hook Vault!");
        } else {
            getLogger().log(Level.WARNING, "Hooked Vault!");
        }

        return (permission != null);
    }

    /**
     * Gets a plugin
     *
     * @param pluginName Name of the plugin to get
     * @return The plugin from name
     */
    private Plugin getPlugin(String pluginName) {
        if (getServer().getPluginManager().getPlugin(pluginName) != null && getServer().getPluginManager().getPlugin(pluginName).isEnabled()) {
            return getServer().getPluginManager().getPlugin(pluginName);
        } else {
            getLogger().log(Level.WARNING, "&cCould not find plugin \"{0}\"!", pluginName);
            return null;
        }
    }
}
