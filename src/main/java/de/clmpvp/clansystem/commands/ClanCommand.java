package de.clmpvp.clansystem.commands;

import de.clmpvp.clansystem.ClanSystem;
import de.clmpvp.clansystem.util.Clan;
import de.clmpvp.clansystem.util.ClanCreationHandler;
import de.clmpvp.clansystem.util.ClanManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanCommand implements CommandExecutor {

    private final ClanManager clanManager;
    private final ClanCreationHandler clanCreationHandler;
    private final boolean isSetBaseEnabled;
    private final Map<UUID, UUID> pendingInvites = new HashMap<>();

    public ClanCommand(ClanSystem plugin) {
        this.clanManager = plugin.getClanManager();
        this.isSetBaseEnabled = plugin.isSetBaseEnabled();
        this.clanCreationHandler = new ClanCreationHandler(clanManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ClanSystem.prefix + "Dieser Befehl kann nur von einem Spieler verwendet werden.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ClanSystem.prefix + "Clan-Befehle:");
            player.sendMessage(ClanSystem.prefix + "/clan create <name> <abkürzung> - Erstelle einen neuen Clan");
            player.sendMessage(ClanSystem.prefix + "/clan delete <name> - Lösche einen Clan");
            player.sendMessage(ClanSystem.prefix + "/clan invite <player> - Lade einen Spieler in den Clan ein");
            player.sendMessage(ClanSystem.prefix + "/clan accept - Akzeptiere eine Clan-Einladung");
            if (isSetBaseEnabled) {
                player.sendMessage(ClanSystem.prefix + "/clan setbase - Setze die Basis des Clans an deine aktuelle Position");
                player.sendMessage(ClanSystem.prefix + "/clan home - Teleportiere dich zur Clan-Basis");
            }
            player.sendMessage(ClanSystem.prefix + "/clan info - Sehe Infos über deinen Clan");

            return true;
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
            String clanName = args[1];
            String abkuerzung = args[2];
            String ownerName = args[3];

            if (clanManager.createClan(clanName, abkuerzung, ownerName)) {
                player.sendMessage(ClanSystem.prefix + "Clan '" + clanName + "' wurde erfolgreich erstellt.");
            } else {
                player.sendMessage(ClanSystem.prefix + "Ein Clan mit dem Namen '" + clanName + "' existiert bereits oder der Besitzer ist ungültig.");
            }
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            String name = args[1];

            if (clanManager.deleteClan(name)) {
                player.sendMessage(ClanSystem.prefix + "Clan '" + name + "' wurde gelöscht!");
            } else {
                player.sendMessage(ClanSystem.prefix + "Ein Clan mit dem Namen '" + name + "' existiert nicht.");
            }
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            String clanName = args[1];
            UUID playerUUID = player.getUniqueId();

            if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
                String playerName = args[1];
                Player invitedPlayer = Bukkit.getPlayer(playerName);

                if (invitedPlayer == null) {
                    player.sendMessage(ClanSystem.prefix + "Der Spieler '" + playerName + "' ist nicht online.");
                    return true;
                }

                String playerClan = null;
                for (Clan clan : clanManager.getClans().values()) {
                    if (clan.getMembers().contains(player.getName())) {
                        playerClan = clan.getName();
                        break;
                    }
                }

                if (playerClan == null) {
                    player.sendMessage(ClanSystem.prefix + "Du musst Mitglied eines Clans sein, um jemanden einzuladen.");
                    return true;
                }

                if (clanManager.invitePlayerToClan(playerClan, invitedPlayer.getUniqueId())) {
                    player.sendMessage(ClanSystem.prefix + "Du hast '" + playerName + "' in deinen Clan eingeladen.");

                    // Erstelle die Nachricht mit den Accept- und Deny-Buttons
                    TextComponent message = new TextComponent(ClanSystem.prefix + "Du wurdest in den Clan '" + playerClan + "' eingeladen. ");

                    TextComponent acceptButton = new TextComponent("[accept]");
                    acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept"));
                    acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    message.addExtra(acceptButton);

                    message.addExtra(" ");

                    TextComponent denyButton = new TextComponent("[deny]");
                    denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan deny"));
                    denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
                    message.addExtra(denyButton);

                    invitedPlayer.spigot().sendMessage(message);
                } else {
                    player.sendMessage(ClanSystem.prefix + "Fehler beim Einladen des Spielers.");
                }
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("accept")) {
                if (clanManager.acceptInvite(player.getUniqueId(), player.getName())) {
                    player.sendMessage(ClanSystem.prefix + "Du bist dem Clan beigetreten!");
                    // Nachricht an den einladenden Spieler
                    Player inviter = Bukkit.getPlayer(getInviter(player.getUniqueId()));
                    if (inviter != null) {
                        inviter.sendMessage(ClanSystem.prefix + player.getName() + " hat die Einladung angenommen und ist dem Clan beigetreten.");
                    }
                } else {
                    player.sendMessage(ClanSystem.prefix + "Du hast keine ausstehende Clan-Einladung.");
                }
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("deny")) {
                if (denyInvite(player.getUniqueId())) {
                    player.sendMessage(ClanSystem.prefix + "Du hast die Clan-Einladung abgelehnt.");
                    // Nachricht an den einladenden Spieler
                    Player inviter = Bukkit.getPlayer(getInviter(player.getUniqueId()));
                    if (inviter != null) {
                        inviter.sendMessage(ClanSystem.prefix + player.getName() + " hat die Einladung abgelehnt.");
                    }
                } else {
                    player.sendMessage(ClanSystem.prefix + "Du hast keine ausstehende Clan-Einladung.");
                }
                return true;
            }

            if (isSetBaseEnabled && args.length == 1 && args[0].equalsIgnoreCase("setbase")) {
                String playerClan = null;
                for (Clan clan : clanManager.getClans().values()) {
                    if (clan.getMembers().contains(player.getName())) {
                        playerClan = clan.getName();
                        break;
                    }
                }

                if (playerClan == null) {
                    player.sendMessage(ClanSystem.prefix + "Du musst Mitglied eines Clans sein, um eine Basis zu setzen.");
                    return true;
                }

                Location baseLocation = player.getLocation();
                if (clanManager.setClanBase(playerClan, baseLocation)) {
                    player.sendMessage(ClanSystem.prefix + "Die Clan-Basis wurde auf deine aktuelle Position gesetzt.");
                } else {
                    player.sendMessage(ClanSystem.prefix + "Fehler beim Setzen der Clan-Basis.");
                }
                return true;
            }

            if (isSetBaseEnabled && args.length == 1 && args[0].equalsIgnoreCase("home")) {
                String playerClan = null;
                for (Clan clan : clanManager.getClans().values()) {
                    if (clan.getMembers().contains(player.getName())) {
                        playerClan = clan.getName();
                        break;
                    }
                }

                if (playerClan == null) {
                    player.sendMessage(ClanSystem.prefix + "Du musst Mitglied eines Clans sein, um zur Clan-Basis zu teleportieren.");
                    return true;
                }

                Location baseLocation = clanManager.getClanBase(playerClan);
                if (baseLocation != null) {
                    player.teleport(baseLocation);
                    player.sendMessage(ClanSystem.prefix + "Du wurdest zur Clan-Basis teleportiert.");
                } else {
                    player.sendMessage(ClanSystem.prefix + "Dein Clan hat keine gesetzte Basis.");
                }
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
                Clan playerClan = clanManager.getClanOfPlayer(player.getName());

                if (playerClan == null) {
                    player.sendMessage(ClanSystem.prefix + "Du bist in keinem Clan.");
                    return true;
                }

                // Clan-Informationen anzeigen
                player.sendMessage(ClanSystem.prefix + "Clan: " + playerClan.getName());
                player.sendMessage(ClanSystem.prefix + "Kürzung: " + playerClan.getAbkürzung());
                player.sendMessage(ClanSystem.prefix + "Member: " + playerClan.getMembers());

                for (String member : playerClan.getMembers()) {
                    player.sendMessage(ClanSystem.prefix + " - " + member);
                }

                return true;
            }

            return false;
        }
        return false;
    }

    public boolean denyInvite(UUID playerUUID) {
        return pendingInvites.remove(playerUUID) != null;
    }

    public UUID getInviter(UUID playerUUID) {
        return pendingInvites.get(playerUUID);
    }
}
