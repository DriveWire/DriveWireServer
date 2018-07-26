package com.groupunix.drivewireserver.dwprotocolhandler;


import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class DWSerialReader implements SerialPortDataListener {
    static final Logger logger = Logger.getLogger(com.groupunix.drivewireserver.dwprotocolhandler.DWSerialReader.class);

    private final ArrayBlockingQueue<Byte> queue;
    private final InputStream in;
    private final SerialPort serialPort;
    private boolean wanttodie = false;

    DWSerialReader(SerialPort serialPort, ArrayBlockingQueue<Byte> q) {

        this.queue = q;
        this.serialPort = serialPort;
        this.in = serialPort.getInputStream();
    }


    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    public void serialEvent(SerialPortEvent arg0) {
        int data;

        try {
            while (!wanttodie && this.serialPort.bytesAvailable() > 0) {
                data = in.read();
                if (data < 0) {
                    logger.warn("Read error.");
                }
                else {
                    queue.add((byte) data);
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        this.wanttodie = true;

    }


}
