package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

import java.io.IOException;

public class UICmdServerShowLocalDisks extends DWCommand {

    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return "localdisks";
    }


    @Override
    public String getShortHelp() {
        // TODO Auto-generated method stub
        return "show server local disks";
    }

    @Override
    public String getUsage() {
        // TODO Auto-generated method stub
        return "ui server show localdisks";
    }


    @Override
    public DWCommandResponse parse(String cmdline) {

        StringBuilder res = new StringBuilder();

        try {
            if (!DriveWireServer.serverconfig.containsKey("LocalDiskDir")) {
                return (new DWCommandResponse(false, DWDefs.RC_CONFIG_KEY_NOT_SET, "LocalDiskDir must be defined in configuration"));
            }

            String path = DriveWireServer.serverconfig.getString("LocalDiskDir");

            FileSystemManager fsManager;

            fsManager = VFS.getManager();

            FileObject dirobj = fsManager.resolveFile(path);

            FileObject[] children = dirobj.getChildren();

            for (FileObject aChildren : children) {
                if (aChildren.getType() == FileType.FILE) {
                    res.append(aChildren.getName()).append("\n");
                }
            }

        }
        catch (IOException e) {
            return (new DWCommandResponse(false, DWDefs.RC_SERVER_IO_EXCEPTION, e.getMessage()));
        }

        return (new DWCommandResponse(res.toString()));
    }


    public boolean validate(String cmdline) {
        return (true);
    }
}
