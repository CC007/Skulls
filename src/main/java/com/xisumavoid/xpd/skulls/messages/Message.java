package com.xisumavoid.xpd.skulls.messages;

import java.util.ArrayList;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crafted in heart of Wales!
 *
 * @author CaLxCyMru
 */
public class Message {

    public enum MessageType {
        CHAT, HOTBAR, TITLE, SUBTITLE
    }

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
     * @param recipient The person who will receive it
     * @param messageType The way in which to display the message
     * @return Instance of the current class
     */
    public Message send(CommandSender recipient, MessageType messageType) {
        if (!(recipient instanceof Player)) {
            send(recipient);
            return this;
        }
        CraftPlayer player = (CraftPlayer) recipient;
        IChatBaseComponent jsonMessage = ChatSerializer.a(getJSON(getFormattedMessage()));
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
     * @param recipients The person who will receive it
     * @param messageType The way in which to display the message
     * @return Instance of the current class
     */
    public Message send(Collection<CommandSender> recipients, MessageType messageType) {
        for (CommandSender recipient : recipients) {
            send(recipient, messageType);
        }
        return this;
    }

    /**
     * Converts a regular string into a JSON message
     *
     * @param title The string to convert
     * @return The new JSON message
     */
    private String getJSON(String title) {
        char colorChar = ChatColor.COLOR_CHAR;

        String template = "{text:\"TEXT\",color:COLOR,bold:BOLD,underlined:UNDERLINED,italic:ITALIC,strikethrough:STRIKETHROUGH,obfuscated:OBFUSCATED,extra:[EXTRA]}";
        String json = "";

        List<String> parts = new ArrayList<>();

        int first = 0;
        int last = 0;

        while ((first = title.indexOf(colorChar, last)) != -1) {
            int offset = 2;
            while ((last = title.indexOf(colorChar, first + offset)) - 2 == first) {
                offset += 2;
            }

            if (last == -1) {
                parts.add(title.substring(first));
                break;
            } else {
                parts.add(title.substring(first, last));
            }
        }

        if (parts.isEmpty()) {
            parts.add(title);
        }

        Pattern colorFinder = Pattern.compile("(" + colorChar + "([a-f0-9]))");
        for (String part : parts) {
            json = (json.isEmpty() ? template : json.replace("EXTRA", template));

            Matcher matcher = colorFinder.matcher(part);
            ChatColor color = (matcher.find() ? ChatColor.getByChar(matcher.group().charAt(1)) : ChatColor.WHITE);

            json = json.replace("COLOR", color.name().toLowerCase());
            json = json.replace("BOLD", String.valueOf(part.contains(ChatColor.BOLD.toString())));
            json = json.replace("ITALIC", String.valueOf(part.contains(ChatColor.ITALIC.toString())));
            json = json.replace("UNDERLINED", String.valueOf(part.contains(ChatColor.UNDERLINE.toString())));
            json = json.replace("STRIKETHROUGH", String.valueOf(part.contains(ChatColor.STRIKETHROUGH.toString())));
            json = json.replace("OBFUSCATED", String.valueOf(part.contains(ChatColor.MAGIC.toString())));

            json = json.replace("TEXT", part.replaceAll("(" + colorChar + "([a-z0-9]))", ""));
        }

        json = json.replace(",extra:[EXTRA]", "");

        return json;
    }

}
