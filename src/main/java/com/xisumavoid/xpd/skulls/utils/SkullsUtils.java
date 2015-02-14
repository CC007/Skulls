package com.xisumavoid.xpd.skulls.utils;

import com.xisumavoid.xpd.skulls.IconMenu;
import com.xisumavoid.xpd.skulls.IconMenu.OptionClickEventHandler;
import com.xisumavoid.xpd.skulls.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONArray;

/**
 *
 * @author Autom
 */
public class SkullsUtils {

    private static List<IconMenu> pages = new ArrayList<>();
    private static List<String> names = new ArrayList<>();
    private static int slot = 0;

    public static void addSkull(String name, UUID owner, String value) {
        final int rowsPerPage = Main.instance.getConfig().getInt("rowsperpage");
        final int size = pages.size();
        if (slot == 0) {
            IconMenu iconMenu = new IconMenu("Page " + (size + 1), (rowsPerPage + 1) * 9, new OptionClickEventHandler() {

                @Override
                public void onOptionClick(final IconMenu.OptionClickEvent event) {
                    event.setWillClose(true);
                    if (event.getPosition() / 9 != rowsPerPage) {
                        event.getPlayer().getInventory().addItem(event.getItem());
                        CommandUtils.sendMessage(event.getPlayer(), "&aHere's the skull");
                    }
                    if (event.getPosition() == rowsPerPage * 9) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                pages.get(size - 1).open(event.getPlayer());
                            }
                        }, Main.instance.getConfig().getLong("delay"));
                    }
                    if (event.getPosition() == ((rowsPerPage + 1) * 9) - 1) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
                            @Override
                            public void run() {
                                pages.get(size + 1).open(event.getPlayer());
                            }
                        }, Main.instance.getConfig().getLong("delay"));
                    }
                }
            });

            if (!pages.isEmpty()) {
                ItemStack skull1 = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
                SkullMeta skullMeta1 = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
                skullMeta1.setOwner("MHF_ArrowRight");
                skull1.setItemMeta(skullMeta1);
                pages.get(size - 1).setOption(((rowsPerPage + 1) * 9) - 1, skull1, "Next", "");

                ItemStack skull2 = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
                SkullMeta skullMeta2 = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
                skullMeta2.setOwner("MHF_ArrowLeft");
                skull2.setItemMeta(skullMeta2);
                iconMenu.setOption(rowsPerPage * 9, skull2, "Previous", "");
            }
            pages.add(iconMenu);
        }

        net.minecraft.server.v1_8_R1.ItemStack nmsStack = new net.minecraft.server.v1_8_R1.ItemStack(net.minecraft.server.v1_8_R1.Items.SKULL, 1, 3);
        nmsStack.setTag(new NBTTagCompound());
        NBTTagCompound displayTag = new NBTTagCompound();
        NBTTagCompound entryTag = new NBTTagCompound();
        NBTTagCompound propertiesTag = new NBTTagCompound();
        NBTTagCompound skullOwnerTag = new NBTTagCompound();

        NBTTagList texturesList = new NBTTagList();

        displayTag.setString("Name", name);

        entryTag.setString("Value", value);

        texturesList.add(entryTag);

        propertiesTag.set("textures", texturesList);

        skullOwnerTag.setString("Id", owner.toString());
        skullOwnerTag.set("Properties", propertiesTag);

        nmsStack.getTag().set("display", displayTag);
        nmsStack.getTag().set("SkullOwner", skullOwnerTag);

        pages.get(pages.size() - 1).setOption(slot, CraftItemStack.asBukkitCopy(nmsStack), name, "");
        slot = (slot + 1) % (rowsPerPage * 9);
        names.add(name);
    }

    public static void openPage(int page, final Player player) {
        if (page >= pages.size() ||page < 0) {
            CommandUtils.sendMessage(player, "&cInvalid page number");
            return;
        }
        pages.get(page).open(player);
    }

    public static List<String> getNames() {
        return names;
    }

    public static ItemStack getSkull(String skullName) {
        for (IconMenu page : pages) {
            ItemStack item = page.getItemByName(skullName);
            if (item != null) {
                return item;
            }
        }
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        skullMeta.setOwner(skullName);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static void loadSkulls() {
        JSONArray json = new JSONArray(readUrl(Main.instance.getConfig().getString("url")));
        int i;
        for (i = 0; i < json.length(); i++) {
            String name = json.getJSONObject(i).getString("name");
            UUID skullOwner = UUID.fromString(json.getJSONObject(i).getString("skullowner"));
            String value = json.getJSONObject(i).getString("value");
            addSkull(name, skullOwner, value);
        }
        Main.instance.getLogger().log(Level.INFO, "Loaded {0} skulls", i);
    }

    public static void unloadSkulls() {
        for (IconMenu page : pages) {
            page.destroy();
        }
        pages.clear();
    }

    private static String readUrl(String urlString) {
        HttpURLConnection request = null;
        try {
            URL url = new URL(urlString);
            request = (HttpURLConnection) url.openConnection();

            request.setRequestMethod("POST");

            request.setDoOutput(true);
            request.setDoInput(true);
            request.setInstanceFollowRedirects(false);
            request.setRequestMethod("POST");
            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            request.setRequestProperty("charset", "utf-8");
            request.setUseCaches(false);

            int responseCode = request.getResponseCode();

            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                    StringBuilder stringBuilder = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    return stringBuilder.toString();
                }
            }
        } catch (IOException e) {
            Main.instance.getLogger().severe("URL not found");
        } finally {

            if (request != null) {
                request.disconnect();
            }
        }
        return null;
    }
}
