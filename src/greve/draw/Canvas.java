package greve.draw;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;

import greve.Events;
import greve.draw.Diagram;
import greve.struct.*;
import greve.struct.Event;

public abstract class Canvas implements Diagram {
	protected int width;
	protected int height;

	protected int marginTop;
	protected int marginLeft;
    protected int marginRight;
	protected int padding;
	protected int sectionPadding;

	private int workingWidth;
	private int workingHeight;

	private BufferedImage image;
	private Graphics2D g2;

	protected int curX, curY;

	private Font titleFont = new Font("Helvetica", Font.BOLD, 22);
	private Font labelFont = new Font("Helvetica", Font.PLAIN, 11);

    private HashMap<String, Color> stainColours;
    public final static Color[] ABERRATION_COLOURS = new Color[]{
            new Color(0.0f,0.0f,1.0f),
            new Color(1.0f,0.0f,0.0f),
            new Color(0.0f,1.0f,0.0f),
            new Color(1.0f,0.7f,0.3f),
            new Color(1.0f,0.0f,1.0f),
            new Color(0.0f,1.0f,1.0f),
            new Color(0.5f,0.5f,1.0f),
            new Color(0.5f,1.0f,0.5f),
            new Color(1.0f,0.5f,0.5f),
            new Color(0.9f,0.8f,0.9f),
            new Color(0.3f,0.3f,0.6f),
            new Color(0.0f,0.0f,1.0f),
            new Color(1.0f,0.0f,0.0f),
            new Color(0.0f,1.0f,0.0f),
            new Color(1.0f,0.7f,0.3f),
            new Color(1.0f,0.0f,1.0f),
            new Color(0.0f,1.0f,1.0f),
            new Color(0.5f,0.5f,1.0f),
            new Color(0.5f,1.0f,0.5f),
            new Color(1.0f,0.5f,0.5f),
            new Color(0.9f,0.8f,0.9f),
            new Color(0.3f,0.3f,0.6f),
            new Color(0.0f,0.0f,1.0f),
            new Color(1.0f,0.0f,0.0f),
            new Color(0.0f,1.0f,0.0f),
            new Color(1.0f,0.7f,0.3f),
            new Color(1.0f,0.0f,1.0f),
            new Color(0.0f,1.0f,1.0f),
            new Color(0.5f,0.5f,1.0f),
            new Color(0.5f,1.0f,0.5f),
            new Color(1.0f,0.5f,0.5f),
            new Color(0.9f,0.8f,0.9f),
            new Color(0.3f,0.3f,0.6f),
            new Color(0.0f,0.0f,1.0f),
            new Color(1.0f,0.0f,0.0f),
            new Color(0.0f,1.0f,0.0f),
            new Color(1.0f,0.7f,0.3f),
            new Color(1.0f,0.0f,1.0f),
            new Color(0.0f,1.0f,1.0f),
            new Color(0.5f,0.5f,1.0f),
            new Color(0.5f,1.0f,0.5f),
            new Color(1.0f,0.5f,0.5f),
            new Color(0.9f,0.8f,0.9f),
            new Color(0.3f,0.3f,0.6f)
    };

	public Canvas(int w, int h) {
		width = w;
		height = h;

		marginTop = 50;
		marginLeft = 50;
        marginRight = 100;

		padding = 10;
		sectionPadding = 20;

		curX = marginLeft;
		curY = marginTop;

		workingWidth = width - (marginLeft + marginRight);
		workingHeight = height - (marginTop * 2);

		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, height);
		g2.setColor(Color.BLACK);

