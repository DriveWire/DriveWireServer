package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.*;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

import java.io.IOException;

public class DWCmdDiskDosFormat extends DWCommand {
    private final DWProtocolHandler dwProto;

    DWCmdDiskDosFormat(DWProtocolHandler dwProto, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProto;
    }

    public String getCommand() {
        return "format";
    }

    public DWCommandResponse parse(String cmdline) {

        String[] args = cmdline.split(" ");

        if (args.length == 1) {
            try {
                return (doDiskDosCreate(dwProto.getDiskDrives().getDriveNoFromString(args[0])));
            }
            catch (DWDriveNotValidException e) {
                return (new DWCommandResponse(false, DWDefs.RC_INVALID_DRIVE, e.getMessage()));
            }
            catch (DWDriveNotLoadedException e) {
                return (new DWCommandResponse(false, DWDefs.RC_DRIVE_NOT_LOADED, e.getMessage()));
            }
            catch (DWInvalidSectorException | DWSeekPastEndOfDeviceException | IOException | DWDriveWriteProtectedException e) {
                return (new DWCommandResponse(false, DWDefs.RC_DRIVE_ERROR, e.getMessage()));
            }

        }

        return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error"));
    }


    private DWCommandResponse doDiskDosCreate(int driveno) throws DWDriveNotValidException, DWDriveNotLoadedException, DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException, IOException {
        dwProto.getDiskDrives().formatDOSFS(driveno);

        return (new DWCommandResponse("Formatted new DOS disk image in drive " + driveno + "."));

    }


    public String getShortHelp() {
        return "Format disk image with DOS filesystem";
    }

    public String getUsage() {
        return "dw disk dos format #";
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
