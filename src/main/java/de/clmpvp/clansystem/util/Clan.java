package de.clmpvp.clansystem.util;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clan {
    private String name;
    private final String abkürzung;
    private List<String> members;
    public String ownerUUID;
    private Location base;

    public Clan(String clanName, String abkürzung, String ownerUUID) {
        this.ownerUUID = ownerUUID;
        this.abkürzung = abkürzung;
        this.name = clanName;

    }

    public String getName() {
        return name;
    }

    public String getAbkürzung() {
        return abkürzung;
    }

    public List<String> getMembers() {
        return members;
    }

    public void addMember(String playerName) {
        if (!members.contains(playerName)) {
            members.add(playerName);
        }
    }

    public void removeMember(String playerName) {
        members.remove(playerName);
    }

    public Location getBase() {
        return base;
    }

    public void setBase(Location base) {
        this.base = base;
    }

    public Object getOwnerUUID() {
        return ownerUUID;
    }
}
