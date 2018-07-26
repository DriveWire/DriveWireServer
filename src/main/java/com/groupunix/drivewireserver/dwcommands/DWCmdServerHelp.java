package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdServerHelp extends DWCommand {

    static final String command = "help";
    private final DWCommandList commands;


    DWCmdServerHelp(DWProtocol dwProtocol, DWCommand parent) {
        setParentCmd(parent);
        commands = new DWCommandList(dwProtocol, dwProtocol.getCMDCols());
        commands.addcommand(new DWCmdServerHelpShow(dwProtocol, this));
        commands.addcommand(new DWCmdServerHelpReload(dwProtocol, this));

    }


    public String getCommand() {
        return command;
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (new DWCommandResponse(this.commands.getShortHelp()));
        }
        return (commands.parse(cmdline));
    }

    public DWCommandList getCommandList() {
        return (this.commands);
    }


    public String getShortHelp() {
        return "Manage the help system";
    }


    public String getUsage() {
        return "dw help [command]";
    }


    public boolean validate(String cmdline) {
        return (commands.validate(cmdline));
    }


}
