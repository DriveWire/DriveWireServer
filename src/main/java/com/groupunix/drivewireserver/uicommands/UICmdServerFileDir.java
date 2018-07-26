package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

import java.io.File;

public class UICmdServerFileDir extends DWCommand {

    static final String command = "dir";


    public String getCommand() {
        return command;
    }

    public DWCommandResponse parse(String cmdline) {
        File dir = new File(cmdline);

        StringBuilder text = new StringBuilder();

        File[] contents = dir.listFiles();

        if (contents != null) {
            for (File f : contents) {
                if (f.isDirectory()) {
                    text.append(DWUtils.getFileDescriptor(f)).append("|false\n");
                }
            }

            for (File f : contents) {
                if (!f.isDirectory()) {
                    text.append(DWUtils.getFileDescriptor(f)).append("|false\n");
                }
            }

        }

        return (new DWCommandResponse(text.toString()));
    }


    public String getShortHelp() {
        return "List directory contents";
    }


    public String getUsage() {
        return "ui server file dir [path]";
    }

    public boolean validate(String cmdline) {
        return (true);
    }

}