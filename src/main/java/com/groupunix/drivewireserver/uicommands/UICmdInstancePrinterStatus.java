package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.List;

public class UICmdInstancePrinterStatus extends DWCommand {

    private DWUIClientThread dwuithread = null;
    private DWProtocolHandler dwProto = null;

    public UICmdInstancePrinterStatus(DWUIClientThread dwuiClientThread) {
        this.dwuithread = dwuiClientThread;
    }


    public UICmdInstancePrinterStatus(DWProtocolHandler dwProto) {
        this.dwProto = dwProto;
    }


    @Override
    public String getCommand() {
        return "printerstatus";
    }


    @Override
    public String getShortHelp() {
        return "show printer status";
    }

    @Override
    public String getUsage() {
        return "ui instance printerstatus";
    }

    @Override
    public DWCommandResponse parse(String cmdline) {
        StringBuilder res;

        if (dwProto == null) {
            if (DriveWireServer.getHandler(this.dwuithread.getInstance()).hasPrinters()) {
                dwProto = (DWProtocolHandler) DriveWireServer.getHandler(this.dwuithread.getInstance());
            }
            else {
                return (new DWCommandResponse(false, DWDefs.RC_INSTANCE_WONT, "This operation is not supported on this type of instance"));
            }
        }


        res = new StringBuilder("currentprinter|" + dwProto.getConfig().getString("CurrentPrinter", "none") + "\r\n");

        @SuppressWarnings("unchecked")
        List<HierarchicalConfiguration> profiles = dwProto.getConfig().configurationsAt("Printer");

        for (HierarchicalConfiguration mprof : profiles) {
            res.append("printer|").append(mprof.getString("[@name]")).append("|").append(mprof.getString("[@desc]")).append("\r\n");
        }


        return (new DWCommandResponse(res.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
