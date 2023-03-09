package net.dec4234.listeners;

import net.dec4234.framework.listeners.EventHandler;
import net.dec4234.framework.listeners.Listener;
import net.dec4234.framework.misc.CachedValue;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class UserVerifyEvent implements Listener {

	@EventHandler
	public void onVerify(MessageReactionAddEvent event) {
		Member member = event.getMember();

		if(event.getMessageId().equals("743840336148824064")) {
			CachedValue.getGuild().addRoleToMember(member.getId(), CachedValue.getVerifiedRole()).queue();
		}
	}
}
