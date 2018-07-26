package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

import java.util.List;

public class UICmdServerShowErrors extends DWCommand {


    private final DWProtocol dwProto;

    public UICmdServerShowErrors(DWUIClientThread dwuiClientThread) {
        this.dwProto = DriveWireServer.getHandler(dwuiClientThread.getInstance());
    }


    public UICmdServerShowErrors(DWProtocol dwProto) {
        this.dwProto = dwProto;
    }


    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return "errors";
    }


    @Override
    public String getShortHelp() {
        // TODO Auto-generated method stub
        return "show error descriptions";
    }

    @Override
    public String getUsage() {
        // TODO Auto-generated method stub
        return "ui server show errors";
    }

    @Override
    public DWCommandResponse parse(String cmdline) {
        StringBuilder res = new StringBuilder();

        if (dwProto != null) {
            List<String> rconfs = dwProto.getHelp().getSectionTopics("resultcode");

            if (rconfs != null) {
                for (String rc : rconfs) {
                    try {
                        res.append(rc.substring(1)).append("|").append(dwProto.getHelp().getTopicText("resultcode." + rc).trim()).append("\r\n");
                    }
                    catch (DWHelpTopicNotFoundException e) {
                        // whatever
                    }

                }

                return (new DWCommandResponse(res.toString()));
            }
        }

        return (new DWCommandResponse(false, DWDefs.RC_HELP_TOPIC_NOT_FOUND, "No error descriptions available from server"));

    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
