package greve;

import java.util.ArrayList;

import greve.struct.*;

public class Events {
	private ArrayList<Event> events;
	private String[] chromosomes;
	private String[] colour;

	/**
	 * A list of events
	 * @param  colour [description]
	 * @return        [description]
	 */
	public Events(String[] colour) {
		// chromosomes = new String[]{'chr1','chr2','chr3','chr4','chr5','chr6', 'chr7', 'chr8', 'chr9', 'chr10', 'chr11', 'chr12', 'chr13', 'chr14', 'chr15', 'chr16', 'chr17', 'chr18', 'chr19', 'chr20', 'chr21', 'chr22', 'chrX', 'chrY'};
		this.colour = colour;
		events = new ArrayList<Event>();
		// this.list = new Map();
		// this.over = new Map();
		// this.iover = new Map();
		// this.props = new Map();

		// for (String c : chromosomes) {
		//     this.list.add(c, new LinkedList<>());
		//     this.over.add(c, new LinkedList<>());
		//     this.iover.add(c, new LinkedList<>().add(new LinkedList<>()));
		//     this.props.add(c, new Object());
		// }

		// this.colour = colour;
		// // this.ncolor = self.ncolor=tuple([1.0-x for x in self.color])
		// this.width = 0.1;
		// this.skip = 0.02;
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