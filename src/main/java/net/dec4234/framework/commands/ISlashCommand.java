package net.dec4234.framework.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface ISlashCommand {

	void onCommand(SlashCommandEvent event, MessageChannel messageChannel, Member member);
}
