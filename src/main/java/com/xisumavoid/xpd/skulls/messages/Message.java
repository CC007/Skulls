package com.xisumavoid.xpd.skulls.messages;

import net.minecraft.server.v1_8_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Crafted in heart of Wales!
 *
 * @author CaLxCyMru
 */
public class Message {

    // The message to be sent
    private String message = null;

    // Title timing
    private int titleFadeIn = 5;
    private int titleLength = 60;
    private int titleFadeOut = 10;

    public Message(String message) {
        this.message = message;
    }

    public Message(String message, int titleFadeIn, int titleLength, int titleFadeOut) {
        this.message = message;
        this.titleFadeIn = titleFadeIn;
        this.titleLength = titleLength;
        this.titleFadeOut = titleFadeOut;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets the fade in time for the title
     *
     * @param titleFadeIn The new fade in time
     * @return The current instance of this class
     */
    public Message setTitleFadeIn(int titleFadeIn) {
        this.titleFadeIn = titleFadeIn;
        return this;
    }

    /**
     * Sets the time the title is displayed for
     *
     * @param titleLength The new length of the title
     * @return The current instance of this class
     */
    public Message setTitleLength(int titleLength) {
        this.titleLength = titleLength;
        return this;
    }

    /**
     * Sets the fade out time for the title
     *
     * @param titleFadeOut The new fade out time
     * @return The current instance of this class
     */
    public Message setTitleFadeOut(int titleFadeOut) {
        this.titleFadeOut = titleFadeOut;
        return this;
    }

    /**
     * Gets the title fade in time
     *
     * @return Fade in time
     */
    public int getTitleFadeIn() {
        return titleFadeIn;
    }

    /**
     * Gets the title length time
     *
     * @return Title length time
     */
    public int getTitleLength() {
        return titleLength;
    }

    /**
     * Get title fade out time
     *
     * @return Title fade out time
     */
    public int getTitleFadeOut() {
        return titleFadeOut;
    }

    /**
     * Gets the message without any formatting
     *
     * @return The message to be sent with no formatting
     */
    public String getMessage() {
        return this.message;
    }

    public String formatMessage(String message) {
        /*message = ChatColor.translateAlternateColorCodes('&', message.
                        replace("<3", ChatColor.RED + "\u2764" + ChatColor.RESET).
                        replace("(heart)", ChatColor.RED + "\u2764" + ChatColor.RESET).
                        replace("(snowman)", ChatColor.AQUA + "\u2603" + ChatColor.RESET).
                        replace("(java)", "\u2615").
                        replace("(tm)", "\u2122").
                        replace("(mail)", "\u2709").
                        replace("(tick)", "\u2714").
                        replace("(cross)", "\u2716").
                        replace("(music)", "\u266C").
                        replace("(coin)", "\u26C3").
                        replace("(coin-low)", "\u26C2").
                        replace("(battle)", "\u2694")
        ); */
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Gets the message to be sent with colour codes
     *
     * @return The formatted message
     */
    public String getFormattedMessage() {
        return formatMessage(message);
    }

    /**
     * Sends the message to a recipient
     *
     * @param recipient The person who will receive it
     * @return Instance of the current class
     */
    public Message send(CommandSender recipient) {
        recipient.sendMessage(getFormattedMessage());
        return this;
    }

    /**
     * Sends the message to a recipient
     *
     * @param recipient   The person who will receive it
     * @param messageType The way in which to display the message
     * @return Instance of the current class
     */
    public Message send(CommandSender recipient, MessageType messageType) {
        if (!(recipient instanceof Player)) {
            send(recipient);
            return this;
        }
        CraftPlayer player = (CraftPlayer) recipient;
        IChatBaseComponent jsonMessage = ChatSerializer.a(MessageUtils.getJSON(getFormattedMessage()));
        switch (messageType) {
            case CHAT:
                send(recipient);
                return this;
            case HOTBAR:
                IChatBaseComponent barMessage = ChatSerializer.a("{\"text\": \"" + getFormattedMessage() + "\"}");
                PacketPlayOutChat bar = new PacketPlayOutChat(barMessage, (byte) 2);
                player.getHandle().playerConnection.sendPacket(bar);
                return this;
            case TITLE:
                PlayerConnection titleConnection = player.getHandle().playerConnection;
                PacketPlayOutTitle titlePacketPlayOutTimes = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, getTitleFadeIn(), getTitleLength(), getTitleFadeOut());

                PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, jsonMessage);

                titleConnection.sendPacket(titlePacketPlayOutTimes);
                titleConnection.sendPacket(packetPlayOutTitle);
                return this;
            case SUBTITLE:
                PlayerConnection subtitleConnection = player.getHandle().playerConnection;
                PacketPlayOutTitle subtitlePacketPlayOutTimes = new PacketPlayOutTitle(EnumTitleAction.TITLE, null, getTitleFadeIn(), getTitleLength(), getTitleFadeOut());
                subtitleConnection.sendPacket(subtitlePacketPlayOutTimes);

                PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, jsonMessage);
                subtitleConnection.sendPacket(packetPlayOutSubTitle);
                return this;
            default:
                send(recipient);
                break;
        }
        return this;
    }

    /**
     * Sends the message to a multiple recipients
     *
     * @param recipients The person who will receive it
     * @return Instance of the current class
     */
    public Message send(Collection<CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            send(recipient);
        }
        return this;
    }

    /**
     * Sends the message to a multiple recipients
     *
     * @param recipients  The person who will receive it
     * @param messageType The way in which to display the message
     * @return Instance of the current class
     */
    public Message send(Collection<CommandSender> recipients, MessageType messageType) {
        for (CommandSender recipient : recipients) {
            send(recipient, messageType);
        }
        return this;
    }

}
