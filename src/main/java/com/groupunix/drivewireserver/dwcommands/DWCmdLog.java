package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdLog extends DWCommand {

    static final String command = "log";
    private final DWCommandList commands;

    DWCmdLog(DWProtocol dwProto, DWCommand parent) {
        setParentCmd(parent);

        commands = new DWCommandList(dwProto, dwProto.getCMDCols());
        commands.addcommand(new DWCmdLogShow(this));

    }


    public String getCommand() {
        return command;
    }

    public DWCommandList getCommandList() {
        return (this.commands);
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (new DWCommandResponse(this.commands.getShortHelp()));
        }
        return (commands.parse(cmdline));
    }


    public String getShortHelp() {
        return "View the server log";
    }


    public String getUsage() {
        return "dw log [command]";
    }

    public boolean validate(String cmdline) {
        return (commands.validate(cmdline));
    }
}
