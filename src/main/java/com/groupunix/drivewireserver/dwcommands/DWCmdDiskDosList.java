package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwdisk.filesystem.DWDECBFileSystem;
import com.groupunix.drivewireserver.dwexceptions.*;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

import java.io.IOException;

public class DWCmdDiskDosList extends DWCommand {
    private final DWProtocolHandler dwProto;

    DWCmdDiskDosList(DWProtocolHandler dwProto, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProto;
    }

    public String getCommand() {
        return "list";
    }

    public DWCommandResponse parse(String cmdline) {

        String[] args = cmdline.split(" ");

        if (args.length == 2) {
            try {
                return (doDiskDosList(dwProto.getDiskDrives().getDriveNoFromString(args[0]), args[1]));
            }
            catch (DWDriveNotValidException e) {
                return (new DWCommandResponse(false, DWDefs.RC_INVALID_DRIVE, e.getMessage()));
            }
            catch (DWDriveNotLoadedException e) {
                return (new DWCommandResponse(false, DWDefs.RC_DRIVE_NOT_LOADED, e.getMessage()));
            }
            catch (DWFileSystemFileNotFoundException e) {
                return (new DWCommandResponse(false, DWDefs.RC_SERVER_FILE_NOT_FOUND, e.getMessage()));
            }
            catch (DWFileSystemInvalidFATException | DWDiskInvalidSectorNumber | DWFileSystemInvalidDirectoryException e) {
                return (new DWCommandResponse(false, DWDefs.RC_SERVER_FILESYSTEM_EXCEPTION, e.getMessage()));
            }
            catch (IOException e) {
                return (new DWCommandResponse(false, DWDefs.RC_SERVER_IO_EXCEPTION, e.getMessage()));
            }

        }

        return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error"));
    }


    private DWCommandResponse doDiskDosList(int driveno, String filename) throws DWDriveNotLoadedException, DWDriveNotValidException, DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException {
        String res;

        DWDECBFileSystem tmp = new DWDECBFileSystem(dwProto.getDiskDrives().getDisk(driveno));

        res = new String(tmp.getFileContents(filename));

        return (new DWCommandResponse(res));

    }


    public String getShortHelp() {
        return "List contents of DOS file";
    }

    public String getUsage() {
        return "dw disk dos list # filename";
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
