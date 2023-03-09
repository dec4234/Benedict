package net.dec4234.framework.util;

import net.dec4234.framework.misc.CachedValue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ContextException;

public class MessageUtil {

	public static void sendPrivateMessage(User user, String message) {
		user.openPrivateChannel().queue(privateChannel -> {
			privateChannel.sendMessage(message).queue();
		});
	}

	public static void sendPrivateMessage(User user, EmbedBuilder embedBuilder) {

		user.openPrivateChannel().submit()
			.thenCompose(privateChannel -> privateChannel.sendMessage(embedBuilder.build()).submit())
			.whenComplete((message, throwable) -> {
				if(throwable != null) {
					CachedValue.getAllChatChannel().sendMessage(user.getAsMention() + " I am not able to reach you through private messaging, please ensure " +
																		"that you have enabled direct messages from co-server members. If this issue persists, please contact <@252508637753376779>").queue();
				}
			});

	}

	public static void spamLog(String message) {
		CachedValue.getSpamLogChannel().sendMessage(message).queue();
	}
}
