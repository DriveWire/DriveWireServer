package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class DWCmdPortShow extends DWCommand {

    private final DWVSerialProtocol dwProto;

    DWCmdPortShow(DWVSerialProtocol dwProtocol, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProtocol;
    }

    public String getCommand() {
        return "show";
    }


    public String getShortHelp() {
        return "Show port status";
    }


    public String getUsage() {
        return "dw port show";
    }

    public DWCommandResponse parse(String cmdline) {
        return (doPortShow());
    }


    private DWCommandResponse doPortShow() {
        StringBuilder text = new StringBuilder();

        text.append("\r\nCurrent port status:\r\n\n");

        for (int i = 0; i < dwProto.getVPorts().getMaxPorts(); i++) {

            text.append(String.format("%6s", dwProto.getVPorts().prettyPort(i)));

            try {


                if (dwProto.getVPorts().isOpen(i)) {
                    text.append(String.format(" %-8s", "open(" + dwProto.getVPorts().getOpen(i) + ")"));

                    text.append(String.format(" %-11s", "PD.INT=" + dwProto.getVPorts().getPD_INT(i)));
                    text.append(String.format(" %-11s", "PD.QUT=" + dwProto.getVPorts().getPD_QUT(i)));
                    text.append(String.format(" %-9s", "buf: " + dwProto.getVPorts().bytesWaiting(i)));

                }
                else {
                    text.append(" closed");
                }


                if (dwProto.getVPorts().getUtilMode(i) != DWDefs.UTILMODE_UNSET) {
                    text.append(DWUtils.prettyUtilMode(dwProto.getVPorts().getUtilMode(i)));
                }

                //text += " " + DWProtocolHandler.byteArrayToHexString(DWVSerialPorts.getDD(i));
            }
            catch (DWPortNotValidException e) {
                text.append(" Error: ").append(e.getMessage());
            }


            text.append("\r\n");
        }

        return (new DWCommandResponse(text.toString()));

    }

    public boolean validate(String cmdline) {
        return (true);
    }

}
