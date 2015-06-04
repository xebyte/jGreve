package greve.draw;

import greve.Events;
import greve.struct.Event;
import greve.struct.Cytoband;
import greve.struct.Patient;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xebyte on 13/03/15.
 */
public class SummaryDiagram extends Canvas {
    int chrRow = 6;
    int cytos = 0;

    int highestY = 0;

    public SummaryDiagram(String title, int width) {
        super(width, 3000);

        if(title != null) {
            plotTitle(title);
        }
    }


    public void plotCyto(String chr, ArrayList<Cytoband> cytobands, Integer maxi37) {
        super.drawSubtitle(chr);

//        int y = curY;
//        curY += sectionPadding;
        super.drawCytoV(cytobands, maxi37);

        cytos++;
        highestY = Math.max(highestY, cytobands.get(cytobands.size() - 1).getChromEnd());

//        curY = y;
//        super.curX = curX + width/6;
    }

    public void plotAberrations(String chr, Integer maxi37, Events events, HashMap<String, ArrayList<Patient>> sortedPatients) {

        super.drawAberrationsV(chr, maxi37, events, sortedPatients);
        super.curX = (width/6) * cytos;
        if(super.curX == 0) super.curX = marginLeft;

        if(cytos >= chrRow) {
            curY += ((highestY * 2) / (double) maxi37) * (maxi37 / 1000000) + sectionPadding*3;
            curX = marginLeft;
            cytos = 0;
            highestY = 0;
        }
    }

    public void plotAberrationIndividual(String chr, Integer maxi37, Events events, ArrayList<Patient> patients, int chromEnd) {

        HashMap<String, ArrayList<Sample>> samples = new HashMap<String, ArrayList<Sample>>();

        for(Patient p : patients) {
            ArrayList<Sample> sample = new ArrayList<Sample>();
            samples.put(p.getPatientID(), sample);
        }

        for(int i = 0; i < events.getEvents().size(); i++) {
            Event e = events.getEvents().get(i);
            ArrayList<Patient> ePatients = e.getList(chr);

            for(Patient p : ePatients) {
                if(samples.get(p.getPatientID()) == null) {
                    ArrayList<Sample> sample = new ArrayList<Sample>();
                    sample.add(new Sample(Canvas.ABERRATION_COLOURS[i], p.getStart(), p.getEnd()));
                    samples.put(p.getPatientID(), sample);
                }
                else {
                    ArrayList<Sample> sample = samples.get(p.getPatientID());
                    sample.add(new Sample(Canvas.ABERRATION_COLOURS[i], p.getStart(), p.getEnd()));
                }
            }
        }

        super.drawIndividualAberrationsV(chr, maxi37, chromEnd, samples);

        super.curX = (width/6) * cytos;
        if(super.curX == 0) super.curX = marginLeft;

        if(cytos >= chrRow) {
            curY += ((highestY * 2) / (double) maxi37) * (maxi37 / 1000000) + sectionPadding*3;
            curX = marginLeft;
            cytos = 0;
            highestY = 0;
        }
    }

    public void plotTitle(String s) {
        super.drawTitle(s);
        super.newSection();
    }

    public void plotLegend(Events events) {
        super.drawSummaryLegend(events);
        super.newSection();
    }
}

class Sample {
    Color color;
    int start;
    int end;

    public Sample(Color color, int start, int end) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}