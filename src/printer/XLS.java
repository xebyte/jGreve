package printer;

import greve.Printer;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;

/**
 * Created by xebyte on 27/02/15.
 */
public class XLS implements PrinterDriver {
    private String file;
    private int rw;
    private Workbook read;
    private WritableWorkbook write;
    private WritableSheet sheet;

    private int curRow;

    public XLS(String filename, int rw) throws Exception {
        file = filename;
        this.rw = rw;

        curRow = 0;

        switch(rw) {
            case Printer.PRINTER_READ:
                WorkbookSettings settings = new WorkbookSettings();
                settings.setSuppressWarnings(true);
                read = Workbook.getWorkbook(new File(filename), settings);
                break;
            case Printer.PRINTER_WRITE:
                write = Workbook.createWorkbook(new File(filename));
                sheet = write.createSheet("GREVE output", 0);
                break;
            default:
                throw new Exception("Unknown mode");
        }
    }

    @Override
    public String[] readNextLine() {
        if(curRow >= read.getSheet(0).getRows()) {
            read.close();
            return null;
        }
        String[] line = new String[read.getSheet(0).getColumns()];
        for(int i = 0; i < read.getSheet(0).getColumns(); i++) {
            if (read.getSheet(0).getCell(i, curRow).getType() == CellType.LABEL)
            {
                LabelCell lc = (LabelCell) read.getSheet(0).getCell(i, curRow);
                line[i] = lc.getString();
            } else if(read.getSheet(0).getCell(i, curRow).getType() == CellType.NUMBER) {
                NumberCell nc = (NumberCell) read.getSheet(0).getCell(i, curRow);
                line[i] = "" + nc.getValue();
            } else {
                line[i] = read.getSheet(0).getCell(i, curRow).getContents();
            }
        }

        curRow++;

        return line;
    }

    @Override
    public void write(String[] line) throws WriteException {
        for(int i = 0; i < line.length; i++) {
            try {
                double num = Double.parseDouble(line[i]);
                Number cell = new Number(i, curRow, num);
                sheet.addCell(cell);
            } catch(Exception e) {
                Label cell = new Label(i, curRow, line[i]);
                sheet.addCell(cell);
            }
        }

        curRow++;
    }

    @Override
    public int getLineCount() {
        return curRow;
    }

    public void close() throws IOException, WriteException {
        write.write();
        write.close();
    }
}
