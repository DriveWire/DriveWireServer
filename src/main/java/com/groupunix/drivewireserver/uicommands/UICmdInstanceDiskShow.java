package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.Iterator;

public class UICmdInstanceDiskShow extends DWCommand {

    static final String command = "show";

    private DWUIClientThread uiref = null;

    private DWProtocolHandler dwProto = null;

    public UICmdInstanceDiskShow(DWUIClientThread dwuiClientThread) {

        this.uiref = dwuiClientThread;
    }

    public UICmdInstanceDiskShow(DWProtocolHandler dwProto) {
        this.dwProto = dwProto;
    }

    public String getCommand() {
        return command;
    }

    @SuppressWarnings("unchecked")
    public DWCommandResponse parse(String cmdline) {
        StringBuilder res = new StringBuilder();

        // TODO hackish!
        if (this.dwProto == null) {
            if (DriveWireServer.getHandler(this.uiref.getInstance()).hasDisks()) {
                dwProto = (DWProtocolHandler) DriveWireServer.getHandler(this.uiref.getInstance());
            }
            else {
                return (new DWCommandResponse(false, DWDefs.RC_INSTANCE_WONT, "This operation is not supported on this type of instance"));
            }
        }


        if (cmdline.length() == 0) {
            if (dwProto.getDiskDrives() == null) {
                return (new DWCommandResponse(false, DWDefs.RC_NO_SUCH_DISKSET, "Disk drives are null, is server restarting?"));
            }

            for (int i = 0; i < dwProto.getDiskDrives().getMaxDrives(); i++) {
                if (dwProto.getDiskDrives().isLoaded(i)) {
                    try {
                        res.append(i).append("|").append(dwProto.getDiskDrives().getDisk(i).getFilePath()).append("\n");
                    }
                    catch (DWDriveNotLoadedException | DWDriveNotValidException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            // disk details
            try {

                int driveno = Integer.parseInt(cmdline);

                if ((!(dwProto.getDiskDrives() == null)) && (dwProto.getDiskDrives().isLoaded(driveno))) {
                    res.append("*loaded|true\n");

                    HierarchicalConfiguration disk = dwProto.getDiskDrives().getDisk(driveno).getParams();

                    for (Iterator<String> itk = disk.getKeys(); itk.hasNext(); ) {
                        String option = itk.next();

                        res.append(option).append("|").append(disk.getProperty(option)).append("\n");
                    }

                }
                else {
                    res.append("*loaded|false\n");
                }
            }
            catch (NumberFormatException e) {
                return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Non numeric drive number"));
            }
            catch (DWDriveNotLoadedException e) {
                res.append("*loaded|false\n");
            }
            catch (DWDriveNotValidException e) {
                return (new DWCommandResponse(false, DWDefs.RC_INVALID_DRIVE, e.getMessage()));
            }

        }

        return (new DWCommandResponse(res.toString()));
    }


    public String getShortHelp() {
        return "Show current disks";
    }


    public String getUsage() {
        return "ui instance disk show";
    }

    public boolean validate(String cmdline) {
        return (true);
    }

}