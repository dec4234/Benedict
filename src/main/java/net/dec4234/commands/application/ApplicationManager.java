package net.dec4234.commands.application;

import lombok.Getter;
import net.dec4234.javadestinyapi.material.user.BungieUser;

import java.util.HashMap;

public class ApplicationManager {

    private static ApplicationManager instance;

    public static ApplicationManager getInstance() {
        if(instance == null) {
            instance = new ApplicationManager();
        }

        return instance;
    }

    @Getter
    private HashMap<String, BungieUser> selectedUser = new HashMap<>(); // A directory of selected users for applicants
    private HashMap<String, BungieUser> preaccepted = new HashMap<>(); // List of users that have finished the application but have not yet submit a request to join

    private ApplicationManager() {

    }
}
