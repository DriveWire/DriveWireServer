package com.groupunix.drivewireserver.dwcommands;


import com.fazecast.jSerialComm.SerialPort;
import com.groupunix.drivewireserver.DWDefs;


public class DWCmdServerShowSerial extends DWCommand {


    DWCmdServerShowSerial(DWCommand parent) {

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
        StringBuilder text = new StringBuilder();

        text.append("Server serial devices:\r\n\r\n");

        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            try {
                text.append(serialPort.getSystemPortName()).append("  ");

                serialPort.openPort(2000);

                text.append(serialPort.getBaudRate()).append(" bps  ");
                text.append(serialPort.getNumDataBits());


                switch (serialPort.getParity()) {
                    case SerialPort.NO_PARITY:
                        text.append("N");
                        break;

                    case SerialPort.EVEN_PARITY:
                        text.append("E");
                        break;

                    case SerialPort.MARK_PARITY:
                        text.append("M");
                        break;

                    case SerialPort.ODD_PARITY:
                        text.append("O");
                        break;

                    case SerialPort.SPACE_PARITY:
                        text.append("S");
                        break;

                }


                text.append(serialPort.getNumStopBits());

                if (serialPort.getFlowControlSettings() == SerialPort.FLOW_CONTROL_DISABLED) {
                    text.append("  No flow control  ");
                }
                else {
                    text.append("  ");

                    if ((serialPort.getFlowControlSettings() & SerialPort.FLOW_CONTROL_RTS_ENABLED) == SerialPort.FLOW_CONTROL_RTS_ENABLED) {
                        text.append("RTS  ");
                    }

                    if ((serialPort.getFlowControlSettings() & SerialPort.FLOW_CONTROL_CTS_ENABLED) == SerialPort.FLOW_CONTROL_CTS_ENABLED) {
                        text.append("CTS  ");
                    }

                    if ((serialPort.getFlowControlSettings() & SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED) == SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED) {
                        text.append("In: XOn/XOff  ");
                    }

                    if ((serialPort.getFlowControlSettings() & SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED) == SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED) {
                        text.append("Out: XOn/XOff  ");
                    }
                }


                text.append(" CTS:").append(yn(serialPort.getCTS()));

                text.append(" DSR:").append(yn(serialPort.getDSR()));


                text.append("\r\n");

                serialPort.closePort();


            }
            catch (Exception e) {
                return (new DWCommandResponse(false, DWDefs.RC_SERVER_IO_EXCEPTION, "While gathering serial port info: " + e.getMessage()));
            }
        }


        return (new DWCommandResponse(text.toString()));
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
