package de.clmpvp.clansystem.util;

import de.clmpvp.clansystem.ClanSystem;
import org.bukkit.entity.Player;

public class ClanCreationHandler {

    private final ClanManager clanManager;

    public ClanCreationHandler(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    /**
     * Erstellt einen Clan und fügt den Ersteller als Mitglied hinzu.
     *
     * @param name       Der Name des Clans.
     * @param abkuerzung Die Abkürzung des Clans.
     * @return true, wenn der Clan erfolgreich erstellt wurde, false, wenn der Clan bereits existiert.
     */
    public boolean createClan(String name, String abkuerzung, Player player,  String ownerName) {
        if (clanManager.createClan(name, abkuerzung, ownerName)) {
            // Füge den Spieler als Mitglied hinzu
            clanManager.addMemberToClan(name, player.getUniqueId(), ownerName);
            player.sendMessage(ClanSystem.prefix + "Clan '" + name + "' wurde erfolgreich erstellt. Du bist dem Clan beigetreten.");
            return true;
        } else {
            player.sendMessage(ClanSystem.prefix + "Ein Clan mit dem Namen '" + name + "' existiert bereits.");
            return false;
        }
    }
}
