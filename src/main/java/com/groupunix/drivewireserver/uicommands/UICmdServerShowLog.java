package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

import java.util.ArrayList;

public class UICmdServerShowLog extends DWCommand {


    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return "log";
    }


    @Override
    public String getShortHelp() {
        // TODO Auto-generated method stub
        return "show log buffer";
    }

    @Override
    public String getUsage() {
        // TODO Auto-generated method stub
        return "ui server show log";
    }

    @Override
    public DWCommandResponse parse(String cmdline) {
        StringBuilder txt = new StringBuilder();

        ArrayList<String> log = DriveWireServer.getLogEvents(DriveWireServer.getLogEventsSize());

        for (String l : log) {
            txt.append(l);
        }

        return (new DWCommandResponse(txt.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
