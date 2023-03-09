package net.dec4234.framework.listeners;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

public class EventListener implements net.dv8tion.jda.api.hooks.EventListener {

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		EventManager.callEvent((Event) event);
	}
}
