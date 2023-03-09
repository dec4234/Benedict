package net.dec4234.database.collections;

import com.mongodb.client.MongoCursor;
import net.dec4234.database.framework.ClanMember;
import net.dec4234.database.framework.MongoKey;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.clan.Clan;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dec4234.javadestinyapi.material.user.DestinyPlatform;
import net.dec4234.src.BenedictMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UserManagement extends UserDatabase {

	/**
	 * Get a list of members who were known to be in the clan at a specific time
	 * @param time The time you want to get information for
	 */
	public java.util.List<ClanMember> getMembersAtTime(long time) {
		List<ClanMember> list = new ArrayList<>();

		for(Document document : getAllDocuments()) {
			ClanMember clanMember = new ClanMember(document);

			if(clanMember.getJoinDate() <= time) {
				if(clanMember.getLeaveDate() == -1 || (clanMember.getLeaveDate() >= time)) {
					list.add(clanMember);
				}
			}
		}

		return list;
	}

	/**
	 * Confirms that everybody is currently marked as a member and applies a leave date if not
	 */
	public void validateDatabase() {
		Clan clan = CachedValue.getClan();

		for(Document document : getAllDocuments()) {
			ClanMember clanMember = new ClanMember(document);

			boolean isMember = clan.isMember(clanMember.getBungieUser());

			if(isMember != clanMember.isMember()) {
				clanMember.setMember(isMember);

				if(!isMember) {
					clanMember.setLeaveDate(System.currentTimeMillis());
					userLeft(clanMember.getBungieID(), "validateDatabase()");
				}
			}

			if(!isMember && clanMember.getLeaveDate() == -1) {
				clanMember.setLeaveDate(System.currentTimeMillis());
				System.out.println("Updated document of " + clanMember.getDestinyName() + " with leave date attached");
			}

			save(clanMember);
		}
	}

	/**
	 * Check for any members that have left or have been kicked from the clan in-game
	 */
	public void checkForMissingMembers() {
		Clan clan = CachedValue.getClan();
		Guild guild = CachedValue.getGuild();

		MongoCursor mongoCursor = mongoCollection.find().cursor();

		while (mongoCursor.hasNext()) {
			Document document = (Document) mongoCursor.next();
			if (!clan.isMember(document.getString("bungieID")) && document.getBoolean("isMember")) {
				System.out.println(document.getString("destinyDisplayName") + " is not in the clan");
				User user = guild.getJDA().getUserById(document.getString("discordID"));
				if (user != null && guild.isMember(user) && guild.getRoles().contains(guild.getRoleById("432199470906867723"))) {
					guild.removeRoleFromMember(user.getId(), guild.getRoleById("432199470906867723")).queue();
					System.out.println(user.getName() + " removed as member");
				}

				replaceDocument(document, document.append("isMember", false));
			}

		}
	}

	/**
	 * Returns a list of all clan members that are not in the discord server
	 */
	public java.util.List<BungieUser> getMissingDiscordMembers() {
		Clan clan = CachedValue.getClan();
		java.util.List<BungieUser> bungieUserList = new LinkedList<>();

		for (BungieUser bungieUser : clan.getMembers()) {
			ClanMember clanMember = new UserDatabase().getClanMember(bungieUser.getID());

			if (clanMember != null && clanMember.getDiscordID() != null) {
				Member member = CachedValue.getGuild().getMemberById(clanMember.getDiscordID());

				if (member == null) {
					bungieUserList.add(bungieUser);
				}
			}
		}

		return bungieUserList;
	}

	/**
	 * Checks that every clan member has the appropriate roles in the discord server
	 */
	public void discordRoleCheck() {
		Clan clan = CachedValue.getClan();
		Guild guild = CachedValue.getGuild();

		for (BungieUser bungieUser : clan.getMembers()) {
			ClanMember clanMember = new UserDatabase().getClanMember(bungieUser.getID());

			if (clanMember != null) {
				Member member = CachedValue.getGuild().getMemberById(clanMember.getDiscordID());

				if (member != null && !member.getRoles().contains(CachedValue.getMemberRole())) {
					guild.removeRoleFromMember(member, CachedValue.getLFGRole()).queue();
					guild.addRoleToMember(member, CachedValue.getMemberRole()).queue();
				}
			}
		}
	}

	/**
	 * Check the entire roster to see if anybody has left the clan
	 */
	public void searchForMissingMembers() {
		for (ClanMember clanMember : getMissingMembers()) {
			userLeft(clanMember.getBungieID(), "User Left In-Game");
		}
	}

	public java.util.List<ClanMember> getMissingMembers() {
		Clan clan = CachedValue.getClan();
		java.util.List<ClanMember> list = new LinkedList<>();
		MongoCursor mongoCursor = mongoCollection.find().cursor();
		java.util.List<String> bungieUsers = clan.getMembersIDs();

		while (mongoCursor.hasNext()) {
			ClanMember clanMember = new ClanMember((Document) mongoCursor.next());
			if (!bungieUsers.contains(clanMember.getBungieID()) && clanMember.isMember() && !clan.getMembersIDs().contains(clanMember.getBungieID())) {
				list.add(clanMember);
			}
		}

		return list;
	}

	/**
	 * Ensures all members have their proper roles in the discord
	 */
	public void fixRoles() {
		Guild guild = CachedValue.getGuild();

		for (BungieUser bungieUser : CachedValue.getClan().getMembers()) {
			ClanMember clanMember = ClanMember.getClanMember(getUserDocument(bungieUser.getID()));

			if (clanMember != null && clanMember.getDiscordID() != null) {
				Member member = guild.getMemberById(clanMember.getDiscordID());

				if (member != null) {
					guild.addRoleToMember(member, CachedValue.getMemberRole()).queue();
					guild.removeRoleFromMember(member, CachedValue.getLFGRole()).queue();

					if (!clanMember.isMember()) {
						clanMember.setMember(true);
						clanMember.setJoinDate(System.currentTimeMillis());
						save(clanMember);

						EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("User Info Updated").setColor(Color.ORANGE);
						embedBuilder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
						embedBuilder.addField(member.getEffectiveName() + " - " + clanMember.getBungieID(), "User is now marked as a member in the database", false);
						CachedValue.getLog2Channel().sendMessage(embedBuilder.build()).queue();
					}
				}
			}
		}
	}

	/**
	 * Update all usernames of members in the discord server to reflect changes to their destiny names
	 */
	public void updateDiscordUsernames() {
		Guild guild = CachedValue.getGuild();

		for(Document document : getAllDocuments()) {
			ClanMember clanMember = new ClanMember(document);

			BungieUser bungieUser = new BungieUser(clanMember.getBungieID(), DestinyPlatform.STEAM);
			if (clanMember.getDiscordID() != null) {
				String name = bungieUser.getGlobalDisplayName();

				if(name.isEmpty()) {
					name = bungieUser.getDisplayName();
				}

				Member member = guild.getMemberById(clanMember.getDiscordID());

				if (member != null && !name.equals(member.getEffectiveName())) {
					System.out.println("Changed member's name " + member.getEffectiveName() + " to " + name);
					guild.modifyNickname(member, name).queue();
					clanMember.setDestinyName(name);
					clanMember.setDiscordName(member.getEffectiveName());

					save(clanMember);
				}
			}

		}
	}

	/**
	 * Update the usernames inside of the database regardless of current membership
	 */
	public void updateDatabaseNames() {

		for(Document document : getAllDocuments()) {
			ClanMember clanMember = new ClanMember(document);
			BungieUser bungieUser = new BungieUser(clanMember.getBungieID(), DestinyPlatform.STEAM);
			String displayName = bungieUser.getGlobalDisplayName();

			if(!clanMember.getDestinyName().equals(displayName)) {
				clanMember.setDestinyName(displayName);
				save(clanMember);
			}
		}
	}

	public void test() {
		Clan clan = CachedValue.getClan();
		MongoCursor mongoCursor = mongoCollection.find().cursor();

		while (mongoCursor.hasNext()) {
			Document document = (Document) mongoCursor.next();
			if (clan.isMember(document.getString(MongoKey.BUNGIEID.getKey())) && !document.getBoolean(MongoKey.IS_MEMBER.getKey())) {
				System.out.println(new BungieUser(document.getString(MongoKey.BUNGIEID.getKey())).getDisplayName());
				document.remove(MongoKey.LEAVE_DATE.getKey());
				document.append(MongoKey.IS_MEMBER.getKey(), true);
				replaceDocument(getUserDocument(document.getString(MongoKey.BUNGIEID.getKey())), document);
			}
		}
	}

	/**
	 * Indicate that a user has left the clan
	 */
	public void userLeft(String bungieID, String source) {
		ClanMember clanMember = new ClanMember(getUserDocument(bungieID));
		clanMember.setLeaveDate(System.currentTimeMillis());
		clanMember.setMember(false);

		save(clanMember);

		String discordID = clanMember.getDiscordID();
		if (discordID != null) {
			User user = BenedictMain.getJDA().getUserById(discordID);

			EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.RED).setTitle("User Left");

			if (user != null && CachedValue.getGuild().isMember(user)) {
				CachedValue.getGuild().removeRoleFromMember(discordID, CachedValue.getMemberRole()).queue();
				CachedValue.getGuild().addRoleToMember(discordID, CachedValue.getLFGRole()).queue();
				embedBuilder.setThumbnail(user.getEffectiveAvatarUrl());
			}

			embedBuilder.addField(CachedValue.getFancySDF().format(new Date()), "<@" + discordID + ">" +
					"\n" + bungieID, false);

			embedBuilder.setFooter(source);

			CachedValue.getLog2Channel().sendMessage(embedBuilder.build()).queue();
		} else {
			CachedValue.getLog2Channel().sendMessage(clanMember.getDestinyName() + " left (No Discord Data Available)").queue();
		}
	}
}
