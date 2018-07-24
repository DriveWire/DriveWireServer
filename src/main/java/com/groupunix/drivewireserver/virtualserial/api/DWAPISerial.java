package com.groupunix.drivewireserver.virtualserial.api;

import com.fazecast.jSerialComm.SerialPort;
import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.exception.NoSuchPortException;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.virtualserial.DWVSerialPorts;

import java.io.IOException;

public class DWAPISerial {


    private String[] command;
    private DWVSerialPorts dwVSerialPorts;
    private int vport;

    public DWAPISerial(String[] cmd, DWVSerialPorts dwVSerialPorts, int vport) {
        this.vport = vport;
        this.dwVSerialPorts = dwVSerialPorts;
        this.setCommand(cmd);
    }

    public DWAPISerial(String[] cmdparts, int vport2) {
        // TODO Auto-generated constructor stub
    }

    public DWCommandResponse process() {
        if ((command.length > 2) && ((command.length & 1) == 1)) {
            if (command[1].equals("join")) {
                return (doCommandJoin(command));
            }
        }
        if (command.length > 2) {
            if (command[1].equals("show")) {
                return (doCommandShow(command[2]));
            }
        }
        else if (command.length > 1) {
            if (command[1].equals("devs")) {
                return (doCommandDevs());
            }
        }
        return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax Error");
    }


