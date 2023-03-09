package net.dec4234.framework.listeners;

import lombok.Getter;
import net.dv8tion.jda.api.events.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EventManager {

	@Getter
	private static List<Listener> listeners = new ArrayList<>();

	// TO-DO: Combine both register methods
	public static void register(Listener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public static void register(Listener... listener) {
		for(Listener listener1 : listener) {
			register(listener1);
		}
	}

	public static void unregister(Listener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	public static void callEvent(Event event) {
		CompletableFuture.runAsync(() -> {
			call(event);
		});
	}

	private static void call(Event event) {
		for (Listener listener : getListeners()) {
			Method[] methods = listener.getClass().getMethods();

			for (Method method : methods) {
				EventHandler eventHandler = method.getAnnotation(EventHandler.class);

				if (eventHandler != null) {
					Class<?>[] methodParameters = method.getParameterTypes();

					if (methodParameters.length < 1) {
						continue;
					}

					if (!event.getClass().getSimpleName().equals(methodParameters[0].getSimpleName())) {
						continue;
					}

					try {
						method.invoke(listener.getClass().newInstance(), event);
					} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
