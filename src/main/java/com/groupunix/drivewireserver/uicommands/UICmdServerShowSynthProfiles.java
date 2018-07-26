package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.List;

public class UICmdServerShowSynthProfiles extends DWCommand {

    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return "synthprofiles";
    }


    @Override
    public String getShortHelp() {
        // TODO Auto-generated method stub
        return "show MIDI synth profiles";
    }

    @Override
    public String getUsage() {
        // TODO Auto-generated method stub
        return "ui server show synthprofiles";
    }

    @SuppressWarnings("unchecked")
    @Override
    public DWCommandResponse parse(String cmdline) {
        StringBuilder res = new StringBuilder();

        List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");

        for (HierarchicalConfiguration mprof : profiles) {

            res.append(mprof.getString("[@name]")).append("|").append(mprof.getString("[@desc]")).append("\n");


        }


        return (new DWCommandResponse(res.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
