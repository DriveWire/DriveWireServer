package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;

import java.util.ArrayList;

public class DWCmdLogShow extends DWCommand {


    DWCmdLogShow(DWCommand parent) {
        setParentCmd(parent);
    }

    public String getCommand() {
        return "show";
    }


    public String getShortHelp() {
        return "Show last 20 (or #) log entries";
    }


    public String getUsage() {
        return "dw log show [#]";
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (doShowLog("20"));
        }
        return (doShowLog(cmdline));
    }


    private DWCommandResponse doShowLog(String strlines) {
        StringBuilder text = new StringBuilder();

        try {
            int lines = Integer.parseInt(strlines);

            text.append("\r\nDriveWire Server Log (").append(DriveWireServer.getLogEventsSize()).append(" events in buffer):\r\n\n");

            ArrayList<String> loglines = DriveWireServer.getLogEvents(lines);

            for (String logline : loglines) {
                text.append(logline);

            }

            return (new DWCommandResponse(text.toString()));

        }
        catch (NumberFormatException e) {
            return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error: non numeric # of log lines"));
        }

    }


    public boolean validate(String cmdline) {
        return (true);
    }
}
