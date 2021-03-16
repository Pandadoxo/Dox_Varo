// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:08 
// -----------------------

package de.pandadoxo.dox_varo.spawn;

import org.bukkit.Location;

public class Spawn {

    private boolean isCenterSpawn;
    private int number;
    private Location location;


    public Spawn(boolean isCenterSpawn, Location location) {
        this.isCenterSpawn = isCenterSpawn;
        this.number = 0;
        this.location = location;
    }

    public Spawn(int number, Location location) {
        this.number = number;
        this.location = location;
        this.isCenterSpawn = false;
    }

    public boolean isCenterSpawn() {
        return isCenterSpawn;
    }

    public void setCenterSpawn(boolean centerSpawn) {
        isCenterSpawn = centerSpawn;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
