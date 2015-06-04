package greve.draw;

import java.util.ArrayList;

import greve.struct.*;

public class IndChromosome extends Canvas {
	private String title;

	public IndChromosome(String title, int scaleMax, int width, int height) {
		super(width, height);

		this.title = "Chromosome " + title.substring(3);
		super.drawTitle(title);
		super.newSection();

//		super.drawLabel(0, scaleMax, label);
	}

	public void drawSample(ArrayList<Patient> patients) {

		for(Patient sample : patients) {
			super.drawBar(sample.getPatientID());
		}
	}
}