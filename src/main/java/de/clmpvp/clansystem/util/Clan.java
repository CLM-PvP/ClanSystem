package de.clmpvp.clansystem.util;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clan {
    private final String name;
    private final String abkürzung;
    private final List<String> members;
    public String ownerUUID;
    private Location base;

    public Clan(String name, String abkürzung, UUID ownerUUID) {
        this.name = name;
        this.abkürzung = abkürzung;
        this.members = new ArrayList<>();
        this.ownerUUID = String.valueOf(ownerUUID);

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