        stainColours = new HashMap<String, Color>();
        stainColours.put("acen", new Color(0.58f, 0.196f, 0.196f));
        stainColours.put("gneg", new Color(0.89f, 0.89f, 0.89f));
        stainColours.put("gpos100", new Color(0f,0f,0f));
        stainColours.put("gpos75", new Color(0.22f, 0.22f, 0.22f));
        stainColours.put("gpos50", new Color(0.3f, 0.3f, 0.3f));
        stainColours.put("gpos25", new Color(0.55f, 0.55f, 0.55f));
        stainColours.put("gvar", new Color(0, 0, 0));
        stainColours.put("stalk", new Color(0.5f, 0.5f, .05f));
	}

	public void drawTitle(String title) {
		g2.setFont(titleFont);
		int stringHeight = (int) g2.getFontMetrics().getStringBounds(title, g2).getHeight();
        g2.drawString(title, curX, curY + stringHeight);

        curY += stringHeight + padding;
        g2.setFont(labelFont);
	}

	public void drawBar(String sample) {
		g2.setFont(labelFont);
		g2.setColor(new Color(0.95f, 0.95f, 0.95f));
		g2.fillRect(curX, curY, width-(marginLeft+marginRight), 20);
		g2.setColor(Color.BLACK);

		int stringHeight = (int) g2.getFontMetrics().getStringBounds(sample, g2).getHeight();
		int stringLen = (int) g2.getFontMetrics().getStringBounds(sample, g2).getWidth();
        
        g2.drawString(sample, width-marginRight+5, curY + stringHeight);
        curY += stringHeight + padding;
        g2.setFont(labelFont);
	}

	public void drawSample(ArrayList<Patient> samples, int maxi37, Color c) {
		double scale = ((double) width - (double) (marginLeft + marginRight)) / ((double) maxi37 / 1000000.);

		int stringHeight = (int) g2.getFontMetrics().getStringBounds(samples.get(0).getPatientID(), g2).getHeight();
		int stringLen = (int) g2.getFontMetrics().getStringBounds(samples.get(0).getPatientID(), g2).getWidth();

		for(Patient p : samples) {

			double x;
			if(p.getStart() == 0) {
				x = 0;
			} else {
				x = p.getStart();
			}

			double xx;
			if(p.getEnd() == 0) {
				xx = maxi37;
			} else {
				xx = p.getEnd();
			}

			double start = ((x * scale) / (double) maxi37) * (maxi37 / 1000000);
			double end = ((xx * scale) / (double) maxi37) * (maxi37 / 1000000);

			double myY0 = (curY - 4.);
			double myY1 = (curY + 4.);

            g2.setColor(c);
			if(start == end) {
				g2.drawLine(curX + (int) + Math.round(start), (int) Math.round(myY0), curX + (int) + Math.round(start), stringHeight);
			}
			else {

				g2.fillRect(curX + ((int) Math.round(start)), ((int) Math.round(myY0)), ((int) Math.round(end-start)), stringHeight);
				g2.setColor(Color.BLACK);

                // TODO
//                if loh and lohname.lower() in record[2].lower():
//                myplot.linewidth(lohlw)
//                myplot.color(lohcol)
//                myplot.line(start, myY1, end, myY1)
//                myplot.linewidth()
			}
	    }

    	g2.setFont(labelFont);
        
        g2.drawString(samples.get(0).getPatientID(), width-marginRight+5, curY + stringHeight);
        curY += stringHeight + padding / 2;
        g2.setFont(labelFont);
	}

	public void drawOverlap(Overlap o, int maxi37, int top, Color c) {
		double scale = ((double) width - (double) (marginLeft + marginRight)) / ((double) maxi37 / 1000000.);
		int stringHeight = (int) g2.getFontMetrics().getStringBounds(o.getType(), g2).getHeight();

		double sx = o.getMin();

		double ex;
		if(o.getMax() == 0) {
			ex = maxi37;
		} else {
			ex = o.getMax();
		}

        float[] overlapColour = c.getRGBColorComponents(null);
        float[] ncolor = new float[]{1.0f - overlapColour[0], 1.0f - overlapColour[1], 1.0f - overlapColour[2]};

		Color col = new Color(0.98f - ncolor[0] * 1.0f / top * (float) o.getCount(), 0.98f - ncolor[1] * 1.0f / top * (float) o.getCount(), 0.98f - ncolor[2] * 1.0f / top * (float) o.getCount());
		
		double start = (sx * scale / (double) maxi37) * (maxi37 / 1000000);
		double end = (ex * scale / (double) maxi37) * (maxi37 / 1000000);

		g2.setColor(col);

		g2.fillRect(curX + ((int) Math.round(start)), (int) Math.round(curY), (int) Math.round(end-start), (int) Math.round(stringHeight));
	}

    public void drawOverlapTitle(String title) {

        int stringHeight = (int) g2.getFontMetrics().getStringBounds(title, g2).getHeight();
        g2.setFont(labelFont);
        g2.setColor(Color.BLACK);

        g2.drawString(title, width-marginRight+5, curY + stringHeight);
        // curY += stringHeight + padding / 2;
        g2.setFont(labelFont);
    }

	public void drawOverlapScore(ArrayList<Overlap> overlaps, int maxi37, int patients, boolean showScore, boolean showGWScore, boolean showCScore) {
		double scale = ((double) width - (double) (marginLeft + marginRight)) / ((double) maxi37 / 1000000.);
		double prev = 0;
		double prevGWP = 0;
		double prevCP = 0;

        double maxHeight = 0;

		for(Overlap o : overlaps) {

			double sx = o.getMin();

			double ex;
			if(o.getMax() == 0) {
				ex = maxi37;
			} else {
				ex = o.getMax();
			}

			double start = (sx * scale / maxi37) * (maxi37 / 1000000);
			double end = (ex * scale / maxi37) * (maxi37 / 1000000);

            double myY0 = o.getCount();

			if(showScore) {
                g2.setColor(new Color(0.5f, 0f, 0f));


                if(prev == 0)
                    prev = myY0;


                g2.drawLine((int) Math.round(marginLeft + start), (int) Math.round(curY - prev), (int) Math.round(marginLeft + start), (int) Math.round(curY - myY0));
                g2.drawLine((int) Math.round(marginLeft + start), (int) Math.round(curY - myY0), (int) Math.round(marginLeft + end), (int) Math.round(curY - myY0));

                prev = myY0;
            }


			if(showGWScore) {
                g2.setColor(new Color(0f, 0.5f, 0f));

                myY0 = Math.abs(Math.log(o.getPoiBin()));
                if(prevGWP == 0) {
                    prevGWP = myY0;
                }

                g2.drawLine((int) Math.round(marginLeft + start), (int) Math.round(curY - prevGWP), (int) Math.round(marginLeft + start), (int) Math.round(curY - myY0));
                g2.drawLine((int) Math.round(marginLeft + start), (int) Math.round(curY - myY0), (int) Math.round(marginLeft + end), (int) Math.round(curY - myY0));

                prevGWP = myY0;
            }

            if(showCScore) {
                g2.setColor(new Color(0f, 0f, 0.5f));

                myY0 = Math.abs(Math.log(o.getPoiBinC()));
                if(prevCP == 0) {
                    prevCP = myY0;
                }

                g2.drawLine((int) Math.round(marginLeft + start), (int) Math.round(curY - prevCP), (int) Math.round(marginLeft + start), (int) Math.round(curY - myY0));
                g2.drawLine((int) Math.round(marginLeft + start), (int) Math.round(curY - myY0), (int) Math.round(marginLeft + end), (int) Math.round(curY - myY0));

                prevCP = myY0;
            }

		}

		newSection();
	}

	public void drawLabel(int min, int max, String label) {
		int stops = 4;
		double distance = (double) (width-(marginLeft+marginRight))/(double)stops;

		double stop = (max - min) / 4.;

        g2.setColor(Color.black);

		g2.fillRect(curX, curY, width-(marginLeft+marginRight), 1);
		curY += 1;

		for(int i = 0; i <= stops; i++) {
			g2.drawLine(curX-3, curY, curX-3, curY + 10);

			String str = Math.round(min + (stop*i)) + "";
			int stringLen = (int) g2.getFontMetrics().getStringBounds(str, g2).getWidth();
			int stringHeight = (int) g2.getFontMetrics().getStringBounds(str, g2).getHeight();
			g2.drawString(str, curX - (stringLen/2), curY + 10 + stringHeight);
			curX += (int) Math.round(distance);

//			if(i == stops-1) curX -= 1;
		}

		curX = marginLeft;
		curY += 10 + padding;

        int stringLen = (int) g2.getFontMetrics().getStringBounds(label, g2).getWidth();
        int stringHeight = (int) g2.getFontMetrics().getStringBounds(label, g2).getHeight();
        g2.drawString(label, marginLeft + (width - (marginLeft+marginRight))/2 - (stringLen/2), curY + stringHeight);
	}

	public void drawLine(double x, double y, double x2, double y2) {

		g2.setColor(new Color(0, 0, 0));
		g2.drawLine((int) (curX + x), (int) (curY + y), (int) (curX + x2), (int) (curY + y2));
	}

	public void fillRect(double x, double y, double width, double height) {

		g2.setColor(new Color(0, 0, 0));
		g2.fillRect((int) Math.round(x),(int) Math.round(y), (int) Math.round(width), (int) Math.round(height));
	}

	public void draw() {
		

		g2.setColor(Color.BLACK);
		g2.drawLine(marginLeft, marginTop, marginLeft, height - marginTop);
		g2.drawLine(marginLeft, height - marginTop, width - marginLeft, height - marginTop);

		String xAxis = "X AXIS";
		String yAxis = "Y AXIS";

		int stringLen = (int) g2.getFontMetrics().getStringBounds(xAxis, g2).getWidth();
		int stringHeight = (int) g2.getFontMetrics().getStringBounds(xAxis, g2).getHeight();
        int start = width/2 - stringLen/2;
        g2.drawString(xAxis, start, height - marginTop + stringHeight);

        stringLen = (int) g2.getFontMetrics().getStringBounds(yAxis, g2).getWidth();
		stringHeight = (int) g2.getFontMetrics().getStringBounds(yAxis, g2).getHeight();
        start = height/2 - stringLen/2;

        // clockwise 90 degrees
	    AffineTransform at = new AffineTransform();
	    at.setToRotation(-Math.PI/2.0, marginLeft - stringHeight, start);
	    
	    Graphics2D copy = (Graphics2D) g2.create();
	    copy.setTransform(at);
        copy.drawString(yAxis, marginLeft - stringHeight, start);
	}


	public void newSection() {
		curX = marginLeft;
		curY += sectionPadding;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Graphics2D getGraphics() {
		return g2;
	}

	public void saveImage(String type, String path) {
		try {

            BufferedImage newImage = new BufferedImage(width, curY, image.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(image, 0, 0, null); // scaled drawing of choice
            g.dispose();

		    File outputfile = new File(path);
		    ImageIO.write(newImage, type, outputfile);

		    System.out.println("Saved as " + path);
		} catch (IOException e) {
		    System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Canvas demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		IndChromosome canvas = new IndChromosome("Chromosome 2", 250, 1000,1000);
		canvas.draw();

		JLabel picLabel = new JLabel(new ImageIcon(canvas.getImage()));
		frame.getContentPane().add(picLabel);
		frame.pack();
		frame.setVisible(true);
	}

    public void drawDGV(ArrayList<Gene> value, Integer maxi37) {
        double scale = ((double) width - (double) (marginLeft +marginRight)) / ((double) maxi37 / 1000000.);

        g2.setColor(new Color(0.5f, 0.5f, 0f, .99f));
        for(Gene g : value) {


            double start = (g.getStart() * scale / maxi37) * (maxi37 / 1000000);
            double end = (g.getEnd() * scale / maxi37) * (maxi37 / 1000000);

            if (start == end) {
                g2.drawLine(curX + (int) Math.round(start), curY, curX + (int) Math.round(start), curY + 9);
            } else {
                g2.fillRect(curX + (int) Math.round(start), curY, (int) Math.round(end-start), 10);
            }
        }


    }

    public void drawGenes(ArrayList<Gene> value, Integer maxi37) {
        double scale = ((double) width - (double) (marginLeft + marginRight)) / ((double) maxi37 / 1000000.);

        for(Gene g : value) {
            g2.setColor(new Color(0, 0, 0));

            double start = (g.getStart() * scale / maxi37) * (maxi37 / 1000000);
            double end = (g.getEnd() * scale / maxi37) * (maxi37 / 1000000);

            int stringLen = (int) g2.getFontMetrics().getStringBounds(g.getName(), g2).getWidth();

            int stringHeight = (int) g2.getFontMetrics().getStringBounds(g.getName(), g2).getHeight();
            g2.drawString(g.getName(), (int) (curX + start + (Math.round(end-start)/2)) - stringLen/2, curY);

            if (start == end) {
                g2.drawLine(curX + (int) Math.round(start), curY+  stringHeight, curX + (int) Math.round(start), curY+  stringHeight + 10);
            } else {
                g2.fillRect(curX + (int) Math.round(start), curY+  stringHeight, (int) Math.round(end-start), 10);
            }
        }
    }

    public void drawLeg(String label, Integer maxi37, int top, int size, Color c) {
        for(int i = 0; i < top; i++) {
            float[] overlapColour = c.getRGBColorComponents(null);
//            Color col = new Color(0.98f - overlapColour[0] * 1.0f / (float) top * i, 0.98f - overlapColour[1] * 1.0f / (float) top * i, 0.98f - overlapColour[2] * 1.0f / (float) top * i);
            Color col = new Color(overlapColour[0], overlapColour[1], overlapColour[2], (float) (1./top) * i);
            g2.setColor(col);

            double start = ((width - (marginLeft+marginRight))/size) * i / top;
            double end = ((width - (marginLeft+marginRight))/size) * (i+1) / top;

            g2.fillRect(curX + (int) Math.round(start), curY, (int) Math.round(end-start), 10);
        }

        g2.setColor(Color.black);

        int stringLen = (int) g2.getFontMetrics().getStringBounds("0", g2).getWidth();

        int stringHeight = (int) g2.getFontMetrics().getStringBounds("0", g2).getHeight();
        int start = curX - stringLen/2;
        g2.drawString("0", start, curY + stringHeight + 5);

        stringLen = (int) g2.getFontMetrics().getStringBounds(top + "", g2).getWidth();
        start = (((width - (marginLeft+marginRight))/size) * (top) / top);
        g2.drawString(top + "", curX + start - stringLen/2, curY + stringHeight + 10);

        stringHeight = (int) g2.getFontMetrics().getStringBounds(label, g2).getHeight();
        start = curX + (((width - (marginLeft+marginRight))/size) * (top) / top) + 10;
        g2.drawString(label, start, curY + stringHeight/2 + 5);

//        curX += 13;
        curY += padding;
    }

    public void drawCyto(ArrayList<Cytoband> cytobands, Integer maxi37) {
        double scale = ((double) width - (double) (marginLeft + marginRight)) / ((double) maxi37 / 1000000.);

        int acens = 0;

        int cytoHeight = 50;

        for(Cytoband cyto : cytobands) {
            double start = ((double) cyto.getChromStart() * scale / (double) maxi37) * (maxi37 / 1000000);
            double end = ((double) cyto.getChromEnd() * scale / (double) maxi37) * (maxi37 / 1000000);

            g2.setColor(stainColours.get(cyto.getGieStain()));
            if(cyto.getGieStain().equals("acen")) {
                if(acens == 0) {
                    g2.fillPolygon(new int[] {curX+(int) Math.round(start), curX+(int) Math.round(start), curX+(int) Math.round(end)}, new int[]{ curY, curY + cytoHeight, curY + cytoHeight/2 }, 3);
                    acens++;

                    g2.setColor(Color.black);
                    g2.drawLine(curX, curY-1, curX + (int) Math.round(start), curY-1);
                    g2.drawLine(curX, curY, curX, curY + cytoHeight);
                    g2.drawLine(curX, curY + cytoHeight, curX + (int) Math.round(start), curY + cytoHeight);
                }
                else {
                    g2.fillPolygon(new int[] {curX+(int) Math.round(start), curX+(int) Math.round(end), curX+(int) Math.round(end)}, new int[]{ curY + cytoHeight/2, curY, curY + cytoHeight }, 3);
                    acens++;

                    g2.setColor(Color.black);
                    g2.drawLine(curX + (int) Math.round(end), curY-1, width-marginRight, curY-1);
                    g2.drawLine(width-marginRight, curY, width-marginRight, curY + cytoHeight);
                    g2.drawLine(curX + (int) Math.round(end), curY + cytoHeight, width-marginRight, curY + cytoHeight);
                }
            }
            else {
                g2.fillRect(curX + (int) Math.round(start), curY, (int) Math.round(end - start), cytoHeight);
            }

            if(end-start > 15 && !cyto.getGieStain().equals("acen")) {
                int stringLen = (int) g2.getFontMetrics().getStringBounds(cyto.getName(), g2).getWidth();
                int stringHeight = (int) g2.getFontMetrics().getStringBounds(cyto.getName(), g2).getHeight();

                int strStart = curX + (int) Math.round(start) + stringHeight;
                int strEnd = curY + cytoHeight/2 + stringLen/2;
                // clockwise 90 degrees
                AffineTransform at = new AffineTransform();
                at.setToRotation(-Math.PI/2.0, strStart, strEnd);

                Graphics2D copy = (Graphics2D) g2.create();
                copy.setTransform(at);

                if(stainColours.get(cyto.getGieStain()).getRed() > 0.5) {
                    copy.setColor(Color.black);
                } else {
                    copy.setColor(Color.white);
                }
//                copy.drawString(cyto.getName(), marginLeft - stringHeight, start);
                copy.drawString(cyto.getName(), strStart, strEnd);
            }

        }

        curY += cytoHeight + sectionPadding;
    }

    public void drawLegend(String[] strings, Color[] colors) {
        int lineWidth = 50;
        int padding = 10;

        curX = width-marginRight;
        for(int i = strings.length-1; i >= 0; i--) {
            int stringLen = (int) g2.getFontMetrics().getStringBounds(strings[i], g2).getWidth();
            int stringHeight = (int) g2.getFontMetrics().getStringBounds(strings[i], g2).getHeight();

            int sectionWidth = lineWidth + padding*2 + stringLen;

            curX -= sectionWidth;

            g2.setColor(colors[i]);
            g2.drawLine(curX, curY, curX + lineWidth, curY);

            g2.setColor(Color.black);
            g2.drawString(strings[i], curX + lineWidth + padding, curY + stringHeight/2);
        }

        curX = 0;
    }

    public void drawIndividualSample(String sample, Events patientEvents, String chr, Integer maxi37) {
        double scale = ((double) width - (double) (marginLeft + marginRight)) / ((double) maxi37 / 1000000.);

        int stringHeight = (int) g2.getFontMetrics().getStringBounds(sample, g2).getHeight();
        int stringLen = (int) g2.getFontMetrics().getStringBounds(sample, g2).getWidth();

        g2.setFont(labelFont);
        g2.setColor(new Color(0.95f, 0.95f, 0.95f));
        g2.fillRect(curX, curY, width-(marginLeft+marginRight), stringHeight);
        g2.setColor(Color.BLACK);

        g2.drawString(sample, width-marginRight+5, curY + stringHeight);

        for(int i = 0; i < patientEvents.getEvents().size(); i++) {
            g2.setColor(ABERRATION_COLOURS[i]);

            Event e = patientEvents.getEvents().get(i);

            Patient p = null;
            for(Patient pat : e.getList().get(chr)) {
                if(pat.getPatientID().equals(sample)) {
                    p = pat;



                    double x;
                    if(p.getStart() == 0) {
                        x = 0;
                    } else {
                        x = p.getStart();
                    }

                    double xx;
                    if(p.getEnd() == 0) {
                        xx = maxi37;
                    } else {
                        xx = p.getEnd();
                    }

                    double start = ((x * scale) / (double) maxi37) * (maxi37 / 1000000);
                    double end = ((xx * scale) / (double) maxi37) * (maxi37 / 1000000);

                    double myY0 = (curY);
                    double myY1 = (curY);

                    if(start == end) {
                        g2.drawLine(curX + (int) + Math.round(start), (int) Math.round(myY0), curX + (int) + Math.round(start), stringHeight);
                    }
                    else {

                        g2.fillRect(curX + ((int) Math.round(start)), ((int) Math.round(myY0)), ((int) Math.round(end-start)), stringHeight);

                        // TODO
//                if loh and lohname.lower() in record[2].lower():
//                myplot.linewidth(lohlw)
//                myplot.color(lohcol)
//                myplot.line(start, myY1, end, myY1)
//                myplot.linewidth()
                    }
                }
            }

        }

        curY += padding;

    }

    public void drawCytoV(ArrayList<Cytoband> cytobands, Integer maxi37) {
//        double scale = ((double) height/4 - (double) marginLeft * 2.) / ((double) maxi37 / 1000000.);

        double scale = 2;
        int acens = 0;

        int cytoHeight = 20;

        int tempY = curY;
        curY += 20;

        for(Cytoband cyto : cytobands) {
            double start = ((double) cyto.getChromStart() * scale / (double) maxi37) * (maxi37 / 1000000);
            double end = ((double) cyto.getChromEnd() * scale / (double) maxi37) * (maxi37 / 1000000);

            g2.setColor(stainColours.get(cyto.getGieStain()));
            if(cyto.getGieStain().equals("acen")) {
                if(acens == 0) {
                    g2.fillPolygon(new int[] {curX, curX+(int) cytoHeight, curX+(int) cytoHeight/2}, new int[]{ curY + (int) Math.round(start), curY + + (int) Math.round(start), curY + + (int) Math.round(end) }, 3);
                    acens++;

                    g2.setColor(Color.black);
                    g2.drawLine(curX, curY, curX, curY + (int) Math.round(start));
                    g2.drawLine(curX, curY, curX + cytoHeight, curY);
                    g2.drawLine(curX+cytoHeight, curY, curX + cytoHeight, curY + (int) Math.round(start));
                }
                else {
                    g2.fillPolygon(new int[] {curX, curX+cytoHeight/2, curX+cytoHeight}, new int[]{ curY + (int) Math.round(end), curY+(int) Math.round(start), curY + (int) Math.round(end) }, 3);
                    acens++;

                    Cytoband last = cytobands.get(cytobands.size()-1);
                    double lastSart = ((double) last.getChromStart() * scale / (double) maxi37) * (maxi37 / 1000000);
                    double lastEnd = ((double) last.getChromEnd() * scale / (double) maxi37) * (maxi37 / 1000000);

                    g2.setColor(Color.black);
                    g2.drawLine(curX-1 , curY+ (int) Math.round(end), curX-1, curY + (int) Math.round(lastEnd));
                    g2.drawLine(curX, curY + (int) Math.round(lastEnd)+1, curX + cytoHeight, curY + (int) Math.round(lastEnd)+1);
                    g2.drawLine(curX + cytoHeight, curY + (int) Math.round(end), curX + cytoHeight, curY + (int) Math.round(lastEnd));
                }
            }
            else {
//                System.out.println("curX = " + curX + " start = " + (int) Math.round(start) + ", curY = " + curY + ", width = " + (int) Math.round(end-start) + ", height = " + 20);
                g2.fillRect(curX, curY + (int) Math.round(start), cytoHeight, (int) Math.round(end - start));
            }

//            curY += (int) Math.round(end - start);

        }

        curY = tempY;

        curX += cytoHeight + padding;
//        curY += cytoHeight + sectionPadding;

    }

    public void drawAberrationsV(String chr, Integer maxi37, Events events, HashMap<String, ArrayList<Patient>> sortedPatients) {
//        double scale = ((double) height/4 - (double) marginLeft * 2.) / ((double) maxi37 / 1000000.);
        double scale = 2;

        int tempY = curY;
        curY += 20;

        for (int i = 0; i < events.getEvents().size(); i++) {
            // ArrayList<Patient> sortedAbberations = sortAberration(e.getList(chr));
            HashMap<String, ArrayList<Patient>> patientsMap = new HashMap<String, ArrayList<Patient>>();
            ArrayList<Patient> patients = sortedPatients.get(events.getEvents().get(i).getType());
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

            if(events.getEvents().get(i).getList().get(chr) == null || events.getEvents().get(i).getList().get(chr).size() == 0) continue;

            for (Map.Entry<String, ArrayList<Patient>> entry : patientsMap.entrySet()) {
                String key = entry.getKey();
                ArrayList<Patient> value = entry.getValue();

                for (Patient s : value) {
                    double x;
                    if (s.getStart() == 0) {
                        x = 0;
                    } else {
                        x = s.getStart();
                    }

                    double xx;
                    if (s.getEnd() == 0) {
                        xx = maxi37;
                    } else {
                        xx = s.getEnd();
                    }

                    double start = ((x * scale) / (double) maxi37) * (maxi37 / 1000000);
                    double end = ((xx * scale) / (double) maxi37) * (maxi37 / 1000000);

                    double myY0 = (curY - 4.);
                    double myY1 = (curY + 4.);

                    g2.setColor(ABERRATION_COLOURS[i]);
                    if (start == end) {
                        g2.drawLine(curX, curY + (int) Math.round(start), curX, curY + (int) +Math.round(end));
                    } else {

                        g2.fillRect(curX, curY + (int) Math.round(start), 5, (int) Math.round(end - start));
                        g2.setColor(Color.BLACK);

                        // TODO
                        //                if loh and lohname.lower() in record[2].lower():
                        //                myplot.linewidth(lohlw)
                        //                myplot.color(lohcol)
                        //                myplot.line(start, myY1, end, myY1)
                        //                myplot.linewidth()
                    }



                }

                curX += 10;
            }

        }

        curY = tempY;
    }

    public void drawIndividualAberrationsV(String chr, Integer maxi37, int chromEnd, HashMap<String, ArrayList<Sample>> patients) {
//        double scale = ((double) height/4 - (double) marginLeft * 2.) / ((double) maxi37 / 1000000.);
        double scale = 2;

        int tempY = curY;
        curY += 20;

        for (Map.Entry<String, ArrayList<Sample>> entry : patients.entrySet()) {
            String p = entry.getKey();
            ArrayList<Sample> samples = entry.getValue();

            for(Sample s : samples) {
                g2.setColor(new Color(0.95f, 0.95f, 0.95f));
                g2.fillRect(curX, curY, 5, (int) Math.round(((chromEnd * scale) / (double) maxi37) * (maxi37 / 1000000)));

                double x;
                if(s.getStart() == 0) {
                    x = 0;
                } else {
                    x = s.getStart();
                }

                double xx;
                if(s.getEnd() == 0) {
                    xx = maxi37;
                } else {
                    xx = s.getEnd();
                }

                double start = ((x * scale) / (double) maxi37) * (maxi37 / 1000000);
                double end = ((xx * scale) / (double) maxi37) * (maxi37 / 1000000);

                double myY0 = (curY - 4.);
                double myY1 = (curY + 4.);

                g2.setColor(s.getColor());
                if(start == end) {
                    g2.drawLine(curX , curY + (int) Math.round(start), curX, curY + (int) + Math.round(end));
                }
                else {

                    g2.fillRect(curX, curY + (int) Math.round(start), 5, (int) Math.round(end-start));
                    g2.setColor(Color.BLACK);

                    // TODO
//                if loh and lohname.lower() in record[2].lower():
//                myplot.linewidth(lohlw)
//                myplot.color(lohcol)
//                myplot.line(start, myY1, end, myY1)
//                myplot.linewidth()
                }
            }

            curX += 10;
        }

        curY = tempY;
    }

    public void drawSubtitle(String chr) {
        g2.setColor(Color.black);
        g2.setFont(titleFont);
        int stringLen = (int) g2.getFontMetrics().getStringBounds(chr, g2).getWidth();
//        int stringHeight = (int) g2.getFontMetrics().getStringBounds(chr, g2).getHeight();
        g2.drawString(chr, curX, curY);
        g2.setFont(labelFont);
    }

    public void drawSubtitleCenter(String title) {
        g2.setFont(titleFont);
        int stringLen = (int) g2.getFontMetrics().getStringBounds(title, g2).getWidth();
        int stringHeight = (int) g2.getFontMetrics().getStringBounds(title, g2).getHeight();
        int start = width/2 - stringLen/2;
        g2.drawString(title, start, curY + stringHeight);

        curY += stringHeight + padding;
        g2.setFont(labelFont);
    }

    public void drawSummaryLegend(Events events) {
        int boxWidth = 100;
        int boxHeight = 30;

        for(int i = 0; i < events.getEvents().size(); i++) {
            g2.setColor(ABERRATION_COLOURS[i]);
            g2.fillRect(curX, curY, boxWidth, boxHeight);

            g2.setColor(Color.black);
            g2.setFont(titleFont);
            int stringLen = (int) g2.getFontMetrics().getStringBounds(events.getEvents().get(i).getType(), g2).getWidth();
            int stringHeight = (int) g2.getFontMetrics().getStringBounds(events.getEvents().get(i).getType(), g2).getHeight();

            g2.drawString(events.getEvents().get(i).getType(), curX + boxWidth + padding, curY + boxHeight/2 + stringHeight/2);
            g2.setFont(labelFont);

            curY += boxHeight + padding;
        }
    }
}