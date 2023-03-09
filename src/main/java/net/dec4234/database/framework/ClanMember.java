package net.dec4234.database.framework;

import lombok.Getter;
import lombok.Setter;
import net.dec4234.database.collections.UserManagement;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dec4234.src.BenedictMain;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

public class ClanMember {

	@Getter private String bungieID;
	@Getter @Setter private String destinyName;
	@Getter @Setter private String discordID;
	@Getter @Setter private String discordName;
	@Getter @Setter private boolean isMember;
	@Getter @Setter private long joinDate;
	@Getter @Setter private long leaveDate = -1;
	@Getter @Setter private int preferredPlatform;

	@Setter private Document document;

	public ClanMember() {

	}

	public ClanMember(Document document) {
		this.bungieID = document.getString(MongoKey.BUNGIEID.getKey());
		this.destinyName = document.getString(MongoKey.DESTINY_NAME.getKey());
		this.discordID = document.getString(MongoKey.DISCORDID.getKey());
		this.discordName = document.getString(MongoKey.DISCORD_NAME.getKey());
		this.isMember = document.getBoolean(MongoKey.IS_MEMBER.getKey());

		if(document.containsKey(MongoKey.JOIN_DATE.getKey())) {
			try {
				this.joinDate = document.getLong(MongoKey.JOIN_DATE.getKey());
			} catch (ClassCastException classCastException) {
				this.joinDate = Long.parseLong(document.getString(MongoKey.JOIN_DATE.getKey()));
			}
		}

		if(document.containsKey(MongoKey.LEAVE_DATE.getKey())) {
			try {
				this.leaveDate = document.getLong(MongoKey.LEAVE_DATE.getKey());
			} catch (ClassCastException classCastException) {
				this.leaveDate = Long.parseLong(document.getString(MongoKey.LEAVE_DATE.getKey()));
			}
		}

		// Grandfather in old documents
		if(document.containsKey(MongoKey.PREFERRED_PLATFORM.getKey())) {
			this.preferredPlatform = document.getInteger(MongoKey.PREFERRED_PLATFORM.getKey());
		}

		this.document = document;
	}

	public ClanMember(String bungieID, String destinyName, String discordID, String discordName, boolean isMember, long joinDate) {
		this.bungieID = bungieID;
		this.destinyName = destinyName;
		this.discordID = discordID;
		this.discordName = discordName;
		this.isMember = isMember;
		this.joinDate = joinDate;
	}

	public ClanMember(String bungieID, String discordID, boolean isMember) {
		BungieUser bungieUser = new BungieUser(bungieID);
		User user = BenedictMain.getJDA().getUserById(discordID);

		this.bungieID = bungieID;
		this.destinyName = bungieUser.getDisplayName();
		this.discordID = discordID;
		this.discordName = user.getName();
		this.isMember = isMember;
		this.joinDate = System.currentTimeMillis();
	}

	public static ClanMember getClanMember(Document document) {
		try {
			ClanMember clanMember = new ClanMember();
			clanMember.bungieID = document.getString(MongoKey.BUNGIEID.getKey());
			clanMember.destinyName = document.getString(MongoKey.DESTINY_NAME.getKey());
			clanMember.discordID = document.getString(MongoKey.DISCORDID.getKey());
			clanMember.discordName = document.getString(MongoKey.DISCORD_NAME.getKey());
			clanMember.isMember = document.getBoolean(MongoKey.IS_MEMBER.getKey());

			if (document.containsKey(MongoKey.JOIN_DATE.getKey())) {
				try {
					clanMember.joinDate = document.getLong(MongoKey.JOIN_DATE.getKey());
				} catch (ClassCastException classCastException) {
					clanMember.joinDate = Long.parseLong(document.getString(MongoKey.JOIN_DATE.getKey()));
				}
			}

			if(document.containsKey(MongoKey.LEAVE_DATE.getKey())) {
				try {
					clanMember.leaveDate = document.getLong(MongoKey.LEAVE_DATE.getKey());
				} catch (ClassCastException classCastException) {
					clanMember.leaveDate = Long.parseLong(document.getString(MongoKey.LEAVE_DATE.getKey()));
				}
			}

			// Grandfather in old documents
			if(document.containsKey(MongoKey.PREFERRED_PLATFORM.getKey())) {
				clanMember.preferredPlatform = document.getInteger(MongoKey.PREFERRED_PLATFORM.getKey());
			}

			clanMember.setDocument(document);
			return clanMember;
		} catch (NullPointerException ignored) {
			return null;
		}
	}

	public static ClanMember getClanMemberByBungieID(String bungieID) {
		return new UserManagement().getClanMember(bungieID);
	}

	public static ClanMember getClanMemberByDiscordID(String discordID) {
		return new ClanMember(new UserManagement().getUserDocumentDiscord(discordID));
	}

	public BungieUser getBungieUser() {
		return new BungieUser(getBungieID());
	}
}
