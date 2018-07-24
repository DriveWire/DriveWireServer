package com.groupunix.drivewireserver.dwcommands;


import com.fazecast.jSerialComm.SerialPort;
import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;


public class DWCmdServerShowSerial extends DWCommand {


    DWCmdServerShowSerial(DWProtocol dwProto, DWCommand parent) {

        setParentCmd(parent);
    }

    public String getCommand() {
        return "serial";
    }


    public String getShortHelp() {
        return "Show serial device information";
    }


    public String getUsage() {
        return "dw server show serial";
    }

    public DWCommandResponse parse(String cmdline) {
        String text = new String();

        text += "Server serial devices:\r\n\r\n";

        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            try {
                text += serialPort.getSystemPortName() + "  ";

                serialPort.openPort(2000);

                text += serialPort.getBaudRate() + " bps  ";
                text += serialPort.getNumDataBits();


                switch (serialPort.getParity()) {
                    case SerialPort.NO_PARITY:
                        text += "N";
                        break;

                    case SerialPort.EVEN_PARITY:
                        text += "E";
                        break;

                    case SerialPort.MARK_PARITY:
                        text += "M";
                        break;

                    case SerialPort.ODD_PARITY:
                        text += "O";
                        break;

                    case SerialPort.SPACE_PARITY:
                        text += "S";
                        break;

                }


                text += serialPort.getNumStopBits();

                if (serialPort.getFlowControlSettings() == SerialPort.FLOW_CONTROL_DISABLED) {
                    text += "  No flow control  ";
                }
                else {
                    text += "  ";

                    if ((serialPort.getFlowControlSettings() & SerialPort.FLOW_CONTROL_RTS_ENABLED) == SerialPort.FLOW_CONTROL_RTS_ENABLED) {
                        text += "RTS  ";
                    }

                    if ((serialPort.getFlowControlSettings() & SerialPort.FLOW_CONTROL_CTS_ENABLED) == SerialPort.FLOW_CONTROL_CTS_ENABLED) {
                        text += "CTS  ";
                    }

                    if ((serialPort.getFlowControlSettings() & SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED) == SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED) {
                        text += "In: XOn/XOff  ";
                    }

                    if ((serialPort.getFlowControlSettings() & SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED) == SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED) {
                        text += "Out: XOn/XOff  ";
                    }
                }


                text += " CTS:" + yn(serialPort.getCTS());

                text += " DSR:" + yn(serialPort.getDSR());


                text += "\r\n";

                serialPort.closePort();


            }
            catch (Exception e) {
                return (new DWCommandResponse(false, DWDefs.RC_SERVER_IO_EXCEPTION, "While gathering serial port info: " + e.getMessage()));
            }
        }


        return (new DWCommandResponse(text));
    }

    private String yn(boolean cd) {
        if (cd) {
            return "Y";
        }

        return "n";
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
