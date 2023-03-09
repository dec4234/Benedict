package net.dec4234.database.framework;

import lombok.Getter;

public enum MongoKey {

	// USER DOC
	BUNGIEID("bungieID"),
	DESTINY_NAME("destinyDisplayName"),
	PREFERRED_PLATFORM("preferredPlatform"),

	IS_MEMBER("isMember"),

	JOIN_DATE("joinDate"),
	LEAVE_DATE("leaveDate"),

	DISCORDID("discordID"),
	DISCORD_NAME("discordDisplayName"),

	// Config Values
	ONLINE("online"),
	API_TOKEN("api-token"),
	ACCESS_TOKEN("access-token"),
	REFRESH_TOKEN("refresh-token"),
	TOKEN_SET_DATE("token-set-date"),
	SCAN_MEMBERS("scan-members"),
	UPDATE_DISCORD_USERNAMES("update-discord-usernames"),
	UPDATE_DATABASE_NAMES("update-database-names");

	@Getter private String key;

	MongoKey(String key) {
		this.key = key;
	}
}
