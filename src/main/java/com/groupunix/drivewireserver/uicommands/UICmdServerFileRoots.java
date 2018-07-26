package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

import java.io.File;

public class UICmdServerFileRoots extends DWCommand {

    static final String command = "roots";


    public String getCommand() {
        return command;
    }

    public DWCommandResponse parse(String cmdline) {

        File[] roots = File.listRoots();

        StringBuilder text = new StringBuilder();

        for (File f : roots) {
            text.append(DWUtils.getFileDescriptor(f)).append("|true\n");
        }

        return (new DWCommandResponse(text.toString()));
    }


    public String getShortHelp() {
        return "List filesystem roots";
    }


    public String getUsage() {
        return "ui server file roots";
    }

    public boolean validate(String cmdline) {
        return (true);
    }

}