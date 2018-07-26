package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class UICmdInstanceDiskDos extends DWCommand {

    static final String command = "dos";


    public UICmdInstanceDiskDos(DWUIClientThread dwuiClientThread) {

        commands.addcommand(new UICmdInstanceDiskDosDir(dwuiClientThread));
    }


    public UICmdInstanceDiskDos(DWProtocolHandler dwProto) {
        commands.addcommand(new UICmdInstanceDiskDosDir(dwProto));
    }


    public String getCommand() {
        return command;
    }

    public DWCommandResponse parse(String cmdline) {
        return (commands.parse(cmdline));
    }


    public String getShortHelp() {
        return "Instance DOS format disk commands";
    }


    public String getUsage() {
        return "ui instance disk dos [command]";
    }

    public boolean validate(String cmdline) {
        return (commands.validate(cmdline));
    }

}