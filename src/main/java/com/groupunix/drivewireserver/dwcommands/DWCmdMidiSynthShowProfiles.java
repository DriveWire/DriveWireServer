package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DriveWireServer;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.List;

public class DWCmdMidiSynthShowProfiles extends DWCommand {


    DWCmdMidiSynthShowProfiles(DWCommand parent) {
        setParentCmd(parent);
    }

    public String getCommand() {
        return "profiles";
    }


    public String getShortHelp() {
        return "Show internal synth profiles";
    }


    public String getUsage() {
        return "dw midi synth show profiles";
    }

    @SuppressWarnings("unchecked")
    public DWCommandResponse parse(String cmdline) {
        StringBuilder text;

        text = new StringBuilder("\r\nAvailable sound translation profiles:\r\n\n");

        List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");

        for (HierarchicalConfiguration mprof : profiles) {
            text.append(String.format("%-10s: %-35s dev_adjust: %2d  gm_adjust: %2d", mprof.getString("[@name]", "n/a"), mprof.getString("[@desc]", "n/a"), mprof.getInt("[@dev_adjust]", 0), mprof.getInt("[@gm_adjust]", 0)));
            text.append("\r\n");
        }

        text.append("\r\n");

        return (new DWCommandResponse(text.toString()));
    }


    public boolean validate(String cmdline) {
        return (true);
    }
}
