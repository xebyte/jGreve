package greve;

import printer.CSV;
import printer.PrinterDriver;
import printer.XLS;

/**
 * Created by xebyte on 27/02/15.
 */
public class Printer {
    final public static int PRINTER_READ = 0;
    final public static int PRINTER_WRITE = 1;
    final public static int TYPE_CVS = 0;
    final public static int TYPE_XLS = 1;


    /**
     * Returns a driver of given type
     * @param filename
     * @param type
     * @return
     * @throws Exception
     */
    public static PrinterDriver getReader(String filename, int type) throws Exception {
        PrinterDriver p;
        switch(type) {
            case TYPE_CVS:
                p = new CSV(filename, PRINTER_READ);
                break;
            case TYPE_XLS:
                p = new XLS(filename, PRINTER_READ);
                break;
            default:
                throw new Exception("Unknown type");
        }

        return p;
    }

    /**
     * Returns a write driver of given type
     * @param filename
     * @param type
     * @return
     * @throws Exception
     */
    public static PrinterDriver getWriter(String filename, int type) throws Exception {
        PrinterDriver p;
        switch(type) {
            case TYPE_CVS:
                p = new CSV(filename, PRINTER_WRITE);
                break;
            case TYPE_XLS:
                p = new XLS(filename, PRINTER_WRITE);
                break;
            default:
                throw new Exception("Unknown type");
        }

        return p;
    }
}
