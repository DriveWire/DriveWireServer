package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;


public class DWCmdServerShowTimers extends DWCommand {

    private final DWProtocol dwProto;

    DWCmdServerShowTimers(DWProtocol dwProto, DWCommand parent) {
        this.dwProto = dwProto;
        setParentCmd(parent);
    }

    public String getCommand() {
        return "timers";
    }


    public String getShortHelp() {
        return "Show instance timers";
    }


    public String getUsage() {
        return "dw server show timers";
    }

    public DWCommandResponse parse(String cmdline) {
        StringBuilder text = new StringBuilder();

        text.append("DriveWire instance timers (not shown == 0):\r\n\r\n");

        for (int i = 0; i < 256; i++) {
            if (dwProto.getTimers().getTimer((byte) i) > 0) {
                text.append(DWUtils.prettyTimer((byte) i)).append(": ").append(dwProto.getTimers().getTimer((byte) i)).append("\r\n");
            }

        }

        return (new DWCommandResponse(text.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
