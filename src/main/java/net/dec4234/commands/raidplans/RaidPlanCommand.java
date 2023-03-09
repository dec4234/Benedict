package net.dec4234.commands.raidplans;

import lombok.Getter;
import net.dec4234.framework.chainCommands.ChainCommand;
import net.dec4234.framework.commands.BenedictCommand;
import net.dec4234.framework.misc.CachedValue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;

public class RaidPlanCommand extends ChainCommand {

	private HashMap<String, Date> mappedTimes = new HashMap<>(); // UserID, Date
	private HashMap<String, Integer> selectedRaid = new HashMap<>(); // UserID, RaidID
	private HashMap<String, String> descriptions = new HashMap<>(); // UserID, Raid Description

	public RaidPlanCommand() {
		super("planraid");
	}

	@Override
	public void onCommand(MessageChannel messageChannel, User user, String message) {
		Member member = CachedValue.getGuild().getMember(user);
		if(member == null || !member.getRoles().contains(CachedValue.getElderRole())) {
			return;
		}

		if(message.equals(BenedictCommand.getPrefix() + getStartCommand())) {
			setStep(user, 1);

			EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Plan A Raid : Step 1").setColor(Color.BLUE);
			embedBuilder.addField("Plan Raid Time", "The Epoch time is the number of seconds/milliseconds since January 1, 1970\n" +
					"Find the Epoch time for the time you want to schedule the raid for here: \nhttps://www.epochconverter.com/\nRespond with the time.", false);

			sendMessage(user, embedBuilder);
			return;
		}

		switch (getStep(user)) {
			case 1:
				long parseDate;
				try {
					parseDate = Long.parseLong(message);
					if (message.length() < 13) {
						parseDate *= 1000;
					}

					if(parseDate + (60 * 60 * 1000) < System.currentTimeMillis()) { // If someone is dumb enough to schedule a raid for a time that has already occured
						messageChannel.sendMessage("The time of the raid must be at least one hour ahead of the time it is scheduled").queue();
					}
					mappedTimes.put(user.getId(), new Date(parseDate));

					EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.BLUE).setTitle("Plan A Raid : Step 2");
					embedBuilder.addField("Raid Scheduled", CachedValue.getFancySDF().format(new Date(parseDate)), false);
					embedBuilder.setDescription("1️⃣ : Last Wish" +
														"\n:two: : Garden Of Salvation" +
														"\n:three: : Deep Stone Crypt" +
														"\n:four: : Vault Of Glass" +
														"\n:five: : Vow Of The Disciple" +
											  "\n\nPlease respond with the number indicating which raid you would like to schedule");
					if (CachedValue.getDec4234ID().equals(user.getId())) { // Adds other options if the user planning the event is myself
						embedBuilder.setFooter("-1 : For A Clan Event");
					}
					sendMessage(user, embedBuilder);

					incrementProgess(user);
				} catch (NumberFormatException exception) {
					EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Error").setColor(Color.RED);
					embedBuilder.addField(message + " is not a valid number.", "Please review what you sent and try again", false);
					sendMessage(user, embedBuilder);
				}
				break;
			case 2:
				int parse;
				try {
					parse = Integer.parseInt(message);
					selectedRaid.put(user.getId(), parse);
				} catch (NumberFormatException e) {
					EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Error").setColor(Color.RED);
					embedBuilder.addField(message + " is not a valid number.", "Please review what you sent and try again", false);
					sendMessage(user, embedBuilder);
					return;
				}

				Raid raid = Raid.getRaidFromID(parse);

				if(raid == null) {
					return;
				}

				Date date = mappedTimes.get(user.getId());

				EmbedBuilder embedBuilder = new EmbedBuilder();

				String title = (raid.getId() == -1 ? "Event Scheduled " : "Raid Scheduled ") + raid.getName();
				embedBuilder.addField(title, CachedValue.getFancySDF().format(date), false);
				embedBuilder.setThumbnail(raid.getImageLink()).setColor(Color.BLUE);
				embedBuilder.setFooter("Please respond with a description of the raid");
				sendMessage(user, embedBuilder);

				incrementProgess(user);
				break;
			case 3:
				descriptions.put(user.getId(), message);

				EmbedBuilder embedBuilder1 = new EmbedBuilder().setTitle("Confirm Event");
				embedBuilder1.setThumbnail(getImageLink(selectedRaid.get(user.getId()))).setColor(Color.BLUE);
				embedBuilder1.addField(CachedValue.getFancySDF().format(mappedTimes.get(user.getId())), descriptions.get(user.getId()), false);
				embedBuilder1.setFooter("Reply with DONE if you are ready for this to be posted. Reply with !planraid to reset from the beginning.");
				sendMessage(user, embedBuilder1);

				incrementProgess(user);
				break;
			case 4:
				if(message.equalsIgnoreCase("DONE")) {
					Raid raid1 = Raid.getRaidFromID(selectedRaid.get(user.getId()));
					String title1 = raid1.getName() + (raid1.getId() == -1 ? " Clan Event" : " Raid Event");

					EmbedBuilder embedBuilder2 = new EmbedBuilder().setTitle(title1).setColor(Color.CYAN);
					embedBuilder2.setThumbnail(getImageLink(raid1.getId()));

					embedBuilder2.setFooter("You can confirm your attendance of this event by using the emoji checkmark below.\n" +
													"If there are more than 6 people who have confirmed than all users after that will be added to the substitutes list.");

					// embedBuilder2.setDescription(user.getAsMention() + " is hosting this event on\n" + CachedValue.getFancySDF().format(mappedTimes.get(user.getId())) + "\n\nNotes: " + descriptions.get(user.getId()));
					embedBuilder2.setDescription(user.getAsMention() + " is hosting this event on\n<t:" +
							(mappedTimes.get(user.getId()).getTime() / 1000) +
							":F>\n\nNotes: " + descriptions.get(user.getId()));

					CachedValue.getRaidChannel().sendMessage(embedBuilder2.build()).queue(message1 -> {
						message1.addReaction(CachedValue.getGreenCheckMark()).queue();
					});

					// Remove all traces of user activity
					cancel(user);
					removeFromAll(user);
				}
				break;
		}
	}

	private String getImageLink(int id) {
		Raid raid = Raid.getRaidFromID(id);
		if(raid != null) {
			return raid.getImageLink();
		}

		return null;
	}

	private void removeFromAll(User user) {
		mappedTimes.remove(user.getId());
		selectedRaid.remove(user.getId());
		descriptions.remove(user.getId());
	}

	enum Raid {

		LW(1,"Last Wish", "https://www.bungie.net/img/destiny_content/pgcr/raid_beanstalk.jpg"),
		GOS(2, "Garden Of Salvation", "https://www.bungie.net/img/destiny_content/pgcr/raid_garden_of_salvation.jpg"),
		DSC(3, "Deep Stone Crypt", "https://www.bungie.net/img/destiny_content/pgcr/europa-raid-deep-stone-crypt.jpg"),
		VOG(4, "Vault Of Glass", "https://www.bungie.net/pubassets/pkgs/150/150569/FrontpageBanner_1920x590.jpg?cv=3983621215&av=1926358162"),
		VOTD(5, "Vow Of The Disciple", "https://www.bungie.net/pubassets/pkgs/157/157111/FrontPageBanner_1920x590.jpg"),

		EVENT(-1, "Clan Event", "https://assets.gamepur.com/wp-content/uploads/2020/03/18000202/destiny-2-weekly-reset-details.jpg");

		@Getter private int id;
		@Getter private String name;
		@Getter private String imageLink;

		Raid(int id, String name, String imageLink) {
			this.id = id;
			this.name = name;
			this.imageLink = imageLink;
		}

		public static Raid getRaidFromID(int id) {
			for(Raid raid : Raid.values()) {
				if(raid.getId() == id) {
					return raid;
				}
			}

			return null;
		}
	}
}
