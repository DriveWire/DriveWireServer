package com.groupunix.drivewireserver.virtualserial.api;


import com.fazecast.jSerialComm.SerialPort;
import com.groupunix.drivewireserver.exception.NoSuchPortException;
import com.groupunix.drivewireserver.exception.PortInUseException;
import com.groupunix.drivewireserver.exception.UnsupportedCommOperationException;


public class DWAPISerialPortDef {
    private int rate = -1;
    private int databits = -1;
    private int stopbits = -1;
    private int parity = -1;
    private int flowcontrol = -1;

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getDatabits() {
        return databits;
    }

    public void setDatabits(int databits) {
        this.databits = databits;
    }

    public int getStopbits() {
        return stopbits;
    }

    public void setStopbits(int stopbits) {
        this.stopbits = stopbits;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getFlowcontrol() {
        return flowcontrol;
    }

    public void setFlowcontrol(int flowcontrol) {
        this.flowcontrol = flowcontrol;
    }

    public void setParams(SerialPort sp) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {



        if (this.rate == -1) {
            this.rate = sp.getBaudRate();
        }

        if (this.databits == -1) {
            this.databits = sp.getNumDataBits();
        }

        if (this.stopbits == -1) {
            this.stopbits = sp.getNumStopBits();
        }

        if (this.parity == -1) {
            this.parity = sp.getParity();
        }

        sp.setComPortParameters(rate, databits, stopbits, parity);

        if (this.flowcontrol > -1) {
            sp.setFlowControl(this.flowcontrol);
        }


    }
}
