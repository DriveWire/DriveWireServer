package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;
import com.groupunix.drivewireserver.virtualserial.DWVPortListenerPool;

public class DWCmdNetShow extends DWCommand {

    private final DWVSerialProtocol dwProto;

    DWCmdNetShow(DWVSerialProtocol dwProtocol, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProtocol;
    }

    public String getCommand() {
        return "show";
    }


    public String getShortHelp() {
        return "Show networking status";
    }


    public String getUsage() {
        return "dw net show";
    }

    public DWCommandResponse parse(String cmdline) {
        return (doNetShow());
    }


    private DWCommandResponse doNetShow() {
        StringBuilder text = new StringBuilder();

        text.append("\r\nDriveWire Network Connections:\r\n\n");


        for (int i = 0; i < DWVPortListenerPool.MAX_CONN; i++) {


            try {
                text.append("Connection ").append(i).append(": ").append(dwProto.getVPorts().getListenerPool().getConn(i).socket().getInetAddress().getHostName()).append(":").append(dwProto.getVPorts().getListenerPool().getConn(i).socket().getPort()).append(" (connected to port ").append(dwProto.getVPorts().prettyPort(dwProto.getVPorts().getListenerPool().getConnPort(i))).append(")\r\n");
            }
            catch (DWConnectionNotValidException e) {
                // text += e.getMessage();
            }
        }

        text.append("\r\n");

        for (int i = 0; i < DWVPortListenerPool.MAX_LISTEN; i++) {
            if (dwProto.getVPorts().getListenerPool().getListener(i) != null) {
                text.append("Listener ").append(i).append(": TCP port ").append(dwProto.getVPorts().getListenerPool().getListener(i).socket().getLocalPort()).append(" (control port ").append(dwProto.getVPorts().prettyPort(dwProto.getVPorts().getListenerPool().getListenerPort(i))).append(")\r\n");
            }
        }

        return (new DWCommandResponse(text.toString()));

    }


    public boolean validate(String cmdline) {
        return (true);
    }

}
