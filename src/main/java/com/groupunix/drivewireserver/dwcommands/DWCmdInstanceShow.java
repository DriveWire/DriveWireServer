package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdInstanceShow extends DWCommand {

    DWCmdInstanceShow(DWProtocol dwProto, DWCommand parent) {
        setParentCmd(parent);

    }

    public String getCommand() {
        return "show";
    }


    public String getShortHelp() {
        return "Show instance status";
    }


    public String getUsage() {
        return "dw instance show";
    }

    public DWCommandResponse parse(String cmdline) {
        StringBuilder text = new StringBuilder();

        text.append("DriveWire protocol handler instances:\r\n\n");

        for (int i = 0; i < DriveWireServer.getNumHandlers(); i++) {
            text.append("#").append(i).append("  (");


            if (DriveWireServer.getHandler(i).isDying()) {
                text.append("Dying..)   ");
            }
            else if (DriveWireServer.getHandler(i).isReady()) {
                text.append("Ready)     ");
            }
            else if (DriveWireServer.getHandler(i).isStarted()) {
                text.append("Starting)  ");
            }
            else {
                text.append("Not ready) ");
            }

            if (DriveWireServer.getHandler(i) == null) {
                text.append(" Null (?)\r\n");
            }
            else {
                String proto = DriveWireServer.getHandler(i).getConfig().getString("Protocol", "DriveWire");
                text.append(String.format("Proto: %-11s", proto));

                String dtype = DriveWireServer.getHandler(i).getConfig().getString("DeviceType", "Unknown");


                if (dtype.equals("serial") || proto.equals("VModem")) {
                    text.append(String.format("Type: %-11s", "serial"));
                    text.append(" Dev: ").append(DriveWireServer.getHandler(i).getConfig().getString("SerialDevice", "Unknown"));
                }
                else if (dtype.equals("tcp-server")) {
                    text.append(String.format("Type: %-11s", "tcp-server"));
                    text.append("Port: ").append(DriveWireServer.getHandler(i).getConfig().getString("TCPServerPort", "Unknown"));
                }
                else if (dtype.equals("tcp-client")) {
                    text.append(String.format("Type: %-11s", dtype));
                    text.append("Host: ").append(DriveWireServer.getHandler(i).getConfig().getString("TCPClientHost", "Unknown")).append(":").append(DriveWireServer.getHandler(i).getConfig().getString("TCPClientPort", "Unknown"));
                }


                text.append("\r\n");
            }

        }

        return (new DWCommandResponse(text.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
