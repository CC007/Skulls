package com.xisumavoid.xpd.skulls.commands;

import com.xisumavoid.xpd.skulls.utils.SkullsUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

/**
 *
 * @author Autom
 */
public class SkullsTabCompleter implements TabCompleter {

    private final SkullsUtils skullsUtils;
    
    public SkullsTabCompleter(SkullsUtils skullsUtils) {
        this.skullsUtils = skullsUtils;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partialCommand = args[0];
            List<String> commands = skullsUtils.getNames();
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        }

        Collections.sort(completions);

        return completions;
    }

}