    private DWCommandResponse doCommandJoin(String[] cmd) {
        // validate
        String port = cmd[2];
        DWAPISerialPortDef spd = new DWAPISerialPortDef();

        for (int i = 3; i < cmd.length; i += 2) {
            String item = cmd[i];
            String val = cmd[i + 1];
            int valno = -1;
            try {
                valno = Integer.parseInt(val);
            }
            catch (NumberFormatException e) {

            }


            // bps rate
            if (item.equalsIgnoreCase("r") && isInteger(val)) {
                spd.setRate(valno);
            }
            // stop bits
            else if (item.equalsIgnoreCase("sb") && isInteger(val)) {
                if (valno == 1) {
                    spd.setStopbits(SerialPort.ONE_STOP_BIT);
                }
                else if (valno == 2) {
                    spd.setStopbits(SerialPort.TWO_STOP_BITS);
                }
                else if (valno == 5) {
                    spd.setStopbits(SerialPort.ONE_POINT_FIVE_STOP_BITS);
                }
                else {
                    return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error on arg sb (1,2 or 5 is valid)");
                }
            }
            // parity
            else if (item.equalsIgnoreCase("p") && val.length() == 1) {
                if (val.equalsIgnoreCase("N")) {
                    spd.setParity(SerialPort.NO_PARITY);
                }
                else if (val.equalsIgnoreCase("E")) {
                    spd.setParity(SerialPort.EVEN_PARITY);
                }
                else if (val.equalsIgnoreCase("O")) {
                    spd.setParity(SerialPort.ODD_PARITY);
                }
                else if (val.equalsIgnoreCase("M")) {
                    spd.setParity(SerialPort.MARK_PARITY);
                }
                else if (val.equalsIgnoreCase("S")) {
                    spd.setParity(SerialPort.SPACE_PARITY);
                }
                else {
                    return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error on arg p (N,E,O,M or S is valid)");
                }

            }
            // data bits
            else if (item.equalsIgnoreCase("db") && isInteger(val)) {
                if (valno >= 5 && valno <= 8) {
                    spd.setDatabits(valno);
                }
                else {
                    return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error on arg db (5,6,7 or 8 is valid)");
                }

            }
            else if (item.equalsIgnoreCase("fc")) {
                int fc = 0;

                for (byte b : val.getBytes()) {
                    if (b == 'r' || b == 'R') {
                        // JSerialComm doesn't distinguish between CTS/RTS IN/OUT, and handles separately
                        fc += SerialPort.FLOW_CONTROL_CTS_ENABLED;
                        fc += SerialPort.FLOW_CONTROL_RTS_ENABLED;
                    }
                    else if (b == 'x') {
                        fc += SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
                    }
                    else if (b == 'X') {
                        fc += SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED;
                    }
                    else if (b == 'n') {
                        fc += SerialPort.FLOW_CONTROL_DISABLED;
                    }
                    else {
                        return new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error on arg fc (R,r,X,x or n is valid)");
                    }

                }

                spd.setFlowcontrol(fc);
            }
        }

        try {
            SerialPort sp = SerialPort.getCommPort(port);


            if (sp == null) {
                return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTINVALID, "Invalid port");
            }

            sp.openPort(2000);


            spd.setParams(sp);

            // join em

            Thread inputT = new Thread(new Runnable() {


                public void run() {
                    boolean wanttodie = false;
                    dwVSerialPorts.markConnected(vport);

                    while (!wanttodie) {
                        int databyte = -1;

                        try {
                            databyte = dwVSerialPorts.getPortOutput(vport).read();
                            System.out.println("input: " + databyte);
                        }
                        catch (IOException e) {
                            wanttodie = true;
                        }
                        catch (DWPortNotValidException e) {
                            wanttodie = true;
                        }

                        if (databyte == -1) {
                            wanttodie = true;
                        }
                        else {
                            try {
                                sp.getOutputStream().write(databyte);
                            }
                            catch (IOException e) {
                                wanttodie = true;
                            }
                        }

                    }

                }
            });

            inputT.setDaemon(true);
            inputT.start();


            Thread outputT = new Thread(new Runnable() {


                public void run() {
                    boolean wanttodie = false;

                    while (!wanttodie) {
                        int databyte = -1;

                        try {

                            databyte = sp.getInputStream().read();
                            System.out.println("output: " + databyte);
                        }
                        catch (IOException e) {
                            wanttodie = true;
                        }

                        if (databyte != -1) {
                            try {
                                dwVSerialPorts.writeToCoco(vport, (byte) (databyte & 0xff));
                            }
                            catch (DWPortNotValidException e) {
                                wanttodie = true;
                            }
                        }

                    }

                }
            });

            outputT.setDaemon(true);
            outputT.start();

            return new DWCommandResponse("Connect to " + port);

        }
        catch (Exception e) {
            return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTERROR, e.getClass().getSimpleName());
        }
        catch (NoSuchPortException e) {
            return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTERROR, e.getClass().getSimpleName());
        }

    }

    private DWCommandResponse doCommandShow(String port) {
        String res = "";
        boolean ok = true;

        try {

            SerialPort sp = SerialPort.getCommPort(port);

            if (null == sp) {
                return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTINVALID, "No such port");
            }

            if (!(sp.openPort(2000))) {
                return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTERROR, "Unable to open");
            }


            res = sp.getBaudRate() + "|" + sp.getNumDataBits() + "|";

            if (sp.getParity() == SerialPort.EVEN_PARITY) {
                res += "E";
            }
            else if (sp.getParity() == SerialPort.ODD_PARITY) {
                res += "O";
            }
            else if (sp.getParity() == SerialPort.NO_PARITY) {
                res += "N";
            }
            else if (sp.getParity() == SerialPort.MARK_PARITY) {
                res += "M";
            }
            else {
                res += "S";
            }

            res += "|";

            if (sp.getNumStopBits() == SerialPort.ONE_STOP_BIT) {
                res += "1";
            }
            else if (sp.getNumStopBits() == SerialPort.TWO_STOP_BITS) {
                res += "2";
            }
            else {
                res += "5";
            }

            res += "|";

            if (((sp.getFlowControlSettings() & SerialPort.FLOW_CONTROL_RTS_ENABLED) == SerialPort.FLOW_CONTROL_RTS_ENABLED) ||
                    ((sp.getFlowControlSettings() & SerialPort.FLOW_CONTROL_CTS_ENABLED) == SerialPort.FLOW_CONTROL_CTS_ENABLED)) {
                // jSerialComm doesn't support RTSCTS_OUT / IN, set both
                res += "R";
                res += "r";
            }

            if ((sp.getFlowControlSettings() & SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED) == SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED) {
                res += "X";
            }


            if ((sp.getFlowControlSettings() & SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED) == SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED) {
                res += "x";
            }

            res += "|";

            if (sp.getCTS()) {
                res += "CTS ";
            }

            if (sp.getDSR()) {
                res += "DSR ";
            }

            res = res.trim();
        }
        catch (Exception e) {
            ok = false;
            res = e.toString();
        }

        if (ok) {
            return new DWCommandResponse(res);
        }
        else {
            return new DWCommandResponse(false, DWDefs.RC_SERIAL_PORTERROR, res);
        }
    }

    private DWCommandResponse doCommandDevs() {
        String res = "";

        for (String p : DriveWireServer.getAvailableSerialPorts()) {
            if (!res.equals("")) {
                res += "|";
            }
            res += p;
        }

        return new DWCommandResponse(res);
    }

    public String[] getCommand() {
        return command;
    }

    public void setCommand(String[] command) {
        this.command = command;
    }


    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }


}
