package de.clmpvp.clansystem.util;

import de.clmpvp.clansystem.ClanSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanManager {

    private final Map<String, Clan> clans = new HashMap<>();
    private final Map<UUID, String> pendingInvites = new HashMap<>();
    private final File file;
    private final FileConfiguration config;
    private String ownerUUID;

    public ClanManager(ClanSystem plugin, String ownerUUID) {
        this.file = new File(plugin.getDataFolder(), "Clans.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadClans();
    }

    private void loadClans() {
        if (config.getConfigurationSection("clans") != null) {
            for (String clanName : config.getConfigurationSection("clans").getKeys(false)) {
                String abkürzung = config.getString("clans." + clanName + ".abkürzung");
                String ownerName = config.getString("clans." + clanName + ".ownerUUID");
                Clan clan = new Clan(clanName, abkürzung, ownerName);
                clans.put(clanName, clan);

                // Lade Mitglieder
                if (config.getConfigurationSection("clans." + clanName + ".members") != null) {
                    for (String member : config.getStringList("clans." + clanName + ".members")) {
                        clan.addMember(member);
                    }
                }

                // Lade Basisposition
                if (config.contains("clans." + clanName + ".base")) {
                    double x = config.getDouble("clans." + clanName + ".base.x");
                    double y = config.getDouble("clans." + clanName + ".base.y");
                    double z = config.getDouble("clans." + clanName + ".base.z");
                    String world = config.getString("clans." + clanName + ".base.world");
                    Location base = new Location(Bukkit.getWorld(world), x, y, z);
                    clan.setBase(base);
                }
            }
        }
    }

    private void saveClans() {
        for (Map.Entry<String, Clan> entry : clans.entrySet()) {
            config.set("clans." + entry.getKey() + ".abkürzung", entry.getValue().getAbkürzung());
            config.set("clans." + entry.getKey() + ".ownerUUID", entry.getValue().getOwnerUUID().toString());
            config.set("clans." + entry.getKey() + ".members", entry.getValue().getMembers());

            // Speichere Basisposition
            if (entry.getValue().getBase() != null) {
                Location base = entry.getValue().getBase();
                config.set("clans." + entry.getKey() + ".base.x", base.getX());
                config.set("clans." + entry.getKey() + ".base.y", base.getY());
                config.set("clans." + entry.getKey() + ".base.z", base.getZ());
                config.set("clans." + entry.getKey() + ".base.world", base.getWorld().getName());
            } else {
                config.set("clans." + entry.getKey() + ".base", null);
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createClan(String name, String abkuerzung, String ownerName) {
        if (clans.containsKey(name)) {
            return false; // Clan existiert bereits
        }

        // Finde den Spieler anhand des Namens
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerName);
        if (owner == null || !owner.hasPlayedBefore()) {
            return false; // Spieler nicht gefunden oder hat nie auf dem Server gespielt
        }

        UUID ownerUUID = owner.getUniqueId();

        // Erstelle den Clan mit dem Besitzer und füge ihn als Mitglied hinzu
        Clan newClan = new Clan(name, abkuerzung, ownerUUID);
        clans.put(name, newClan);
        saveClans();
        return true;
    }

    public boolean deleteClan(String name) {
        if (!clans.containsKey(name)) {
            return false;
        }
        clans.remove(name);
        config.set("clans." + name, null);
        saveClans();
        return true;
    }

    public boolean invitePlayerToClan(String clanName, UUID playerUUID) {
        if (!clans.containsKey(clanName)) {
            return false;
        }
        pendingInvites.put(playerUUID, clanName);
        return true;
    }

    public boolean acceptInvite(UUID playerUUID, String playerName) {
        if (!pendingInvites.containsKey(playerUUID)) {
            return false;
        }
        String clanName = pendingInvites.remove(playerUUID);
        Clan clan = clans.get(clanName);
        clan.addMember(playerName);
        saveClans();
        return true;
    }

    public boolean setClanBase(String clanName, Location base) {
        if (!clans.containsKey(clanName)) {
            return false;
        }
        Clan clan = clans.get(clanName);
        clan.setBase(base);
        saveClans();
        return true;
    }

    public Location getClanBase(String clanName) {
        if (!clans.containsKey(clanName)) {
            return null;
        }
        return clans.get(clanName).getBase();
    }

    public Map<String, Clan> getClans() {
        return clans;
    }

    public boolean addMemberToClan(String clanName, UUID playerUUID, String playerName) {
        Clan clan = clans.get(clanName);
        if (clan == null) {
            return false; // Clan existiert nicht
        }
        clan.getMembers().add(playerName);
        saveClans();
        return true;
    }

    public Clan getClan(String name) {
        return clans.get(name);
    }
}

