package printer;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.IOException;

/**
 * Created by xebyte on 27/02/15.
 */
public interface PrinterDriver {
    public String[] readNextLine() throws IOException;
    public void write(String[] line) throws WriteException;
    public void close() throws IOException, WriteException;
    public int getLineCount();
}
