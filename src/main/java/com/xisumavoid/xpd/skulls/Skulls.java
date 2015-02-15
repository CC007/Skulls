package com.xisumavoid.xpd.skulls;

import com.xisumavoid.xpd.skulls.commands.SkullsTabCompleter;
import com.xisumavoid.xpd.skulls.commands.SkullsCommand;
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
public class Skulls extends JavaPlugin {

    private Plugin vault = null;
    private Permission permission = null;
    private SkullsUtils skullsUtils;

    @Override
    public void onEnable() {
        /* Setup the utils */
        skullsUtils = new SkullsUtils(this);
        skullsUtils.loadSkulls();
        
        /* Setup plugin hooks */
        vault = getPlugin("Vault");
        if (vault != null) {
            setupPermissions();
        }
        /* Register commands */
        getCommand("specialskull").setExecutor(new SkullsCommand(skullsUtils));
        getCommand("specialskull").setTabCompleter(new SkullsTabCompleter(skullsUtils));

        /* Config stuffs */
        this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        skullsUtils.unloadSkulls();
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

    public Plugin getVault() {
        return vault;
    }

    public Permission getPermission() {
        return permission;
    }
}
