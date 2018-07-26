package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWFileSystemInvalidFilenameException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

import java.io.File;

public class UICmdServerFileXDir extends DWCommand {

    static final String command = "xdir";


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
                    try {
                        text.append(DWUtils.getFileXDescriptor(f)).append("\n");
                    }
                    catch (DWFileSystemInvalidFilenameException ignored) {

                    }
                }
            }

            for (File f : contents) {
                if (!f.isDirectory()) {
                    try {
                        text.append(DWUtils.getFileXDescriptor(f)).append("\n");
                    }
                    catch (DWFileSystemInvalidFilenameException ignored) {
                    }
                }
            }

        }

        return (new DWCommandResponse(text.toString()));
    }


    public String getShortHelp() {
        return "List directory contents (short form)";
    }


    public String getUsage() {
        return "ui server file xdir [path]";
    }

    public boolean validate(String cmdline) {
        return (true);
    }

}