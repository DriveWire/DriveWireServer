package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

import java.util.ArrayList;

public class UICmdServerShowSerialDevs extends DWCommand {

    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return "serialdevs";
    }


    @Override
    public String getShortHelp() {
        // TODO Auto-generated method stub
        return "show available serial devices";
    }


    @Override
    public String getUsage() {
        // TODO Auto-generated method stub
        return "ui server show serialdevs";
    }

    @Override
    public DWCommandResponse parse(String cmdline) {
        StringBuilder txt = new StringBuilder();

        ArrayList<String> ports = DriveWireServer.getAvailableSerialPorts();

        for (String port : ports) {
            txt.append(port).append("\n");
        }

        return (new DWCommandResponse(txt.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
