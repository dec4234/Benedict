package net.dec4234.framework.chainCommands;

import lombok.Getter;
import net.dec4234.framework.commands.BenedictCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public abstract class ChainCommand implements IChainCommand {

	@Getter private static HashMap<String, ChainCommand> chainCommandSet = new HashMap<>(); // Mapped command to their ChainCommand

	@Getter private static HashMap<String, ChainCommand> chainCommandLog = new HashMap<>(); // Mapped users to their active chain commands
	@Getter protected HashMap<String, Integer> chainCommandProgress = new HashMap<>();

	@Getter private String startCommand;

	public ChainCommand(String startCommand) {
		this.startCommand = startCommand;

		chainCommandSet.put(BenedictCommand.getPrefix() + startCommand, this);
	}

	public void incrementProgess(User user) {
		if(chainCommandProgress.containsKey(user.getId())) {
			int step = chainCommandProgress.get(user.getId());
			chainCommandProgress.remove(user.getId());
			chainCommandProgress.put(user.getId(), step + 1); // Increment recorded progress of user by 1
		} else {
			chainCommandProgress.put(user.getId(), 1);
		}
	}

	public int getStep(User user) {
		if(chainCommandProgress.containsKey(user.getId())) {
			return chainCommandProgress.get(user.getId());
		} else {
			chainCommandProgress.put(user.getId(), 1);
			return 1;
		}
	}

	public void setStep(User user, int step) {
		chainCommandProgress.remove(user.getId());
		chainCommandProgress.put(user.getId(), step);
	}

	public void cancel(User user) {
		if(chainCommandLog.containsKey(user.getId())) {
			chainCommandLog.remove(user.getId());
			chainCommandProgress.remove(user.getId());
		}
	}

	public void sendMessage(User user, EmbedBuilder embedBuilder) {
		if(user.hasPrivateChannel()) {
			user.openPrivateChannel().queue(privateChannel -> {
				privateChannel.sendMessage(embedBuilder.build()).queue();
			});
		} else {
			System.out.println(user.getName() + "#" + user.getDiscriminator() + " does not have a private channel for me to use.");
		}
	}
}
