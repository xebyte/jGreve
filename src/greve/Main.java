package greve;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import com.martiansoftware.jsap.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.lang.Integer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import greve.draw.IndividualDiagram;
import greve.draw.SummaryDiagram;
import greve.struct.Event;
import jxl.write.WriteException;

import greve.struct.*;
import greve.draw.Canvas;

import printer.PrinterDriver;
import rcaller.RCaller;
import rcaller.RCode;

/**
 * Imports the data needed for the program to work and puts it to suitable datastructures.
 */
public class Main {
	public static String[] chromosomeList = new String[]{"chr1","chr2","chr3","chr4","chr5","chr6", "chr7", "chr8", "chr9", "chr10", "chr11", "chr12", "chr13", "chr14", "chr15", "chr16", "chr17", "chr18", "chr19", "chr20", "chr21", "chr22", "chrX", "chrY"};

	private ChromosomeMap genes;
	private ChromosomeMap dgvdata;

	private HashMap<String, Integer> maxi37;
	private HashMap<String, Integer> maxi;
	private HashMap<Integer, String> chrom;
	private HashMap<Integer, Integer> position;
	private HashMap<Integer, String> position37;
	private HashMap<String, ArrayList<Cytorange>> cyto;

    private int outputType = 1; // 0 = CSV, 1 = XLS
	private Events events;

	private Config config;

	public static boolean debug = false;

	private HashMap<String, ArrayList<Cytoband>> annot;
	private ArrayList<Patient> patients;

	private static String PATH;
	public static String OUTPUT_PATH;

    private PrinterDriver globalWriter;

    private int[] top;

    private String title;
    private boolean showDGV;
    private boolean showScore;
    private boolean showGWScore;
    private boolean showCScore;

    private HashMap<String, ArrayList<String[]>> results;

