package net.dec4234.framework.commands;

import net.dec4234.framework.chainCommands.ChainCommand;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class BenedictCommandListener implements EventListener {

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof PrivateMessageReceivedEvent) {
			PrivateMessageReceivedEvent e = (PrivateMessageReceivedEvent) event;

			assess(e.getChannel(), e.getMessage().getContentRaw(), e.getAuthor());
		} else if (event instanceof GuildMessageReceivedEvent) {
			GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

			assess(e.getChannel(), e.getMessage().getContentRaw(), e.getAuthor());
		} else if (event instanceof SlashCommandEvent) {
			SlashCommandEvent e = (SlashCommandEvent) event;

			if(SlashCommand.getSlashCommandMap().containsKey(e.getName())) {
				SlashCommand.getSlashCommandMap().get(e.getName()).onCommand(e, e.getChannel(), e.getMember());
			}
		}
	}

	private void assess(MessageChannel messageChannel, String message, User user) {
		for(String userID : ChainCommand.getChainCommandLog().keySet()) { // If a user is already in the middle of a chain command, call chain command function
			if(userID.equals(user.getId())) {
				ChainCommand.getChainCommandLog().get(userID).onCommand(messageChannel, user, message);
				return;
			}
		}  // Some responses to chain commands may not start as a command, so call before checking for a command

		if(!message.startsWith(BenedictCommand.getPrefix())) return; // IF the message does not start as a command, ignore it

		String cmd = message.split(" ")[0];

		for(String key : ChainCommand.getChainCommandSet().keySet()) { // IF a user passes a chain command starter, call the onCommand function
			if(key.equals(cmd)) {
				ChainCommand.getChainCommandLog().put(user.getId(), ChainCommand.getChainCommandSet().get(key));
				ChainCommand.getChainCommandSet().get(key).onCommand(messageChannel, user, message);
				return;
			}
		}

		for(String command : BenedictCommand.getCommandMap().keySet()) { // Search regular command set to check if they called any commands
			if(command.equals(cmd)) { // Retrieve the command of the message
				BenedictCommand.getCommandMap().get(command).onCommand(messageChannel, user, message.replace(command + " ", "").split(" ").clone());
				return;
			}
		}
	}
}
