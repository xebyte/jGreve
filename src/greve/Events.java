package greve;

import java.util.ArrayList;

import greve.struct.*;

public class Events {
	private ArrayList<Event> events;
	private String[] chromosomes;
	private String[] colour;

	/**
	 * A list of events
	 */
	public Events() {
		events = new ArrayList<Event>();
	}

	/**
	 * Add an event if it already doesn't exists
	 * @param event Event to add
	 */
	public void add(Event event) {
		for(int i = 0; i < events.size(); i++) {
			if(events.get(i).getType().equals(event.getType())) {
				return;
			}
		}

		events.add(event);
	}

	/**
	 * Get an event of type type
	 * @param  type Type of the event to extract
	 * @return      Event of type type
	 */
	public Event get(String type) {
		for(int i = 0; i < events.size(); i++) {
			if(events.get(i).getType().equals(type)) {
				return events.get(i);
			}
		}

		return null;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public String toString() {
		return events.toString();
	}
}