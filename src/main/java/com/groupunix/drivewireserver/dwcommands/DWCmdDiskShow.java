package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwdisk.DWDisk;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotLoadedException;
import com.groupunix.drivewireserver.dwexceptions.DWDriveNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class DWCmdDiskShow extends DWCommand {

    private final DWProtocolHandler dwProto;

    DWCmdDiskShow(DWProtocolHandler dwProto, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProto;
    }

    public String getCommand() {
        return "show";
    }


    public String getShortHelp() {
        return "Show current disk details";
    }


    public String getUsage() {
        return "dw disk show [#]";
    }

    public DWCommandResponse parse(String cmdline) {

        // show loaded disks
        if (cmdline.length() == 0) {
            return (doDiskShow());
        }


        String[] args = cmdline.split(" ");

        if (args.length == 1) {
            try {
                return doDiskShow(dwProto.getDiskDrives().getDriveNoFromString(args[0]));
            }
            catch (DWDriveNotValidException e) {
                return (new DWCommandResponse(false, DWDefs.RC_INVALID_DRIVE, e.getMessage()));
            }
        }

        return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Syntax error"));

    }


    private DWCommandResponse doDiskShow(int driveno) {
        String text;

        try {

            DWDisk disk = dwProto.getDiskDrives().getDisk(driveno);

            text = "Details for disk in drive #" + driveno + ":\r\n\r\n";

            text += DWUtils.shortenLocalURI(disk.getFilePath()) + "\r\n";

            text += "\r\n";

            // optional/warning type info

            if (disk.getParams().containsKey("_readerrors")) {
                text += "This drive reports " + disk.getParams().getInt("_readerrors") + " read errors.\r\n";
            }

            if (disk.getParams().containsKey("_writeerrors")) {
                text += "This drive reports " + disk.getParams().getInt("_writeerrors") + " write errors.\r\n";
            }

            if (disk.getDirtySectors() > 0) {
                text += "This drive reports " + disk.getDirtySectors() + " dirty sectors.\r\n";
            }


            HierarchicalConfiguration params = disk.getParams();

            ArrayList<String> ignores = new ArrayList<>();
            ArrayList<String> syss = new ArrayList<>();
            ArrayList<String> usrs = new ArrayList<>();

            ignores.add("_readerrors");
            ignores.add("_writeerrors");
            ignores.add("_path");
            ignores.add("_last_modified");


            for (@SuppressWarnings("unchecked") Iterator<String> itk = params.getKeys(); itk.hasNext(); ) {
                String p = itk.next();

                if (!ignores.contains(p)) {
                    if (p.startsWith("_")) {
                        syss.add(p.substring(1) + ": " + params.getProperty(p));
                    }
                    else {
                        usrs.add(p + ": " + params.getProperty(p));
                    }
                }
            }

            Collections.sort(syss);
            Collections.sort(usrs);

            text += "System params:\r\n";
            text += DWCommandList.colLayout(syss, this.dwProto.getCMDCols());

            text += "\r\nUser params:\r\n";
            text += DWCommandList.colLayout(usrs, this.dwProto.getCMDCols());

            return (new DWCommandResponse(text));


        }
        catch (DWDriveNotValidException e) {
            return (new DWCommandResponse(false, DWDefs.RC_INVALID_DRIVE, e.getMessage()));

        }
        catch (DWDriveNotLoadedException e) {
            return (new DWCommandResponse(false, DWDefs.RC_DRIVE_NOT_LOADED, e.getMessage()));
        }
    }


    private DWCommandResponse doDiskShow() {
        StringBuilder text;

        text = new StringBuilder("\r\nCurrent DriveWire disks:\r\n\r\n");


        for (int i = 0; i < dwProto.getDiskDrives().getMaxDrives(); i++) {


            if (dwProto.getDiskDrives().isLoaded(i)) {
                try {
                    text.append(String.format("X%-3d", i));
                    if (dwProto.getDiskDrives().getDisk(i).getWriteProtect()) {
                        text.append("*");
                    }
                    else {
                        text.append(" ");
                    }
                    text.append(DWUtils.shortenLocalURI(dwProto.getDiskDrives().getDisk(i).getFilePath())).append("\r\n");
                }
                catch (DWDriveNotLoadedException | DWDriveNotValidException ignored) {

                }
            }

        }

        return (new DWCommandResponse(text.toString()));
    }


    public boolean validate(String cmdline) {
        return (true);
    }

}
