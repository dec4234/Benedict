package net.dec4234.modules.scheduled;

import net.dec4234.database.collections.UserManagement;
import net.dec4234.database.framework.DatabaseConfig;
import net.dec4234.database.framework.MongoKey;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateSchedules {

	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	private UserManagement userManagement = new UserManagement();

	public UpdateSchedules() {
		scheduledExecutorService.scheduleAtFixedRate(runnable, 10, 60, TimeUnit.MINUTES);
	}

	private Runnable runnable = () -> {
		DatabaseConfig databaseConfig = new DatabaseConfig();

		if(databaseConfig.getBoolean(MongoKey.UPDATE_DISCORD_USERNAMES.getKey())) {
			updateDiscordUsernames();
		}

		if (databaseConfig.getBoolean(MongoKey.SCAN_MEMBERS.getKey())) {
			scanMembers();
		}

		if(databaseConfig.getBoolean(MongoKey.UPDATE_DATABASE_NAMES.getKey())) {
			updateDatabaseNames();
		}
	};

	private void updateDiscordUsernames() {
		userManagement.updateDiscordUsernames();
	}

	private void scanMembers() {
		userManagement.searchForMissingMembers();
	}

	private void updateDatabaseNames() { userManagement.updateDatabaseNames(); }
}
