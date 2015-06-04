package greve.draw;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import greve.Events;
import greve.struct.*;
import greve.struct.Event;

public class IndividualDiagram extends Canvas {
	private String title;

	public IndividualDiagram(String title, String subtitle, int width, int height) {
		super(width, 5000);

        if(title != null) {
            plotTitle(title);
        }

		this.title = "Chromosome " + subtitle.substring(3);
		super.drawSubtitleCenter(this.title);
		super.newSection();

		
	}

	public void plotAberration(ArrayList<Patient> patients, int maxi37, Color c) {
		HashMap<String, ArrayList<Patient>> patientsMap = new HashMap<String, ArrayList<Patient>>();
		for(Patient p : patients) {
			ArrayList<Patient> array = patientsMap.get(p.getPatientID());
			if(array == null) {
				array = new ArrayList<Patient>();
				array.add(p);
				patientsMap.put(p.getPatientID(), array);
			} else {
				patientsMap.get(p.getPatientID()).add(p);
			}
		}

		for (Map.Entry<String, ArrayList<Patient>> entry : patientsMap.entrySet()) {
		    String key = entry.getKey();
		    ArrayList<Patient> value = entry.getValue();
		    
		    super.drawSample(value, maxi37, c);
		}
	}

	public void plotOverlap(ArrayList<Overlap> overlaps, int maxi37, int top, Color c) {
		for(Overlap o : overlaps) {
			super.drawOverlap(o, maxi37, top, c);

		}

        super.drawOverlapTitle(overlaps.get(0).getType());
		super.newSection();
	}

	public void plotScores(ArrayList<Overlap> overlaps, int maxi37, int patients, boolean showScore, boolean showGWScore, boolean showCScore) {
        super.newSection();
        super.newSection();
		super.drawOverlapScore(overlaps, maxi37, patients, showScore, showGWScore, showCScore);

        super.newSection();
	}

	public void drawLabel(int scaleMax, String label) {
		super.newSection();
		super.drawLabel(0, scaleMax, label);
	}

    public void plotDGV(ArrayList<Gene> value, Integer integer) {
        super.newSection();
        super.drawSubtitle("DGV");
        curY += padding;

        super.drawDGV(value, integer);

        super.newSection();
    }

    public void plotGenes(ArrayList<Gene> value, Integer integer) {
        super.newSection();
        super.drawGenes(value, integer);

        super.newSection();
        super.newSection();
    }

    public void plotLeg(String label, Integer integer, int i, int size, Color c) {
        super.drawLeg(label, integer, i, size, c);

        super.newSection();
    }

    public void plotCyto(ArrayList<Cytoband> cytobands, Integer maxi37) {
        super.drawCyto(cytobands, maxi37);

        super.newSection();
    }

    public void drawLegend(String[] strings, Color[] colors) {

        super.newSection();

        super.drawLegend(strings, colors);

        super.newSection();
    }

    public void plotIndividualSample(String p, Events patientEvents, String chr, Integer maxi37) {
        super.drawIndividualSample(p, patientEvents, chr, maxi37);
        super.newSection();
    }

    public void plotTitle(String title) {
        super.drawTitle(title);
        super.newSection();
    }
}