package de.clmpvp.clansystem.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClanTabCompleter implements TabCompleter {

    private final ClanManager clanManager;
    private final boolean isSetBaseEnabled;

    public ClanTabCompleter(ClanManager clanManager, boolean isSetBaseEnabled) {
        this.clanManager = clanManager;
        this.isSetBaseEnabled = isSetBaseEnabled;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // Bietet Hauptbefehle wie create, delete, invite, accept an
            suggestions.add("create");
            suggestions.add("delete");
            suggestions.add("invite");
            suggestions.add("accept");

            if (isSetBaseEnabled) {
                suggestions.add("setbase");
                suggestions.add("home");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete")) {
                // Vorschläge für existierende Clans zum Löschen
                suggestions.addAll(clanManager.getClans().keySet());
            } else if (args[0].equalsIgnoreCase("invite")) {
                // Vorschläge für online Spieler zum Einladen
                for (Player player : Bukkit.getOnlinePlayers()) {
                    suggestions.add(player.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            // Hier könnten Vorschläge für Abkürzungen angeboten werden
            // Zum Beispiel: suggestions.add("ABC");
        }

        return suggestions;
    }
}
