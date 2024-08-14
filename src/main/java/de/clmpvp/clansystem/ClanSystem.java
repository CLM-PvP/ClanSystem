package de.clmpvp.clansystem;

import de.clmpvp.clansystem.commands.ClanCommand;
import de.clmpvp.clansystem.util.Clan;
import de.clmpvp.clansystem.util.ClanCreationHandler;
import de.clmpvp.clansystem.util.ClanManager;
import de.clmpvp.clansystem.util.ClanTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class ClanSystem extends JavaPlugin {

    public static String prefix = "§7[§6ᴄʟᴀɴꜱ§7] ";
    private ClanManager clanManager;
    private boolean isSetBaseEnabled;
    private ClanCreationHandler clanCreationHandler;
    private Clan clan;
    private String name;
    private String abkürzung;
    private String ownerUUID;

    @Override
    public void onEnable() {
        if (this.clan != null) {
            UUID ownerUUID = (UUID) this.clan.getOwnerUUID();
        } else {
            getLogger().severe("Clan konnte nicht geladen werden, da es null ist.");
        }

        // Konfiguration laden oder erstellen, wenn sie nicht existiert
        this.saveDefaultConfig();

        // Den Wert aus der config.yml laden
        isSetBaseEnabled = getConfig().getBoolean("enable-setbase", true);

        clanManager = new ClanManager(this, ownerUUID);

        // Registrierung der Befehle und Tab-Completer
        getCommand("clan").setExecutor(new ClanCommand(this));
        getCommand("clan").setTabCompleter(new ClanTabCompleter(clanManager, isSetBaseEnabled));

        getLogger().info("ClanPlugin wurde aktiviert!");
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public boolean isSetBaseEnabled() {
        return isSetBaseEnabled;
    }

    public Clan getClan() {
        return clan;
    }
}
