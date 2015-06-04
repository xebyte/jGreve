package printer;

import greve.Printer;

import java.io.*;

/**
 * Created by xebyte on 27/02/15.
 */
public class CSV implements PrinterDriver {
    private String file;
    private BufferedReader reader;
    private PrintWriter writer;
    private int rw;

    private int lineCount;

    /**
     * Returns a read or write object based on rw argument
     * @param filename
     * @param rw
     * @throws Exception
     */
    public CSV(String filename, int rw) throws Exception {
        file = filename;
        this.rw = rw;
        lineCount = 0;

        switch(rw) {
            case Printer.PRINTER_READ:
                reader = new BufferedReader(new FileReader(filename));
                break;
            case Printer.PRINTER_WRITE:
                writer = new PrintWriter(filename, "UTF-8");
                break;
            default:
                throw new Exception("Unknown mode");
        }
    }

    @Override
    public String[] readNextLine() throws IOException {
        String line = reader.readLine();
        if(line == null) return null;

        line = line.replaceAll("\\s+", " ").trim();

        return line.split(" ");
    }

    @Override
    public void write(String[] line) {
        String toWrite = "";

        if(lineCount > 0) toWrite += "\n";

        for(int i = 0; i < line.length; i++) {
            toWrite += line[i];
            if(i != line.length+1) toWrite += "\t";
        }

        writer.println(toWrite);
    }

    @Override
    public int getLineCount() {
        return lineCount;
    }

    @Override
    public void close() {

    }
}
