package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import org.apache.commons.configuration.ConfigurationException;

public class DWCmdConfigSave extends DWCommand {

    final DWProtocol dwProto;


    DWCmdConfigSave(DWProtocol dwProtocol, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProtocol;
    }

    public String getCommand() {
        return "save";
    }


    public String getShortHelp() {
        return "Save configuration";
    }


    public String getUsage() {
        return "dw config save";
    }

    public DWCommandResponse parse(String cmdline) {

        try {
            DriveWireServer.saveServerConfig();
        }
        catch (ConfigurationException e) {
            return (new DWCommandResponse(false, DWDefs.RC_SERVER_IO_EXCEPTION, e.getMessage()));
        }

        return (new DWCommandResponse("Configuration saved."));
    }


    public boolean validate(String cmdline) {

        return true;
    }


}
