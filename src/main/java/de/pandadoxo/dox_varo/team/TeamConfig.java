// -----------------------
// Coded by Pandadoxo
// on 10.03.2021 at 12:07 
// -----------------------

package de.pandadoxo.dox_varo.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamConfig {

    public List<Team> teams = new ArrayList<>();

    public Team getTeam(String name) {
        for (Team team : teams) {
            if (team.getTeamName().equalsIgnoreCase(name) || team.getShortcut().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    public Team getTeamless() {
        for (Team team : teams) {
            if (team.getTeamName().equalsIgnoreCase("Teamlos")) {
                return team;
            }
        }
        return createTeamless();
    }

    public Team createTeamless() {
        teams.add(new Team("Teamlos", "&4&lX", UUID.randomUUID()));
        return getTeamless();
        //WATCHOUT : Possible Loop
    }

}
