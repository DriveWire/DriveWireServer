package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DriveWireServer;

public class DWCmdServerStatus extends DWCommand {


    DWCmdServerStatus(DWCommand parent) {
        setParentCmd(parent);

    }

    public String getCommand() {
        return "status";
    }


    public String getShortHelp() {
        return "Show server status information";
    }


    public String getUsage() {
        return "dw server status";
    }

    public DWCommandResponse parse(String cmdline) {
        return (doServerStatus());
    }

    private DWCommandResponse doServerStatus() {
        String text = "";

        text += "DriveWire " + DriveWireServer.DWVersion + " status:\r\n\n";

        text += "Total memory:  " + Runtime.getRuntime().totalMemory() / 1024 + " KB";
        text += "\r\nFree memory:   " + Runtime.getRuntime().freeMemory() / 1024 + " KB";
        text += "\r\n";

        return (new DWCommandResponse(text));

    }

    public boolean validate(String cmdline) {
        return (true);
    }

}
