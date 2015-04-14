package com.xisumavoid.xpd.skulls.commands;

import com.xisumavoid.xpd.skulls.utils.CommandUtils;
import com.xisumavoid.xpd.skulls.utils.SkullsUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Autom
 */
public class SkullsCommand implements CommandExecutor {

    private final SkullsUtils skullsUtils;

    public SkullsCommand(SkullsUtils skullsUtils) {
        this.skullsUtils = skullsUtils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("update") && CommandUtils.hasPermission(sender, "skulls.update")) {
            skullsUtils.updateSkulls();
            sender.sendMessage(ChatColor.GOLD + "Now you need to reload this plugin!");
            return true;
        }

        if (!(sender instanceof Player)) {
            return CommandUtils.sendMessage(sender, "&cOnly players can perform this command");
        }

        Player player = (Player) sender;

        if (CommandUtils.hasPermission(sender, "skulls.specialskull")) {
            if (args.length == 0) {
                skullsUtils.openPage(0, player);
                return true;
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("page") && StringUtils.isNumeric(args[1])) {
                skullsUtils.openPage(Integer.parseInt(args[1]) - 1, player);
                return true;
            }
            String skullName = StringUtils.join(args, ' ');
            ItemStack item = skullsUtils.getSkull(skullName);
            if (item == null) {
                return CommandUtils.sendMessage(sender, "&cInvalid name");
            }
            player.getInventory().addItem(item);
            CommandUtils.sendMessage(sender, "&aHere's the skull");
            return true;
        }
        return false;
    }
}
