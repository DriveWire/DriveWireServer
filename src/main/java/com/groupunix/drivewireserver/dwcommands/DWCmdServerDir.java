package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

public class DWCmdServerDir extends DWCommand {


    DWCmdServerDir(DWCommand parent) {
        setParentCmd(parent);
    }


    public String getCommand() {
        return "dir";
    }


    public String getShortHelp() {
        return "Show directory of URI or local path";
    }


    public String getUsage() {
        return "dw server dir URI/path";
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "dw server dir requires a URI or path as an argument"));
        }
        return (doDir(cmdline));
    }

    private DWCommandResponse doDir(String path) {
        FileSystemManager fsManager;

        StringBuilder text = new StringBuilder();

        path = DWUtils.convertStarToBang(path);

        try {
            fsManager = VFS.getManager();

            FileObject dirobj = fsManager.resolveFile(path);

            FileObject[] children = dirobj.getChildren();

            text.append("Directory of ").append(dirobj.getName().getURI()).append("\r\n\n");


            int longest = 0;

            for (FileObject aChildren : children) {
                if (aChildren.getName().getBaseName().length() > longest) {
                    longest = aChildren.getName().getBaseName().length();
                }
            }

            longest++;
            longest++;

            int cols = Math.max(1, 80 / longest);

            for (int i = 0; i < children.length; i++) {
                text.append(String.format("%-" + longest + "s", children[i].getName().getBaseName()));
                if (((i + 1) % cols) == 0) {
                    text.append("\r\n");
                }
            }

        }
        catch (FileSystemException e) {
            return (new DWCommandResponse(false, DWDefs.RC_SERVER_FILESYSTEM_EXCEPTION, e.getMessage()));
        }

        return (new DWCommandResponse(text.toString()));
    }


    public boolean validate(String cmdline) {
        return true;
    }


}
