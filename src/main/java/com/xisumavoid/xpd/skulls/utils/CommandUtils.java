package com.xisumavoid.xpd.skulls.utils;

import com.xisumavoid.xpd.skulls.messages.Message;
import org.bukkit.command.CommandSender;

/**
 * Crafted in the heart of Wales!
 *
 * @author CaLxCyMru
 */
public class CommandUtils {
    
    /**
     * Sends a message to a command sender
     *
     * @param sender The sender
     * @param message The message to send to the sender
     * @return Always True
     */
    public static boolean sendMessage(CommandSender sender, String message) {
        new Message(message).send(sender);
        return true;
    }

    /**
     * Check to see if the CommandSender has a certain permission node
     *
     * @param commandSender The player to query
     * @param permission    The permission node to check against the CommandSender
     * @return True: CommandSender has permission, False: CommandSender doesn't have permission
     */
    public static boolean hasPermission(CommandSender commandSender, String permission) {
        return commandSender.hasPermission(permission) || commandSender.isOp();
    }

    /**
     * Sends the player the no permission message
     *
     * @param commandSender The player to send the message to
     * @return Always True
     */
    public static boolean sendNoPermission(CommandSender commandSender) {
        return sendMessage(commandSender, "&cYou do not have permission to use this command!");
    }
}
