package com.xisumavoid.xpd.skulls;

import com.xisumavoid.xpd.skulls.utils.CommandUtils;
import com.xisumavoid.xpd.skulls.utils.SkullsUtils;
import org.apache.commons.lang.StringUtils;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return CommandUtils.sendMessage(sender, "Only players can perform this command");
        }

        Player player = (Player) sender;

        if ((commandLabel.equalsIgnoreCase("specialskull") || commandLabel.equalsIgnoreCase("sps")) && CommandUtils.hasPermission(sender, "skulls.specialskull")) {
            if (args.length == 0) {
                SkullsUtils.openPage(0, player);
                return true;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("page") && StringUtils.isNumeric(args[1])) {
                SkullsUtils.openPage(Integer.parseInt(args[1]), player);
                return true;
            }
            String skullName = StringUtils.join(args, ' ');
            ItemStack item = SkullsUtils.getSkull(skullName);
            if (item == null) {
                return CommandUtils.sendMessage(sender, "Invalid name");
            }
            player.getInventory().addItem(item);
            CommandUtils.sendMessage(sender, "Here's the skull");
            return true;
        }
        return false;
    }
}
