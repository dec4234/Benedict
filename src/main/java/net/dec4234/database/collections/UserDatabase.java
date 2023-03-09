package net.dec4234.database.collections;

import net.dec4234.database.framework.ClanMember;
import net.dec4234.database.framework.MongoFramework;
import net.dec4234.database.framework.MongoKey;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserDatabase extends MongoFramework {

	public UserDatabase() {
		super("Benedict", "userData");
	}

	public void insertData(User user, BungieUser bungieUser, boolean isMember) {
		save(new ClanMember(bungieUser.getID(), bungieUser.getDisplayName(),
							user.getId(), user.getName(), isMember, System.currentTimeMillis()));
	}

	public ClanMember getClanMember(String bungieID) {
		if (hasData(bungieID)) {
			return new ClanMember(getUserDocument(bungieID));
		}

		return null;
	}

	public List<BungieUser> getClanUsers(String name) {
		List<BungieUser> bungieUsers = new LinkedList<>();

		for(Document document : getAllDocuments()) {
			ClanMember clanMember = new ClanMember(document);

			if(clanMember.getDestinyName().equalsIgnoreCase(name)) {
				bungieUsers.add(new BungieUser(clanMember.getBungieID()));
			}
		}

		return bungieUsers;
	}

	public List<ClanMember> getClanMembers() {
		ArrayList<ClanMember> clanMembers = new ArrayList<>();

		for(Document document : getAllDocuments()) {
			clanMembers.add(new ClanMember(document));
		}

		return clanMembers;
	}

	public Document getUserDocument(String bungieID) {
		return (Document) mongoCollection.find(new Document(MongoKey.BUNGIEID.getKey(), bungieID)).first();
	}

	public Document getUserDocumentDiscord(String discordID) {
		return (Document) mongoCollection.find(new Document(MongoKey.DISCORDID.getKey(), discordID)).first();
	}

	/**
	 * Find and replace the old information for the user specific
	 */
	public void replace(String bungieID, Document document) {
		if (hasData(bungieID)) {
			mongoCollection.replaceOne(getUserDocument(bungieID), document);
		} else {
			mongoCollection.insertOne(document);
		}
	}

	/**
	 * Does the collection have data for the user with the following bungieID?
	 */
	public boolean hasData(String bungieID) {
		return getDoc(MongoKey.BUNGIEID.getKey(), bungieID) != null;
	}

	public boolean hasDataDiscord(String discordID) { return getDoc(MongoKey.DISCORDID.getKey(), discordID) != null; }

	public void save(ClanMember clanMember) {
		Document oldDoc = new Document();
		oldDoc.append(MongoKey.BUNGIEID.getKey(), clanMember.getBungieID());
		oldDoc.append(MongoKey.DESTINY_NAME.getKey(), clanMember.getDestinyName());
		oldDoc.append(MongoKey.DISCORDID.getKey(), clanMember.getDiscordID());
		oldDoc.append(MongoKey.DISCORD_NAME.getKey(), clanMember.getDiscordName());
		oldDoc.append(MongoKey.IS_MEMBER.getKey(), clanMember.isMember());
		oldDoc.append(MongoKey.JOIN_DATE.getKey(), clanMember.getJoinDate());
		oldDoc.append(MongoKey.PREFERRED_PLATFORM.getKey(), clanMember.getPreferredPlatform());

		if(clanMember.getLeaveDate() != -1) {
			oldDoc.append(MongoKey.LEAVE_DATE.getKey(), clanMember.getLeaveDate());
		}

		if (hasData(clanMember.getBungieID())) {
			replace(clanMember.getBungieID(), oldDoc);
		} else {
			mongoCollection.insertOne(oldDoc);
		}

	}
}
