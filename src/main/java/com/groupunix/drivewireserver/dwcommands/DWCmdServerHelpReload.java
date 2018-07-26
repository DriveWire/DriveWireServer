package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

public class DWCmdServerHelpReload extends DWCommand {


    private final DWProtocol dwProto;


    DWCmdServerHelpReload(DWProtocol dwProtocol, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProtocol;
    }


    public String getCommand() {
        return "reload";
    }


    public String getShortHelp() {
        return "Reload help topics";
    }


    public String getUsage() {
        return "dw help reload";
    }

    public DWCommandResponse parse(String cmdline) {
        return (doHelpReload());
    }


    private DWCommandResponse doHelpReload() {
        dwProto.getHelp().reload();

        return (new DWCommandResponse("Reloaded help topics."));
    }


    public boolean validate(String cmdline) {
        return (true);
    }
}
