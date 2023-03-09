package net.dec4234.commands.application;

import net.dec4234.database.collections.UserDatabase;
import net.dec4234.database.collections.UserManagement;
import net.dec4234.framework.chainCommands.ChainCommand;
import net.dec4234.framework.commands.BenedictCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dec4234.javadestinyapi.material.DestinyAPI;
import net.dec4234.javadestinyapi.material.clan.Clan;
import net.dec4234.javadestinyapi.material.user.BungieUser;
import net.dec4234.src.BenedictMain;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static net.dec4234.framework.util.MessageUtil.spamLog;

public class ApplicationCommand extends ChainCommand {

	public ApplicationCommand() {
		super("apply");
	}

	private Guild guild;
	private UserManagement userManagement = new UserManagement();
	// String = Discord ID
	private HashMap<String, List<BungieUser>> userSelector = new HashMap<>(); // A directory of potential user selections for users

	private ApplicationManager appManager = ApplicationManager.getInstance();

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String message) {
		if(message.equalsIgnoreCase(BenedictCommand.getPrefix() + getStartCommand())) {

			// Pre-Application Checks
			if(!BenedictMain.getJDA().getGuildById(getGuild().getId()).isMember(user)) { // Is the user in the clan discord server?
				messageChannel.sendMessage("**You must be a member of our discord to apply** " + CachedValue.getDiscordInvite()).queue();
				spamLog(user.getName() + " was told they were not in the discord.");
				return;
			}

			EmbedBuilder eb = new EmbedBuilder().setTitle("Please respond with your Bungie Name and Identifier");
			eb.addField("For Example:", "dec4234#9909", false);
			eb.setColor(Color.BLUE);
			eb.setDescription("Contact <@252508637753376779> if you have any problems while applying");
			eb.setFooter("You can also respond using your Steam ID or Bungie ID instead of a username");
			sendMessage(user, eb);
			setStep(user, 2);
			spamLog(user.getName() + " started an application to join the clan");

			return;
		}

		if(message.equalsIgnoreCase(BenedictCommand.getPrefix() + "cancel")) {
			userSelector.remove(user.getId());
			appManager.getSelectedUser().remove(user.getId());
			cancel(user);
			spamLog(user.getName() + " cancelled their application");

			sendMessage(user, new EmbedBuilder().setTitle("Application Cancelled").setColor(Color.CYAN));

			return;
		}

		switch (getStep(user)) {
			case 2: // Receiving and interpreting the name of the user the applicant is looking for
				if(message.startsWith("4611")) { // If a user applies using a BungieID
					BungieUser user1;
					try {
						user1 = new BungieUser(message);

						spamLog(user.getName() + " - Applied using bungie ID - " + message);

						appManager.getSelectedUser().put(user.getId(), user1);
						setStep(user, 4);
						onCommand(user.openPrivateChannel().complete(), user, "");
					} catch (IndexOutOfBoundsException e) {
						EmbedBuilder eb = new EmbedBuilder().setTitle("No users with that ID found").setColor(Color.RED);
						eb.setDescription("Please review what you entered and try again.");
						sendMessage(user, eb);
					}

					return;
				}

				if(message.startsWith("765611")) { // If a user applies using a Steam ID
					BungieUser user1;
					try {
						user1 = DestinyAPI.getMemberFromSteamID(message);
						spamLog(user.getName() + " applied using Steam ID " + message);

						appManager.getSelectedUser().put(user.getId(), user1);
						setStep(user, 4);
						onCommand(user.openPrivateChannel().complete(), user, "");
					} catch (NullPointerException e) {
						EmbedBuilder eb = new EmbedBuilder().setTitle("No users with that ID found").setColor(Color.RED);
						eb.setDescription("Please review what you entered and try again.");
						sendMessage(user, eb);
					}

					return;
				}

				BungieUser user1 = DestinyAPI.getUserWithName(message);
				if(user1 == null) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.RED);
					eb.setTitle("No users with that name found");
					eb.setDescription("Please review what you entered and try again.");
					sendMessage(user, eb);

					spamLog(user.getName() + " - No users with name found: " + message);
					return;
				}
				// if(users.size() == 1) { // If there is only one possible user, move them to step 4
					appManager.getSelectedUser().put(user.getId(), user1);

					setStep(user, 4);
					onCommand(user.openPrivateChannel().complete(), user, ""); // Trigger it again to start action for step 4 without need for user to send message
				/* } else {
					EmbedBuilder eb = new EmbedBuilder();
					int i = 1;
					for (BungieUser bungieUser : users) {
						eb.addField(i + " " + bungieUser.getDisplayName(),
									bungieUser.getDaysSinceLastPlayed() + " days since last login", false);
						i++;
					}
					userSelector.put(user.getId(), users);

					sendMessage(user, eb);
					incrementProgess(user);
				}
				*/

				break;

			case 3:
				int number;
				try {
					number = Integer.parseInt(message) - 1;
					List<BungieUser> list = userSelector.get(user.getId());
					appManager.getSelectedUser().put(user.getId(), list.get(number));
					incrementProgess(user);
					onCommand(user.openPrivateChannel().complete(), user, "");
				} catch (NumberFormatException exception) {
					EmbedBuilder numberError = new EmbedBuilder();
					numberError.setTitle(message + " is not a number").setColor(Color.RED).setDescription("Please enter a valid whole number representing a person on the list");
					sendMessage(user, numberError);
					return;
				}
				break;
			case 4: // Now that we know the user, check requirements and return a fail/success message
				BungieUser bungieUser = appManager.getSelectedUser().get(user.getId());
				if(CachedValue.getClan().isMember(bungieUser)) {
					spamLog(user.getName() + " is already a member of the clan on account - " + bungieUser.getGlobalDisplayName() + "#" + bungieUser.getDiscriminator() + " " + bungieUser.getID());

					EmbedBuilder alreadyMember = new EmbedBuilder()
							.setTitle("Already a Member")
							.setColor(Color.RED)
							.setDescription("It appears that this user is already a member of the clan, contact <@252508637753376779> if you believe this is a mistake.");
					sendMessage(user, alreadyMember);
					cancel(user);
					return;
				}
				boolean metRequirements = true;
				EmbedBuilder eb = new EmbedBuilder().setTitle("Requirements - " + bungieUser.getGlobalDisplayName());

				if(bungieUser.getTimePlayed() / 60 >= 75) { // IF the user has played greater than equal to 75 hours
					eb.addField("✅ 75 hours of Destiny 2 play time", "", false);
				} else {
					spamLog(user.getName() + " - Does not have required time played - " + (bungieUser.getTimePlayed() / 60.0));
					eb.addField("❌ 75 hours of Destiny 2 play time", "Only " + bungieUser.getTimePlayed() / 60.0 + " of 75 hours played", false);
					metRequirements = false;
				}

				if((int) bungieUser.getDaysSinceLastPlayed() < 7) { // IF it has been less than 7 days since they have logged in
					eb.addField("✅ Has logged in the last 7 days", "", false);
				} else {
					spamLog(user.getName() + " - Hasn't played in the last 7 days - " + (bungieUser.getDaysSinceLastPlayed()));
					eb.addField("❌ Has logged in the last 7 days", "Last logged in " + bungieUser.getDaysSinceLastPlayed() + " days ago", false);
					metRequirements = false;
				}

				if(!metRequirements) {
					eb.setDescription("Please meet the following requirements before trying again.");
					eb.setColor(Color.RED);

					eb.addField("", "If you believe some of this is in error, please contact <@252508637753376779>", false);
					cancel(user); // End the process here because they cannot continue
				} else {
					eb.setColor(Color.GREEN);
					eb.addField("", "Respond with **YES** if you would like to join the clan", false);
					incrementProgess(user);
				}
				sendMessage(user, eb);


				break;
			case 5:
				if(message.equalsIgnoreCase("YES")) {
					EmbedBuilder requestToJoin = new EmbedBuilder().setTitle("Request to join the clan.")
							.addField("Make a request to join on our bungie page.", "https://www.bungie.net/en/ClanV2?groupid=3074427", false)
							.addField("", "Respond with **done** when you have done so", false)
							.setColor(Color.MAGENTA);
					sendMessage(user, requestToJoin);
					spamLog(user.getName() + " - Has been told to send a request to join");
					incrementProgess(user);
				}
				break;
			case 6:
				if(message.equalsIgnoreCase("done")) {
					Clan clan = CachedValue.getClan();
					BungieUser bungieUser1 = appManager.getSelectedUser().get(user.getId());
					for(BungieUser bungieUser2 : clan.getClanManagement().getPendingMembers()) {
						if(bungieUser1.getID().equals(bungieUser2.getID())) { // The user has a pending request, accept them
							clan.getClanManagement().approvePendingMember(bungieUser1);
							done(user, bungieUser1);
							spamLog(user.getName() + " - Accepted");
							return;
						}
					}
					EmbedBuilder embed = new EmbedBuilder().setTitle("No Pending Request Found").setColor(Color.YELLOW);
					embed.setDescription("Please make a request to join the clan and reply done when you have done so. ");
					embed.addField("https://www.bungie.net/en/ClanV2?groupid=3074427", "\nIf you believe something is wrong contact <@252508637753376779>", false);
					sendMessage(user, embed);

				}
				break;
		}
	}

	private void done(User user, BungieUser bungieUser) {
		EmbedBuilder embed = new EmbedBuilder().setTitle("Welcome to the clan!").setColor(Color.GREEN);
		embed.addField("Please be aware of the following", "- If you leave the discord, you will be kicked from the clan " +
				"\n - If you leave the clan, you will be demoted to LFG on discord " +
				"\n - You could be kicked if you do not play at least 10 hours every 30 days \n " +
				"Thanks for joining and enjoy your stay!", false);
		sendMessage(user, embed);

		// Manage User Roles
		getGuild().addRoleToMember(user.getId(), CachedValue.getMemberRole()).queue();
		getGuild().removeRoleFromMember(user.getId(), CachedValue.getLFGRole()).queue();

		// Change discord nickname to destiny display name
		getGuild().modifyNickname(getGuild().getMember(user), bungieUser.getGlobalDisplayName()).queue();

		SimpleDateFormat sdf = CachedValue.getSDF(); // Notify staff in the #log2 channel that someone has joined the clan
		EmbedBuilder notifyStaff = new EmbedBuilder()
				.setTitle("User Accepted")
				.setColor(Color.GREEN)
				.setThumbnail(user.getEffectiveAvatarUrl())
				.addField("Joined " + sdf.format(new Date()), "<@" + user.getId() + "> \n" + user.getName() + "\n" + bungieUser.getID(), false);
		CachedValue.getLog2Channel().sendMessage(notifyStaff.build()).queue();

		new UserDatabase().insertData(user, bungieUser, true);
		System.out.println(user.getName() + " is now a member of the clan - " + sdf.format(new Date()));
		appManager.getSelectedUser().remove(user.getId());
		userSelector.remove(user.getId());
	}

	public Guild getGuild() {
		if(guild == null) {
			guild = CachedValue.getGuild();
		}

		return guild;
	}
}
