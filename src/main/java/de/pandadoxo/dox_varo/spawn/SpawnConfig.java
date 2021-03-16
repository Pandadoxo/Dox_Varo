// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:11 
// -----------------------

package de.pandadoxo.dox_varo.spawn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpawnConfig {

    public List<Spawn> spawns = new ArrayList<>();

    public Spawn getCenterSpawn() {
        for (Spawn spawn : spawns) {
            if (spawn.isCenterSpawn()) {
                return spawn;
            }
        }
        return null;
    }

    public Spawn getSpawn(int number) {
        for (Spawn spawn : spawns) {
            if (spawn.getNumber() == number && !spawn.isCenterSpawn()) {
                return spawn;
            }
        }
        return null;
    }

    public int getHeighestSpawn() {
        int highest = -1;
        for (Spawn spawn : spawns) {
            if (!spawn.isCenterSpawn() && spawn.getNumber() > highest) {
                highest = spawn.getNumber();
            }
        }
        return highest;
    }

    public void fixNumbers() {
        List<Spawn> sortedSpawns = new ArrayList<>(spawns);
        sortedSpawns.sort(Comparator.comparingInt(Spawn::getNumber));

        int current = 0;
        for (Spawn spawn : sortedSpawns) {
            if (spawn.isCenterSpawn()) {
                continue;
            }
            if (spawn.getNumber() != current) {
                spawn.setNumber(current);
            }
            current++;
        }
    }

    public int getSpawnCount() {
        int count = 0;
        for (Spawn spawn : spawns) {
            if (spawn.isCenterSpawn()) {
                continue;
            }
            count++;
        }
        return count;
    }

}
