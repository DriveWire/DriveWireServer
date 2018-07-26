package com.groupunix.drivewireserver.virtualprinter;

import com.groupunix.drivewireserver.dwexceptions.DWPrinterFileError;
import com.groupunix.drivewireserver.dwexceptions.DWPrinterNotDefinedException;

import java.io.IOException;

public interface DWVPrinterDriver {
    void flush() throws IOException, DWPrinterFileError, DWPrinterNotDefinedException;

    void addByte(byte data) throws IOException;

    String getDriverName();

    String getPrinterName();
}
