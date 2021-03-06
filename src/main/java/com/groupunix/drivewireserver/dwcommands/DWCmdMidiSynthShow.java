package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;


public class DWCmdMidiSynthShow extends DWCommand {

    private final DWCommandList commands;

    DWCmdMidiSynthShow(DWProtocolHandler dwProto, DWCommand parent) {
        setParentCmd(parent);
        commands = new DWCommandList(dwProto, dwProto.getCMDCols());
        commands.addcommand(new DWCmdMidiSynthShowChannels(dwProto, this));
        commands.addcommand(new DWCmdMidiSynthShowInstr(dwProto, this));
        commands.addcommand(new DWCmdMidiSynthShowProfiles(this));
    }

    public String getCommand() {
        return "show";
    }

    public DWCommandList getCommandList() {
        return (this.commands);
    }


    public String getShortHelp() {
        return "View details about the synth";
    }


    public String getUsage() {
        return "dw midi synth show [item]";
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (new DWCommandResponse(this.commands.getShortHelp()));
        }
        return (commands.parse(cmdline));
    }

    public boolean validate(String cmdline) {
        return (true);
    }

}
