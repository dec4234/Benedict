package net.dec4234.commands.stats;

import net.dec4234.database.collections.UserDatabase;
import net.dec4234.database.collections.UserManagement;
import net.dec4234.framework.commands.SlashCommand;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dec4234.javadestinyapi.stats.activities.ActivityHistoryReview;
import net.dec4234.javadestinyapi.stats.activities.ActivityIdentifier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

public class RaidHistory extends SlashCommand {

	private UserManagement userManagement = new UserManagement();
	private ActivityHistoryReview activityHistoryReview = new ActivityHistoryReview();

	public RaidHistory() {
		super("raidhistory", "Retrieve a user's raid history of a specific raid",
			  new OptionData(OptionType.STRING, "raid", "The 3 letter identifier of the raid").setRequired(true),
			  new OptionData(OptionType.STRING, "username", "The username of the specific Destiny player (Optional)"));
	}

	@Override
	public void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member) {
		String raid = event.getOption("raid").getAsString();
		String username = null;

		try {
			username = event.getOption("username").getAsString();
		} catch (NullPointerException e) {

		}

		ActivityIdentifier identifier = ActivityIdentifier.fromShorthand(raid);

		EmbedBuilder loading = new EmbedBuilder().setTitle("Loading").setColor(Color.YELLOW);

		if(identifier != null) {
			loading.setThumbnail(identifier.getPgcrImage());
			event.replyEmbeds(loading.build()).queue();

			EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Raid History").setThumbnail(identifier.getPgcrImage()).setColor(Color.ORANGE);

			if(username != null) {
				for (BungieUser bungieUser : new UserDatabase().getClanUsers(username)) {
					embedBuilder.addField(bungieUser.getDisplayName(), activityHistoryReview.getCompletions(bungieUser, identifier) + " clears\n" +
							"https://raid.report/pc/" + bungieUser.getID() + "/", false);
				}
			} else {

			}

			event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
		} else {
			event.reply("I don't know what " + raid + " is.").queue();
		}
	}

	/*
	@Override
	public void onCommand(MessageChannel messageChannel, User user, String[] args) {
		if (args.length >= 1) {
			ActivityIdentifier activityIdentifier = ActivityIdentifier.fromShorthand(args[0]);
			if (activityIdentifier != null && activityIdentifier.getMode() == ActivityMode.RAID) {

				EmbedBuilder loading = new EmbedBuilder().setTitle("Loading").setColor(Color.YELLOW);
				loading.setThumbnail(activityIdentifier.getPgcrImage());
				messageChannel.sendMessage(loading.build()).queue(message -> {
					if (args.length == 1) {
						Member member = CachedValue.getGuild().getMember(user);

						if (member != null && member.getVoiceState().inVoiceChannel()) {
							VoiceChannel voiceChannel = member.getVoiceState().getChannel();
							EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Raid History").setColor(Color.GREEN).setThumbnail(activityIdentifier.getPgcrImage());
							if (voiceChannel != null) {
								for (Member member1 : voiceChannel.getMembers()) {
									Document document = userManagement.getUserDocumentDiscord(member.getId());
									if (document != null) {
										BungieUser bungieUser = new BungieUser(new ClanMember(document).getBungieID());
										ActivityHistoryReview activityHistoryReview = new ActivityHistoryReview();
										embedBuilder.addField(member1.getEffectiveName(), activityHistoryReview.getCompletions(bungieUser, activityIdentifier) + " clears\n" +
												"https://raid.report/pc/" + bungieUser.getBungieMembershipID() + "/", false);
									} else {
										embedBuilder.addField(member1.getEffectiveName(), "No data", false);
									}
								}
								embedBuilder.setDescription("Success! Here's what I found for you.");
							} else {
								embedBuilder.setTitle("Error").setColor(Color.RED).setDescription("You are not in a voice channel!");
							}
							message.editMessage(embedBuilder.build()).queue();

						}
					} else {
						String username = args[1];

						EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Raid History").setColor(Color.GREEN).setThumbnail(activityIdentifier.getPgcrImage());
						for (BungieUser bungieUser : CachedValue.getClan().searchMembers(username)) {
							ActivityHistoryReview activityHistoryReview = new ActivityHistoryReview();
							embedBuilder.addField(bungieUser.getDisplayName(), activityHistoryReview.getCompletions(bungieUser, activityIdentifier) + " clears\n" +
									"https://raid.report/pc/" + bungieUser.getBungieMembershipID() + "/", false);
						}
						message.editMessage(embedBuilder.build()).queue();

					}
				});
			} else {
				messageChannel.sendMessage("**Raid shorthand identifier is not valid!**").queue();
			}
		}
	}
	 */
}
