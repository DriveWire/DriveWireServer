package com.groupunix.drivewireserver.dwprotocolhandler;


import com.fazecast.jSerialComm.SerialPort;
import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.exception.NoSuchPortException;
import com.groupunix.drivewireserver.exception.PortInUseException;
import com.groupunix.drivewireserver.exception.UnsupportedCommOperationException;
import com.groupunix.drivewireserver.dwexceptions.DWCommTimeOutException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DWSerialDevice implements DWProtocolDevice {
    private static final Logger logger = Logger.getLogger("DWServer.DWSerialDevice");

    private SerialPort serialPort = null;

    private boolean bytelog = false;
    private String device;
    private DWProtocol dwProto;
    private boolean DATurboMode = false;
    private boolean xorinput = false;
    private long WriteByteDelay = 0;
    private long ReadByteWait = 200;
    private byte[] prefix;
    private long readtime;

    private ArrayBlockingQueue<Byte> queue;

    private DWSerialReader evtlistener;

    private boolean ProtocolFlipOutputBits;

    private boolean ProtocolResponsePrefix;


    public DWSerialDevice(DWProtocol dwProto) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException {

        this.dwProto = dwProto;

        this.device = dwProto.getConfig().getString("SerialDevice");

        prefix = new byte[1];
        //prefix[0] = (byte) 0xFF;
        prefix[0] = (byte) 0xC0;

        logger.debug("init " + device + " for handler #" + dwProto.getHandlerNo() + " (logging bytes: " + bytelog + "  xorinput: " + xorinput + ")");

        connect(device);

    }


    public boolean connected() {
        if (this.serialPort != null) {
            logger.debug("Serial port connected.");
            return (true);
        }
        else {
            logger.debug("Serial port not connected.");
            return (false);
        }
    }


    public void close() {

        logger.debug("Closing serial port");
        if (this.serialPort != null) {
            logger.debug("closing serial device " + device + " in handler #" + dwProto.getHandlerNo());

            if (this.evtlistener != null) {
                this.evtlistener.shutdown();
                serialPort.removeDataListener();
            }

            serialPort.closePort();
            serialPort = null;

        }
        else {
            logger.debug("Serial port already null.");
        }


    }


    public void shutdown() {
        this.close();

    }


    public void reconnect() throws UnsupportedCommOperationException, IOException, TooManyListenersException {
        logger.debug("Starting reconnect");
        if (this.serialPort != null) {
            logger.debug("Serial port not null in reconnect.");

            if(!(serialPort.isOpen())) {
                logger.debug("Port is currently closed.");
            }
            else {
                logger.debug("Port is currently open.");
            }

            setSerialParams();

            logger.debug("Opening port.");
            serialPort.openPort(2000);

            if (this.evtlistener != null) {
                logger.debug("Removing existing event listener.");
                this.serialPort.removeDataListener();
            }

            this.queue = new ArrayBlockingQueue<Byte>(512);

            logger.debug("Starting serial reader");
            this.evtlistener = new DWSerialReader(serialPort, queue);


            logger.debug("Adding data listener");
            if(!(serialPort.addDataListener(this.evtlistener))) {
                logger.warn("Unable to add data listener!");
            }
            logger.debug("Done with reconnect");
        }
        else {
            logger.debug("Reconnect failed - serial port is null.");
        }
    }

    private void connect(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, TooManyListenersException {
        logger.debug("attempting to open device '" + portName + "'");

        serialPort = SerialPort.getCommPort(portName);


        if (null == serialPort) {
            logger.error("The operating system says '" + portName + "' is not a serial port!");
            throw new NoSuchPortException();
        }
        else {
            logger.debug("We have a serial device in connect");
        }

        logger.debug("Calling reconnect.");
        reconnect();
        logger.info("opened serial device " + portName);


    }


    private void setSerialParams() throws UnsupportedCommOperationException {
        logger.debug("Setting serial params");
        int rate;
        int parity = 0;
        int stopbits = 1;
        int databits = 8;

        // mode vars

        this.WriteByteDelay = this.dwProto.getConfig().getLong("WriteByteDelay", 0);
        this.ReadByteWait = this.dwProto.getConfig().getLong("ReadByteWait", 200);
        this.ProtocolFlipOutputBits = this.dwProto.getConfig().getBoolean("ProtocolFlipOutputBits", false);
        this.ProtocolResponsePrefix = dwProto.getConfig().getBoolean("ProtocolResponsePrefix", false);
        this.xorinput = dwProto.getConfig().getBoolean("ProtocolXORInputBits", false);
        this.bytelog = dwProto.getConfig().getBoolean("LogDeviceBytes", false);


        // serial params

        rate = dwProto.getConfig().getInt("SerialRate", 115200);

        logger.debug("Rate set to: " + rate);

        if (dwProto.getConfig().containsKey("SerialStopbits")) {
            String sb = dwProto.getConfig().getString("SerialStopbits");
            logger.debug("Setting stop bits to: " + sb);

            if (sb.equals("1"))
                stopbits = SerialPort.ONE_STOP_BIT;
            else if (sb.equals("1.5"))
                stopbits = SerialPort.ONE_POINT_FIVE_STOP_BITS;
            else if (sb.equals("2"))
                stopbits = SerialPort.TWO_STOP_BITS;

        }

        if (dwProto.getConfig().containsKey("SerialParity")) {
            String p = dwProto.getConfig().getString("SerialParity");

            logger.debug("Setting parity to: " + p);

            if (p.equals("none"))
                parity = SerialPort.NO_PARITY;
            else if (p.equals("even"))
                parity = SerialPort.EVEN_PARITY;
            else if (p.equals("odd"))
                parity = SerialPort.ODD_PARITY;
            else if (p.equals("mark"))
                parity = SerialPort.MARK_PARITY;
            else if (p.equals("space"))
                parity = SerialPort.SPACE_PARITY;


        }

        int flow = SerialPort.FLOW_CONTROL_DISABLED;

        if (dwProto.getConfig().getBoolean("SerialFlowControl_RTSCTS_IN", false)) {
            // jSerialComm doesn't support IN/OUT on RTS/CTS, just enable both
            flow = flow | SerialPort.FLOW_CONTROL_CTS_ENABLED;
            flow = flow | SerialPort.FLOW_CONTROL_RTS_ENABLED;
            logger.debug("Setting RTSCTS_IN");
        }

        if (dwProto.getConfig().getBoolean("SerialFlowControl_RTSCTS_OUT", false)) {
            // jSerialComm doesn't support IN/OUT on RTS/CTS, just enable both
            flow = flow | SerialPort.FLOW_CONTROL_CTS_ENABLED;
            flow = flow | SerialPort.FLOW_CONTROL_RTS_ENABLED;
            logger.debug("Setting RTSCTS_OUT");
        }
        if (dwProto.getConfig().getBoolean("SerialFlowControl_XONXOFF_IN", false)) {
            flow = flow | SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED;
            logger.debug("Setting XONXOFF_IN");
        }

        if (dwProto.getConfig().getBoolean("SerialFlowControl_XONXOFF_OUT", false)) {
            flow = flow | SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
            logger.debug("Setting XONXOFF_OUT");
        }


        logger.debug("setting port params to " + rate + " " + databits + ":" + parity + ":" + stopbits);
        serialPort.setComPortParameters(rate, databits, stopbits, parity);

        if (dwProto.getConfig().containsKey("SerialDTR")) {
            boolean isDTR = dwProto.getConfig().getBoolean("SerialDTR", false);
            if(isDTR) {
                logger.debug("Setting DTR");
                flow = flow | SerialPort.FLOW_CONTROL_DTR_ENABLED;
            }
            logger.debug("setting port DTR to " + isDTR);
        }

        if (dwProto.getConfig().containsKey("SerialRTS")) {
            boolean isRTS = dwProto.getConfig().getBoolean("SerialRTS", false);
            int flowControlSettings = serialPort.getFlowControlSettings();
            if(isRTS) {
                logger.debug("Setting RTS");
                flow = flow | SerialPort.FLOW_CONTROL_RTS_ENABLED;
            }
            logger.debug("setting port RTS to " + isRTS);
        }

        logger.debug("Setting flow control to: " + flow);
        serialPort.setFlowControl(flow);


    }


    public int getRate() {
        if (this.serialPort != null) {
            logger.debug("getRate returned: " + this.serialPort.getBaudRate());
            return (this.serialPort.getBaudRate());
        }
        else {
            logger.debug("Can't getRate because serial port is null");
            return -1;
        }
    }


    public void comWrite(byte[] data, int len, boolean pfix) {
        try {
            if (this.ProtocolFlipOutputBits || this.DATurboMode) {
                data = DWUtils.reverseByteArray(data);
            }


            if (this.WriteByteDelay > 0) {
                for (int i = 0; i < len; i++) {
                    comWrite1(data[i], pfix);
                }
            }
            else {
                if (pfix && (this.ProtocolResponsePrefix || this.DATurboMode)) {
                    logger.debug("Reversing in comWrite");
                    byte[] out = new byte[this.prefix.length + len];
                    System.arraycopy(this.prefix, 0, out, 0, this.prefix.length);
                    System.arraycopy(data, 0, out, this.prefix.length, len);

                    serialPort.getOutputStream().write(out);
                }
                else {
                    serialPort.getOutputStream().write(data, 0, len);
                }


                // extreme cases only

                if (bytelog) {
                    String tmps = new String();

                    for (int i = 0; i < len; i++) {
                        tmps += " " + (int) (data[i] & 0xFF);
                    }

                    logger.debug("WRITE " + len + ":" + tmps);

                }
            }
        }
        catch (IOException e) {
            // problem with comm port, bail out
            logger.error(e.getMessage());

        }
    }


    public void comWrite1(int data, boolean pfix) {

        try {
            if (this.ProtocolFlipOutputBits || this.DATurboMode) {
                logger.debug("Flipping out bits");
                data = DWUtils.reverseByte(data);
            }

            if (this.WriteByteDelay > 0) {
                try {
                    Thread.sleep(this.WriteByteDelay);
                }
                catch (InterruptedException e) {
                    logger.warn("interrupted during writebytedelay");
                }
            }

            if (pfix && (this.ProtocolResponsePrefix || this.DATurboMode)) {
                byte[] out = new byte[this.prefix.length + 1];
                out[out.length - 1] = (byte) data;
                System.arraycopy(this.prefix, 0, out, 0, this.prefix.length);

                serialPort.getOutputStream().write(out);
            }
            else {
                serialPort.getOutputStream().write((byte) data);
            }

            if (bytelog)
                logger.debug("WRITE1: " + (0xFF & data));

        }
        catch (IOException e) {
            // problem with comm port, bail out
            logger.error(e.getMessage());

        }
    }


    public byte[] comRead(int len) throws IOException, DWCommTimeOutException {
        byte[] buf = new byte[len];

        for (int i = 0; i < len; i++) {
            buf[i] = (byte) comRead1(true, false);
        }

        if (this.bytelog) {
            String tmps = new String();

            for (int i = 0; i < buf.length; i++) {
                tmps += " " + (int) (buf[i] & 0xFF);
            }

            logger.debug("READ " + len + ": " + tmps);
        }

        return (buf);
    }


    public int comRead1(boolean timeout) throws IOException, DWCommTimeOutException {
        return comRead1(timeout, true);
    }


    public int comRead1(boolean timeout, boolean blog) throws IOException, DWCommTimeOutException {
        int res = -1;

        try {
            while ((res == -1) && (this.serialPort != null)) {
                long starttime = System.currentTimeMillis();
                Byte read = queue.poll(this.ReadByteWait, TimeUnit.MILLISECONDS);
                this.readtime += System.currentTimeMillis() - starttime;

                if (read != null)
                    res = 0xFF & read;
                else if (timeout) {
                    throw (new DWCommTimeOutException("No data in " + this.ReadByteWait + " ms"));
                }

            }
        }
        catch (InterruptedException e) {
            logger.debug("interrupted in serial read");
        }

        if (this.xorinput)
            res = res ^ 0xFF;

        if (blog && this.bytelog)
            logger.debug("READ1: " + res);

        return res;
    }


    public String getDeviceName() {
        logger.debug("In getDeviceName");
        if (this.serialPort != null) {
            logger.debug("Device name: " + this.serialPort.getSystemPortName());
            return (this.serialPort.getSystemPortName());
        }
        else {
            logger.debug("Can't get device name, null serialPort");
            return null;
        }
    }


    public String getDeviceType() {
        return ("serial");
    }


    public void enableDATurbo() throws UnsupportedCommOperationException {
        logger.debug("Calling enableDATurbo");
        // valid port, not already turbo
        if ((this.serialPort != null) && !this.DATurboMode) {
            logger.debug("Setting com port parameters");
            // change to 2x instead of hardcoded
            if ((this.serialPort.getBaudRate() >= DWDefs.COM_MIN_DATURBO_RATE) && ((this.serialPort.getBaudRate() <= DWDefs.COM_MAX_DATURBO_RATE))) {
                this.serialPort.setComPortParameters(this.serialPort.getBaudRate() * 2, 8, 2, 0);
                this.DATurboMode = true;
            }
        }
        else {
            logger.debug("Either serial port null or already DATurbo mode");
        }
    }

    public long getReadtime() {
        return this.readtime;
    }

    public void resetReadtime() {
        this.readtime = 0;
    }


    public SerialPort getSerialPort() {
        return this.serialPort;
    }


    public String getClient() {
        return null;
    }


    public InputStream getInputStream() {
        logger.debug("Calling to get input stream");
        return new InputStream() {
            private boolean endReached = false;


            public int read() throws IOException {
                try {
                    if (endReached)
                        return -1;

                    Byte value = queue.take();
                    if (value == null) {

                        throw new IOException(
                                "Timeout while reading from the queue-based input stream");
                    }

                    endReached = (value.intValue() == -1);
                    return value;
                }
                catch (InterruptedException ie) {
                    throw new IOException(
                            "Interruption occurred while writing in the queue");
                }
            }
        };
    }
}