	/**
	 * Initialize all the datastructures
	 */
	public Main(String aberrations) {

		try {
//			PATH = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "/";
            PATH = "./";
		} catch(Exception ex) {
			System.err.println("Couldn't determine working directory path.");
		}

        results = new HashMap<String, ArrayList<String[]>>();

		genes = new ChromosomeMap();
        dgvdata = new ChromosomeMap();

		chrom = new HashMap<Integer, String>();

		maxi37 = new HashMap<String, Integer>();
		maxi = new HashMap<String, Integer>();

		position = new HashMap<Integer, Integer>();
		position37 = new HashMap<Integer, String>();

		annot = new HashMap<String, ArrayList<Cytoband>>();

		cyto = new HashMap<String, ArrayList<Cytorange>>();

		events = new Events();

		patients = new ArrayList<Patient>();

        File aberrationFile = new File(aberrations);
        String[] filename = aberrationFile.getName().split("\\.");

        String toSave;
        if(outputType == Printer.TYPE_CVS) {
            toSave = filename[0] + ".txt";
        } else {
            toSave = filename[0] + ".xls";
        }

        try {
            globalWriter = Printer.getWriter(OUTPUT_PATH + "/" + toSave, outputType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {

		// Set up command line parameters
		JSAP jsap = new JSAP();

        UnflaggedOption filename = new UnflaggedOption("filename")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setRequired(true);

        FlaggedOption build = new FlaggedOption("build")
                                .setStringParser(JSAP.INTEGER_PARSER)
                                .setDefault("37")
                                .setShortFlag('b')
                                .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption chrom = new FlaggedOption("chrom")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setList(true)
                                .setListSeparator(',')
                                .setShortFlag('c')
                                .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption output = new FlaggedOption("output")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setShortFlag('o')
                                .setDefault("./")
                                .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption cfgfile = new FlaggedOption("cfg")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("default.cfg")
                                .setShortFlag('f')
                                .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption genefile = new FlaggedOption("genes")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setShortFlag('g')
                                .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption bin = new FlaggedOption("bin")
                                .setStringParser(JSAP.INTEGER_PARSER)
                                .setShortFlag(JSAP.NO_SHORTFLAG)
                                .setDefault("1")
                                .setLongFlag("bin");

        FlaggedOption sum = new FlaggedOption("sum")
                                .setStringParser(JSAP.INTEGER_PARSER)
                                .setShortFlag('s')
                                .setLongFlag("sum");

        FlaggedOption title = new FlaggedOption("title")
                .setStringParser(JSAP.STRING_PARSER)
                .setShortFlag(JSAP.NO_SHORTFLAG)
                .setLongFlag("title");

        Switch dgv = new Switch("dgv").setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("dgv");
        Switch showScore = new Switch("showScore").setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("Score");
        Switch genomeWide = new Switch("showGWP").setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("GW_P");
        Switch chrWide = new Switch("showCP").setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("C_P");

        Switch showHelp = new Switch("help")
                        .setShortFlag('h')
                        .setLongFlag("help");

        Switch showDebug = new Switch("debug")
                        .setShortFlag('v')
                        .setLongFlag("verbose");

        Switch individual = new Switch("individual")
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("ind");

        try {
        	jsap.registerParameter(filename);
        	jsap.registerParameter(build);
	        jsap.registerParameter(chrom);
	        jsap.registerParameter(cfgfile);
	        jsap.registerParameter(genefile);
	        jsap.registerParameter(showHelp);
	        jsap.registerParameter(showDebug);
	        jsap.registerParameter(bin);
	        jsap.registerParameter(sum);
	        jsap.registerParameter(individual);
	        jsap.registerParameter(output);
            jsap.registerParameter(title);

            jsap.registerParameter(dgv);
            jsap.registerParameter(showScore);
            jsap.registerParameter(genomeWide);
            jsap.registerParameter(chrWide);
        } catch(JSAPException e) {
        	System.err.println("Can't parse the arguments");
        	return;
        }

        final JSAPResult config = jsap.parse(args);

        // if --help then print usage and quit
        if(config.getBoolean("help")) {
        	printUsage();
        	return;
        }

        if(config.getBoolean("debug")) {
        	debug = true;
        }

        if(config.getString("output") != null) OUTPUT_PATH = config.getString("output");
        File outputPath = new File(OUTPUT_PATH);
        if(!outputPath.exists()) {
            if(!outputPath.mkdirs()) {
                System.err.println("The output path couldn't be accessed");
                return;
            }
        }

        // start the execution
        final Main main = new Main(config.getString("filename"));
        main.setTitle(config.getString("title"));
        main.setShowDGV(config.getBoolean("dgv"));
        main.setShowScore(config.getBoolean("showScore"));
        main.setGenomeWideScore(config.getBoolean("showGWP"));
        main.setChromosomeWideScore(config.getBoolean("showCP"));

        // read the gene file
        if(config.getString("genes") != null) {
            try {
                main.readGenes(config.getString("genes"));

                if(debug) System.out.println("Gene list:\n" + main.getGenes());
            } catch(Exception e) {
                System.err.println("Couldn't load genes from " + config.getString("genes") + ": ");
                System.err.println(e.getMessage());
                if(debug) e.printStackTrace();
            }
        }

        try {
        	main.readDGVData(config.getInt("build"));
        } catch(Exception e) {
        	System.err.println("Couldn't load DGV data for build " + config.getInt("build") + ": ");
        	System.err.println(e.getMessage());
        	if(debug) e.printStackTrace();
        	return;
        }

        // read the annotation file
        try {
        	main.readAnnot(chromosomeList, config.getInt("build"));

        	if(debug) System.out.println("annot:\n" + main.getAnnot());
        } catch(Exception e) {
        	System.err.println("Couldn't load annotations for build " + config.getInt("build") + ": ");
        	System.err.println(e.getMessage());
        	if(debug) e.printStackTrace();
        	return;
        }

        // make cytobands
        main.makeCyto(chromosomeList);
        if(debug) System.out.println("cyto:\n" + main.getCyto());

        // read patient abberations
        try {
//            System.out.println(config.getString("filename"));
        	main.readAbberations(config.getString("filename"));
        	if(debug) System.out.println("abberations:\n" + main.getEvents());
        } catch(Exception e) {
        	System.err.println("Couldn't load abberations from " + config.getString("filename") + ": ");
        	System.err.println(e.getMessage());
        	if(debug) e.printStackTrace();
        	return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(16);
        for(int i = 0; i < config.getStringArray("chrom").length; i++) {
            final Integer index = new Integer(i);
            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    try {
                        main.calculateIndividual(config.getStringArray("chrom")[index], config.getInt("bin"), config.getInt("sum"), config.getBoolean("individual"));
                    } catch (Exception e) {
                        System.err.println("There was an error during calculations");
                        System.err.println(e.getMessage());
                        if(debug) e.printStackTrace();
                        return;
                    }
                }
            };
            executor.execute(worker);
        }

        executor.execute(new Runnable() {
             @Override
             public void run() {
                 main.plotSummary(config.getBoolean("individual"), config.getInt("sum"), config.getString("title"));
             }
        });

        executor.shutdown();
        while (!executor.isTerminated()) {}

        try {
            main.printSummary(config.getStringArray("chrom"), config.getInt("sum"));
        } catch (WriteException e) {
            e.printStackTrace();
        }

        System.out.println("Finished all threads");
//        System.out.println(main.getEvents());

        // clean up
        main.close();

        // plot MHT
	}

    private void setTitle(String title) {
        this.title = title;
    }

    public ChromosomeMap getDGV() {
        return dgvdata;
    }

    /**
     * Close all the buffers before ending the execution
     */
    public void close() {
        try {
            globalWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
	 * Routine reading the gene information from 4 columns: name, chromosome, start, end
	 * If only a start is given then it is used to draw a segment
	 * @param file Gene file
	 */
	public void readGenes(String file) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;

		while ((line = br.readLine()) != null) {
			line = line.replaceAll("\\s+", " ").trim();
			String[] record = line.split(" ");

			if(debug) System.out.println("Current gene line: " + Arrays.toString(record));

			if(record.length > 2) {
				if(record.length > 3) {
					genes.add(record[1], new Gene(record[0], Integer.parseInt(record[2]), Integer.parseInt(record[3])));
				} else {
					genes.add(record[1], new Gene(record[0], Integer.parseInt(record[2])));
				}
			} else {
				System.out.println("Gene couldn't be added");
			}
		}

		br.close();
	}

	/**
	 * Routine reading the DGV information, with the proper build, filter it and tranform into 4 columns: name, chromosome, start, end
	 * If only a start is given then it is used to draw a segment
	 * @param  build                 Build number, 37 is default
	 * @throws FileNotFoundException If DGV data file is not found
	 * @throws IOException           IO problems
	 */
	public void readDGVData(int build) throws FileNotFoundException, IOException {
		String[] dgvFiles = new String[]{"variation.hg19.v10.nov.2010.txt","indel.hg19.v10.nov.2010.txt"};
		if (build == 35)
			dgvFiles= new String[]{"variation.hg17.v10.nov.2010.txt","indel.hg17.v10.nov.2010.txt"};
		else if (build == 36)
			dgvFiles= new String[]{"variation.hg18.v10.nov.2010.txt","indel.hg18.v10.nov.2010.txt"};
		else if (build == 37)
			dgvFiles= new String[]{"variation.hg19.v10.nov.2010.txt","indel.hg19.v10.nov.2010.txt"};

		for(int i = 0; i < dgvFiles.length; i++) {
			BufferedReader br = new BufferedReader(new FileReader(PATH + "res/data/" + dgvFiles[i]));
			String line;

			// skip the first line as it contains headers
			br.readLine();
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\s+", " ").trim();
				String[] record = line.split(" ");

				if(record.length > 2) {
					if(record.length > 3) {
						dgvdata.add(record[2], new Gene(record[0], Integer.parseInt(record[3]), Integer.parseInt(record[4])));
					} else {
						dgvdata.add(record[2], new Gene(record[0], Integer.parseInt(record[3])));
					}
				} else {
					System.out.println("Chromosome couldn't be added");
				}
			}

			br.close();
		}
	}

	/**
	 * Routine providing the locations of the cytobands in a specific build
	 * @param chromosomes a list of chromosomes
	 * @param build       build number, default 37
	 */
	public void readAnnot(String[] chromosomes, int build) throws FileNotFoundException, IOException {
		for(int i = 0; i < chromosomes.length; i++) {
			maxi37.put(chromosomes[i], 0);
			maxi.put(chromosomes[i], 0);
			annot.put(chromosomes[i], new ArrayList<Cytoband>());
		}

		BufferedReader br = new BufferedReader(new FileReader(PATH + "/res/data/cytoBand"+build+".txt"));
		String line;

		while ((line = br.readLine()) != null) {
			line = line.replaceAll("\\s", " ").trim();
			String[] record = line.split(" ");
			if(debug) System.out.println("Annot line: " + Arrays.toString(record));

			maxi37.put(record[0], Math.max(Integer.parseInt(record[2]), maxi37.get(record[0])));
			String thisChr=record[0];
		    maxi.put(thisChr, Math.max(Integer.parseInt(record[2]), maxi.get(thisChr)));
		    chrom.put(Integer.parseInt(record[1]), thisChr);
		    position.put(Integer.parseInt(record[1]), Integer.parseInt(record[2]));
		    position37.put(Integer.parseInt(record[1]), record[3]);

		    Cytoband cyto = new Cytoband(record[0], Integer.parseInt(record[1]), Integer.parseInt(record[2]), record[3], record[4]);

		    annot.get(record[0]).add(cyto);
		}

		System.out.println("Done reading " + position37.size() + " SNPs");
		System.out.println("Maxi is " + maxi);
		System.out.println("Maxi for build37 is " + maxi37);

		br.close();
	}

	/**
	 * Make list of cyto events to match Karin's coding with pter, qter, pcen and qcen
	 * @param chromosomes List of chromosomes
	 */
	public void makeCyto(String[] chromosomes) {
		for(int i = 0; i < chromosomes.length; i++) {
			boolean pcen = true;

			ArrayList<Cytorange> list = new ArrayList<Cytorange>();
			cyto.put(chromosomes[i], list);
			String c = chromosomes[i];

			list.add(new Cytorange(c.substring(3, c.length())+"pter",0,0));

			ArrayList<Cytoband> chr = annot.get(chromosomes[i]);
			for(int k = 0; k < chr.size(); k++) {
				Cytoband a = chr.get(k);

				list.add(new Cytorange(c.substring(3, c.length())+a.getName(),a.getChromStart(),a.getChromEnd()));

				if(a.getGieStain().equals("acen")) {
					if(pcen) {
						pcen = false;
						list.add(new Cytorange(c.substring(3, c.length())+"pcen",a.getChromStart(),a.getChromStart()));
					} else {
						cyto.get(c).add(k+2, new Cytorange(c.substring(3, c.length())+"qcen",a.getChromStart(),a.getChromStart())); // ??
					}
				}
			}

			Cytoband a = annot.get(chromosomes[i]).get(annot.get(chromosomes[i]).size()-1);
			list.add(new Cytorange(c.substring(3, c.length())+"qter",a.getChromStart(),a.getChromStart()));
		}
	}

	/**
	 * Return the location for Start or End from either cyto end or location with SNP name
	 * @param  str       String to get the location from
	 * @param  chr       A chromosome
	 * @return           Returns start/end location
	 * @throws Exception If the location couldn't be parsed
	 */
	public int findLoc(String str, String chr) throws Exception {
		int loc = -1;

		try {
			loc = Integer.parseInt(str);
		} catch(Exception e) {
			String[] split = str.split(" ");
//			System.out.println("split: " + split);
			if(split.length > 1) {
				loc = Integer.parseInt(split[split.length-1]);
			} else if(str.contains("pter")) {
				loc = annot.get(chr).get(0).getChromStart();
			} else if(str.contains("qter")) {
				loc = annot.get(chr).get(annot.get(chr).size()-1).getChromEnd();
			} else if(str.contains("pcen")) {
				ArrayList<Cytoband> cytos = annot.get(chr);
				for(int i = 0; i < cytos.size(); i++) {
					if(cytos.get(i).getGieStain().equals("acen")) {
						loc = cytos.get(i).getChromStart();
						break;
					}
				}

			} else if(str.contains("qcen")) {
				ArrayList<Cytoband> cytos = annot.get(chr);

				boolean flag = false;
				for(int i = 0; i < cytos.size(); i++) {
					if(cytos.get(i).getGieStain().equals("acen")) {
						if(flag) loc = cytos.get(i).getChromStart();
						flag = true;
					}
				}
			} else {
				System.out.println("Uncrecognized location for " + str);
				throw new Exception("Uncrecognized location");
			}
		}

		return loc;
	}

	/**
	 * Read abberations from file
	 * @param  filename              A file with abberations
	 * @throws FileNotFoundException If the file couldn't be found
	 * @throws IOException
	 * @throws Exception
	 */
	public void readAbberations(String filename) throws FileNotFoundException, IOException, Exception {
		String[] file = filename.split("\\.");
		String fileType = file[file.length-1];
		String patientID;

        PrinterDriver printer;

		if(fileType.equals("xls") || fileType.equals("xlsx")) {
            printer = Printer.getReader(filename, Printer.TYPE_XLS);
        }
        else {
            printer = Printer.getReader(filename, Printer.TYPE_CVS);
        }

        String[] line;

        printer.readNextLine();
        while ((line = printer.readNextLine()) != null) {
            String pid = line[0];
            String chr;
            try {
                chr = "chr" + (int) Double.parseDouble(line[1]);
            } catch(NumberFormatException e) {
                chr = "chr" + line[1];
            }

            String type = line[2];
            String sStart = "" + (int) Double.parseDouble(line[3]);
            String sEnd = "" + (int) Double.parseDouble(line[4]);

            if(debug) System.out.println("Abberation line: " + Arrays.toString(new String[]{pid, chr, type, sStart, sEnd}));

            String[] split = chr.split("\\.");
            chr = split[0];

            int start = findLoc(sStart, chr);
            int end = findLoc(sEnd, chr);

            Patient patient = new Patient(pid, start, end);

            boolean found = false;
            for(Patient p : patients) {
                if(p.getPatientID().equals(pid)) {
                    found = true;
                    break;
                }
            }

            if(!found) patients.add(patient);

            events.add(new Event(type));
            events.get(type).add(chr, patient);
        }

        top = new int[events.getEvents().size()];
        for(int i = 0; i < top.length; i++) {
            top[i] = 5;
        }
	}

    public void printSummary(String[] chroms, int sum) throws WriteException {
        String[] headers = new String[]{"Chromosome", "Type", "Start", "End", "Count", "Score", "GW_P", "C_P"};
        globalWriter.write(headers);

        for(int i = 0; i < Common.CHROMOSOME_LIST.length; i++) {
            String key = Common.CHROMOSOME_LIST[i];
            ArrayList<String[]> value = results.get(key);
            if(value == null) continue;

            for(String[] s : value) {
                globalWriter.write(s);
            }
        }


//        for(String chr : chroms) {
//            for (int i = 0; i < events.getEvents().size(); i++) {
//                ArrayList<Overlap> overlaps = events.getEvents().get(i).getOverlaps(chr);
//                Event e = events.getEvents().get(i);
//                if(e.getList().get(chr) != null && e.getList().get(chr).size() != 0) {
//                    if (sum > 1) {
//                        for (int k = 0; k < overlaps.size(); k++) {
////                            System.out.println(Arrays.toString(overlaps.get(k).toPrint()));
//
//                        }
//                    }
//                }
//            }
//        }
    }

    /**
     * Makes overlaps and copies them to a separate hashmap to free the original one for use of other threads
     * @param chr
     * @param bin
     * @param sum
     * @return
     */
    synchronized HashMap<Integer, ArrayList<Overlap>> makeOverlaps(String chr, int bin, int sum) {
        HashMap<Integer, ArrayList<Overlap>> map = new HashMap<Integer, ArrayList<Overlap>>();

        if(sum > 1) {
            for(int i = 0; i < events.getEvents().size(); i++) {
                events.getEvents().get(i).makeOverlap(bin, patients, chr, maxi, maxi37);
                ArrayList<Overlap> overlaps = events.getEvents().get(i).getOverlaps(chr);
                ArrayList<Overlap> copies = new ArrayList<Overlap>();
                for(Overlap o : overlaps) {
                    copies.add(o.clone());
                }

                map.put(i, copies);
            }
        }

        return map;
    }

    /**
     * Calculates individual chromosome passed from the user, prints the output to a file and produces a graph
     * @param chr
     * @param bin
     * @param sum
     * @param indiv
     * @throws Exception
     */
	public void calculateIndividual(String chr, int bin, int sum, boolean indiv) throws Exception {
		String[] headers = new String[]{"Chromosome", "Type", "Start", "End", "Count", "Score", "GW_P", "C_P"};

//		System.out.println("Chromosome: " + chr);
        String filename;
        if(outputType == Printer.TYPE_CVS) {
            filename = OUTPUT_PATH + chr + ".txt";
        }
        else {
            filename = OUTPUT_PATH + chr + ".xls";
        }

        int[] top = new int[events.getEvents().size()];

        PrinterDriver writer = Printer.getWriter(filename, outputType);
        writer.write(headers);

		ArrayList<Event> events = this.events.getEvents();

        HashMap<Integer, ArrayList<Overlap>> overlapMap = makeOverlaps(chr, bin, sum);

		if(!indiv) {
			for(int i = 0; i < events.size(); i++) {

				double[] poiBin = calculatePoisson(events.get(i), chr);
				double[] poiBinC = calculatePoissonC(events.get(i), chr);

				ArrayList<Overlap> overlaps = overlapMap.get(i);
                Event e = events.get(i);
				if(e.getList().get(chr) != null && e.getList().get(chr).size() != 0) {
					if(sum > 1) {
                        top[i] = Math.max(6, max(overlaps)); // max out of e.over[c] (noidea field)

						for(int k = 0; k < overlaps.size(); k++) {
							Overlap overlap = overlaps.get(k);
//                            System.out.println(chr + " = " + poiBin[Math.min(patients.size(), overlap.getCount())]);

							overlap.setScore(100f * (float) overlap.getCount() / (float) patients.size() / 100.0f);
							overlap.setPoiBin(poiBin[Math.min(patients.size(), overlap.getCount())]);
							overlap.setPoiBinC(poiBinC[Math.min(patients.size(), overlap.getCount())]);

                            writer.write(overlap.toPrint());

                            if(results.get(chr) == null) {
                                ArrayList<String[]> res = new ArrayList<String[]>();
                                res.add(overlap.toPrint());
                                results.put(chr, res);
                            } else{
                                results.get(chr).add(overlap.toPrint());
                            }
						}

					}
				}
			}
		}

		//plot by individual
		else {

			for(int i = 0; i < events.size(); i++) {
				double[] poiBin = calculatePoisson(events.get(i), chr);
				double[] poiBinC = calculatePoissonC(events.get(i), chr);

				ArrayList<Overlap> overlaps = overlapMap.get(i);
                Event e = events.get(i);

				// overlap
				if(e.getList().get(chr) != null && e.getList().get(chr).size() != 0 && sum > 1) {
                    top[i] = Math.max(6, max(overlaps)); // max out of e.over[c] (noidea field)

					for(int k = 0; k < overlaps.size(); k++) {
						Overlap overlap = overlaps.get(k);
						overlap.setScore(100f * (float) overlap.getCount() / (float) patients.size() / 100.0f);
						overlap.setPoiBin(poiBin[Math.min(patients.size(), overlap.getCount())]);
						overlap.setPoiBinC(poiBinC[Math.min(patients.size(), overlap.getCount())]);

                        writer.write(overlaps.get(k).toPrint());

                        if(results.get(chr) == null) {
                            ArrayList<String[]> res = new ArrayList<String[]>();
                            res.add(overlap.toPrint());
                            results.put(chr, res);
                        } else{
                            results.get(chr).add(overlap.toPrint());
                        }
					}
				}
			}
		}

		writer.close();

        plotIndividuals(chr, overlapMap, indiv, sum, title, top);

    }

    /**
     * Routine to provide a p-val for a given count in the region
     * Perform a Poisson Binomial test per chromosome using the proportion of event within the chromosome
     */
	public double[] calculatePoisson(Event e, String chr) {

		double mm = Math.ulp(1.0);

		double p[] = new double[patients.size()];


		double s = 0.0f;

		for(int i = 0; i < Common.CHROMOSOME_LIST.length; i++) {
			for(int k = 0; k < patients.size(); k++) {
				HashMap<String, Double> prop = e.getProps().get(Common.CHROMOSOME_LIST[i]);

				if(prop != null && prop.get(patients.get(k).getPatientID()) != null) {
					p[k] += prop.get(patients.get(k).getPatientID());
				}
			}

			s += maxi37.get(Common.CHROMOSOME_LIST[i]);
		}

		for(int i = 0; i < patients.size(); i++) {
			p[i] /= s;
		}

		try {
	    	RCaller caller = new RCaller();
	    	RCode code = new RCode();
	    	code.clear();

	      	caller.setRscriptExecutable("/usr/bin/Rscript");
	      	caller.setRExecutable("/usr/bin/R");

	      	int kk[] = new int[patients.size() + 1];
            for(int i = 0; i < kk.length; i++) {
                kk[i] = i;
            }

	      	code.addRCode("library(poibin)");
	      	code.addIntArray("kk", kk);
	      	code.addDoubleArray("pp", p);
		    code.addRCode("my.poi<-ppoibin(kk=kk, pp=pp, method=\"DFT-CF\")");
	        code.addRCode("my.all<-list(pb=my.poi)");

            System.out.println("kk = " + Arrays.toString(kk));
            System.out.println("pp = " + Arrays.toString(p));
	      	caller.setRCode(code);
	      	caller.runAndReturnResult("my.all");

	      	double[] results = caller.getParser().getAsDoubleArray("pb");
            System.out.println("pb = " + Arrays.toString(results));

            System.exit(0);

		    for (int i = 0; i < results.length; i++) {
                results[i] = Math.max(mm, 1 - results[i]);
            }

		    return results;
	    } catch (Exception ex) {
	      System.out.println(ex.toString());
	    }

	    return null;
	}

	/**
	 * Calculate the proportion of the genome with a given event for each patient
	 */
	public double[] calculatePoissonC(Event e, String chr) {
		double mm = Math.ulp(1.0);
		double pC[] = new double[patients.size()];

		for(int i = 0; i < patients.size(); i++) {
			// if there exists a patient for chromosome chr in props for the given event
			try {
				pC[i] = e.getProps().get(chr).get(patients.get(i).getPatientID()) / maxi37.get(chr);
			} catch(Exception ex) {
				// it doesn't..
			}
		}

		try {
	    	RCaller caller = new RCaller();
	    	RCode code = new RCode();

	      	caller.setRscriptExecutable("/usr/bin/Rscript");

            int kk[] = new int[patients.size() + 1];
            for(int i = 0; i < kk.length; i++) {
                kk[i] = i;
            }

		    code.addRCode("library(poibin)");
		    code.addIntArray("kk", kk);
	      	code.addDoubleArray("pp", pC);
		    code.addRCode("my.poi<-ppoibin(kk=kk, pp=pp, method=\"DFT-CF\")");
	        code.addRCode("my.all<-list(pb=my.poi)");

	      	caller.setRCode(code);
	      	caller.runAndReturnResult("my.all");

	      	double[] results = caller.getParser().getAsDoubleArray("pb");

		    for (int i = 0; i < results.length; i++) {
		    	results[i] = Math.max(mm, 1-results[i]);
		    }

		    return results;
	    } catch (Exception ex) {
	      System.out.println(ex.toString());
	    }

	    return null;
	}

	public ArrayList<Patient> makeIntervals(ArrayList<Patient> patients, int build) {
		return patients;
	}

    /**
     * A very badly written method for patient sorting (probably can be done via simple sort() call on patients)
     * @param patients
     * @return
     */
	public ArrayList<Patient> sortAberration(ArrayList<Patient> patients) {
		ArrayList<Patient> tempPatients = new ArrayList<Patient>();
		HashMap<String, Integer> minrec = new HashMap<String, Integer>();

		String prev = "";
		for(Patient p : patients) {
			if(!prev.equals(p.getPatientID())) {
				minrec.put(p.getPatientID(), p.getStart());
			} else {
				minrec.put(p.getPatientID(), Math.min(minrec.get(p.getPatientID()), p.getStart()));
			}

			prev = p.getPatientID();
		}

		while(minrec != null) {
			int min = Integer.MAX_VALUE;

			for (Map.Entry<String, Integer> entry : minrec.entrySet()) {
			    String key = entry.getKey();
			    Integer value = entry.getValue();

			    min = Math.min(value, min);
			}

			ArrayList<String> dela = new ArrayList<String>();
		    for(Patient p : patients) {
		    	if(p.getStart() == min && minrec.get(p.getPatientID()) != null) {
		    		dela.add(p.getPatientID());
		    	}
		    }

		    for(String s : dela) {
		    	for(Patient p : patients) {
		    		if(p.getPatientID().equals(s)) {
		    			tempPatients.add(p);
		    		}
		    	}

		    	if(minrec.get(s) != null) {
		    		minrec.remove(s);
		    	} else {
		    		System.out.println("There is a problem with your file. Most likely the same sample " + s + " has to overlaping events");
		    	}
		    }

			if(minrec.size() == 0) {
				minrec = null;
			}
		}

		return tempPatients;
	}

	/**
	 * Getter method for chromosomes
	 * @return A chromosome map
	 */
	public ChromosomeMap getGenes() {
		return genes;
	}

	/**
	 * Returns a map of cytoranges for every chromosome
	 * @return Returns a map of cytoranges for every chromosome
	 */
	public HashMap<String, ArrayList<Cytorange>> getCyto() {
		return cyto;
	}

	public HashMap<String, ArrayList<Cytoband>> getAnnot() {
		return annot;
	}

	public Events getEvents() {
		return events;
	}

    /**
     * Routine to plot individual chromosomes
     * @param chr
     * @param overlapMap
     * @param indiv
     * @param sum
     * @param title
     * @param top
     */
	public void plotIndividuals(String chr, HashMap<Integer, ArrayList<Overlap>> overlapMap, boolean indiv, int sum, String title, int[] top) {
		IndividualDiagram canvas = new IndividualDiagram(title, chr, 1000, 1000);

        canvas.plotCyto(annot.get(chr), maxi37.get(chr));

        if(sum > 1) {
            for(int i = 0; i < events.getEvents().size(); i++) {
                if(events.getEvents().get(i).getList(chr) != null && events.getEvents().get(i).getList(chr).size() != 0) {
                    canvas.plotLeg(events.getEvents().get(i).getType(), maxi37.get(chr), top[i], events.getEvents().size(), Canvas.ABERRATION_COLOURS[i]);
                }
            }
        }

        if(genes.get(chr) != null) {
            canvas.plotGenes(genes.get(chr), maxi37.get(chr));
        }

        if(showDGV && dgvdata.get(chr) != null) {
            canvas.plotDGV(dgvdata.get(chr), maxi37.get(chr));
        }

		if(!indiv) {

			for(int i = 0; i < events.getEvents().size(); i++) {
				Event e = events.getEvents().get(i);

				if(e.getList().get(chr) != null && e.getList().get(chr).size() != 0) {

					if(sum > 1) {
                        ArrayList<Overlap> overlaps = overlapMap.get(i);
						// TODO: if GWA_mod.Score or GWA_mod.GW_P or GWA_mod.C_P:

						if(showCScore || showGWScore || showScore)
                            canvas.plotScores(overlaps, maxi37.get(chr), top[i], showScore, showGWScore, showCScore);
						canvas.plotOverlap(overlaps, maxi37.get(chr), patients.size(), Canvas.ABERRATION_COLOURS[i]);
						// if(GWA_mod.Score or GWA_mod.GW_P or GWA_mod.C_P)
						// 	GWA_MOD.gv_plot_SCores
					}

					if(sum >= 1 && sum <= 2) {
						ArrayList<Patient> sortedAbberations = sortAberration(e.getList(chr));

						// GWA_mod.GV_plot_aberration
						canvas.plotAberration(sortedAbberations, (int) maxi37.get(chr), Canvas.ABERRATION_COLOURS[i]);

					}


				}
			}

			canvas.drawLabel(maxi37.get(chr) / 1000000, "Location (mb)");
		} else {
            // plot background lines
            for(int i = 0; i < events.getEvents().size(); i++) {
                Event e = events.getEvents().get(i);

                if(e.getList().get(chr) != null && e.getList().get(chr).size() != 0) {

                    if(sum > 1) {
                        ArrayList<Overlap> overlaps = overlapMap.get(i);
                        // TODO: if GWA_mod.Score or GWA_mod.GW_P or GWA_mod.C_P:

                        if(showCScore || showGWScore || showScore)
                            canvas.plotScores(overlaps, maxi37.get(chr), top[i], showScore, showGWScore, showCScore);
                        canvas.plotOverlap(overlaps, maxi37.get(chr), patients.size(), Canvas.ABERRATION_COLOURS[i]);
                    }
                }
            }

            for(Patient p : patients) {
                canvas.plotIndividualSample(p.getPatientID(), events, chr, maxi37.get(chr));
            }

            canvas.drawLabel(maxi37.get(chr) / 1000000, "Location (mb)");
		}

        // end session
        // if GWA_mod.Score etc:
        canvas.drawLegend(new String[]{"Score", "GW_P", "C_P"}, new Color[]{new Color(0.5f, 0f, 0f), new Color(0f, 0.5f, 0f), new Color(0f, 0f, 0.5f)});

		canvas.saveImage("png", OUTPUT_PATH + chr + ".png");
	}

    /**
     * Routine for drawing summary
     * @param indiv
     * @param sum
     * @param title
     */
    private void plotSummary(boolean indiv, int sum, String title) {
        SummaryDiagram summary = new SummaryDiagram(title, 3000);
        for(int i = 0; i < Common.CHROMOSOME_LIST.length; i++) {
            summary.plotCyto(Common.CHROMOSOME_LIST[i], annot.get(Common.CHROMOSOME_LIST[i]), maxi37.get(Common.CHROMOSOME_LIST[i]));

            if(!indiv) {
                HashMap<String, ArrayList<Patient>> sortedPatients = new HashMap<String, ArrayList<Patient>>();
                for(Event e : events.getEvents()) {
                    sortedPatients.put(e.getType(), sortAberration(e.getList(Common.CHROMOSOME_LIST[i])));
                }
                // GV_sort_aberration
                // if merge:
                //    data = GWA_mod.GV_sort_aberration

                summary.plotAberrations(Common.CHROMOSOME_LIST[i], maxi37.get(Common.CHROMOSOME_LIST[i]), events, sortedPatients);
            }
            else {
                summary.plotAberrationIndividual(Common.CHROMOSOME_LIST[i], maxi37.get(Common.CHROMOSOME_LIST[i]), events, patients, annot.get(Common.CHROMOSOME_LIST[i]).get(annot.get(Common.CHROMOSOME_LIST[i]).size()-1).getChromEnd());
            }
        }

        summary.plotLegend(events);

        summary.saveImage("png", OUTPUT_PATH + "summary" + ".png");
    }

	private int max(ArrayList<Overlap> overlaps) {
		int max = Integer.MIN_VALUE;

		for(Overlap o : overlaps) {
            max = Math.max(max, o.getCount());
		}

		return max;
	}

	/**
	 * --help
	 */
	public static void printUsage() {
		System.out.print("GWA_exec.py routine reads from the input file the aberrations.\n");
		System.out.print("It can plot a view by chromosome or genome-wide  showing for example UPD, Loss, or Gains\n");
		System.out.print("The default output is in Encapsulated Postscript (EPS) format but TIFF, JPG, PNG and PDF are available too.\n");
		System.out.print("Optionally overlap, cytoband names can be shown\n\n");
		System.out.print("Usage:\n");
		System.out.print("   GWA_exec.py [-b Build] [-c Chrom] [-f cfg.file] [-g gene.file] [-h] [-m] [-n] [-o] [-s] [-t] [-u] [-v] [-w] [--bin] [--jpg] [--pdf] [--png] [--tiff] [--ind] [--help] [--usage] [--mht] [--Score] [--GW_P] [--C_P]\n\n");
		System.out.print("Options:\n");
		System.out.print("   -b Build    Select the Build used for the Input location: 36 or 37 (default)\n");
		System.out.print("   -c Chrom    Shows only one single chromosome. By default all individual chromosomes are plotted\n");
		System.out.print("   -f file     Read this file for configuration rather than default.cfg\n");
		System.out.print("   -g file     Read this file to add genes in the chromosome picture\n");
		System.out.print("   -h          This help\n");
		System.out.print("     --help    \n");
		System.out.print("   -m          Merge the events in the summary in larger blocks\n");
		System.out.print("   -n          Show the name of the cytoband when large enough\n");
		System.out.print("   -p p1:..:pn Include Publications from Database of Genomic Variant data including property p1 to pn (default all=\"\")\n");
		System.out.print("   -s          Show the overlap of an aberration over samples\n");
		System.out.print("   -t          Optional Title (default None)\n");
		System.out.print("   -u          This help\n");
		System.out.print("     --usage   \n\n");
		System.out.print("   -v          Verbose mode, ie print debug information\n\n");
		System.out.print("   -w          Create an extra genome-view without overlap or cytobands names\n");
		System.out.print("   --bin size  Select the bin size for the counts in the overlap, default is 1, ie no bin\n");
		System.out.print("   --ind       Plot Summary individually, no sorting\n");
		System.out.print("   --mht       Plot selected scores along a genome-wide profile\n");
		System.out.print("   --Score     Plot Score\n");
		System.out.print("   --GW_P      Plot Genome-Wide Poisson-Binomial score\n");
		System.out.print("   --C_P       Plot Chromoomsal Poisson-Binomial score\n");
		System.out.print("   --loh       Highlight the LOH region (from Event name) with a black line\n");
		System.out.print("   --jpg       Select the JPEG format as output\n");
		System.out.print("   --pdf       Select the PDF format as output\n");
		System.out.print("   --png       Select the PNG format as output\n");
		System.out.print("   --tiff      Select the TIFF format as output\n");
		System.out.print("Example:\n");
		System.out.print("   GWA_exec.py -w -n file.xls \n");
		System.out.print("   GWA_exec.py --jpg -c chr2 -c chr4 --ind -s file.txt\n\n");
	}

    public boolean isShowCScore() {
        return showCScore;
    }

    public void setChromosomeWideScore(boolean showCScore) {
        this.showCScore = showCScore;
    }

    public boolean isShowGWScore() {
        return showGWScore;
    }

    public void setGenomeWideScore(boolean showGWScore) {
        this.showGWScore = showGWScore;
    }

    public boolean isShowDGV() {
        return showDGV;
    }

    public void setShowDGV(boolean showDGV) {
        this.showDGV = showDGV;
    }

    public boolean isShowScore() {
        return showScore;
    }

    public void setShowScore(boolean showScore) {
        this.showScore = showScore;
    }
}