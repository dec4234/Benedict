package net.dec4234.commands.clan;

import net.dec4234.database.collections.UserManagement;
import net.dec4234.database.framework.ClanMember;
import net.dec4234.framework.commands.SlashCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.DestinyAPI;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dec4234.javadestinyapi.material.user.DestinyPlatform;
import net.dec4234.javadestinyapi.material.user.UserCredentialType;
import net.dec4234.src.BenedictMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bson.Document;

import java.awt.*;
import java.util.Date;
import java.util.LinkedList;

public class WhoIsCommand extends SlashCommand {

	private UserManagement userManagement;

	public WhoIsCommand() {
		super("whois", "Search up what I know about a player", new OptionData(OptionType.STRING, "name", "Name of the player").setRequired(true));
		userManagement = new UserManagement();
	}

	@Override
	public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {
		String name = event.getOption("name").getAsString();

		final String temp = name;

		EmbedBuilder loading = new EmbedBuilder().setTitle("Loading Response").setColor(Color.YELLOW).setFooter("Loading response, please wait");

		final String finalName = name; // Copy name String to effectively final String
		event.replyEmbeds(loading.build()).queue();

		EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Who is " + temp).setColor(Color.BLUE);

			/*
			java.util.List<BungieUser> list = CachedValue.getClan().searchMembers(temp);

			for (BungieUser bungieUser : list) {
				embedBuilder.addField(bungieUser.getDisplayName() + " - " + bungieUser.getBungieMembershipID(),
									  "Last on " + bungieUser.getDaysSinceLastPlayed() + " days ago", false);
			}

			if (list.isEmpty()) {
				embedBuilder.setTitle("No Users Found");
				embedBuilder.setDescription("No users with that string in their name, refine your search and try again.");
			}

			message.editMessage(embedBuilder.build()).queue();
			 */

		java.util.List<ClanMember> clanMemberList = new LinkedList<>();

		for (Document document : userManagement.getAllDocuments()) {
			ClanMember clanMember = new ClanMember(document);

			if (clanMember.getDestinyName().equalsIgnoreCase(finalName)) {
				clanMemberList.add(clanMember);
			}
		}

		for (ClanMember clanMember : clanMemberList) {
			BungieUser bungieUser = new BungieUser(clanMember.getBungieID(), DestinyPlatform.STEAM);
			User discordUser = BenedictMain.getJDA().getUserById(clanMember.getDiscordID());
			Date lastPlayed = bungieUser.getLastPlayed();

			embedBuilder.setThumbnail(discordUser.getEffectiveAvatarUrl());

			embedBuilder.addField("Discord Tag: " + discordUser.getName() + "#" + discordUser.getDiscriminator()
										  + "\nDiscord ID: " + discordUser.getId(),
								  "Destiny Name: " + bungieUser.getSupplementalDisplayName()
										  + "\nBungie ID: " + bungieUser.getID()
										  + "\nSteam ID: " + DestinyAPI.getUserCredential(UserCredentialType.STEAM_ID, bungieUser).getCredentialAsString()
										  + "\nDate Last Played: " + CachedValue.getFancySDF().format(lastPlayed)
										  + "\nDate Joined: " + CachedValue.getFancySDF().format(new Date(clanMember.getJoinDate()))
										  + "\nDate Left: " + (clanMember.getLeaveDate() == 0 ? "N/A" : CachedValue.getFancySDF().format(new Date(clanMember.getLeaveDate()))), false);
		}

		if (clanMemberList.isEmpty()) {
			EmbedBuilder loading2 = new EmbedBuilder().setTitle("Loading");
			loading2.setColor(Color.YELLOW).setFooter("No clan members with that name found.\nThis is going to take a little longer.");

			event.getHook().editOriginalEmbeds(loading2.build()).queue();

			java.util.List<BungieUser> searchedUsers = DestinyAPI.searchUsers(finalName);

			for (BungieUser bungieUser : searchedUsers) {
				embedBuilder.addField("Not A Clan Member",
									  "Destiny PC Name: " + bungieUser.getDisplayName()
											  + "\nBungie ID: " + bungieUser.getID()
											  + "\nSteam ID: " + DestinyAPI.getUserCredential(UserCredentialType.STEAM_ID, bungieUser).getCredentialAsString()
											  + "\nDate Last Played: " + CachedValue.getFancySDF().format(bungieUser.getLastPlayed()), false);
			}

			if (searchedUsers.isEmpty()) {
				embedBuilder.setTitle("No Users Found");
				embedBuilder.setColor(Color.RED).setFooter("No users with that Destiny name were found");
			}
		}

		event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
	}
}
