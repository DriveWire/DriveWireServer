package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

import java.util.ArrayList;

public class DWCmdServerHelpShow extends DWCommand {


    private final DWProtocol dwProto;


    DWCmdServerHelpShow(DWProtocol dwProtocol, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProtocol;
    }


    public String getCommand() {
        return "show";
    }


    public String getShortHelp() {
        return "Show help topic";
    }


    public String getUsage() {
        return "dw help show [topic]";
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (doShowHelp());
        }
        return (doShowHelp(cmdline));
    }


    private DWCommandResponse doShowHelp(String cmdline) {
        String text = "Help for " + cmdline + ":\r\n\r\n";

        try {
            text += dwProto.getHelp().getTopicText(cmdline);
            return (new DWCommandResponse(text));
        }
        catch (DWHelpTopicNotFoundException e) {
            return (new DWCommandResponse(false, DWDefs.RC_CONFIG_KEY_NOT_SET, e.getMessage()));
        }

    }


    private DWCommandResponse doShowHelp() {
        StringBuilder text;

        text = new StringBuilder("Help Topics:\r\n\r\n");

        ArrayList<String> tops = dwProto.getHelp().getTopics(null);

        for (String top : tops) {
            text.append(top).append("\r\n");
        }
        return (new DWCommandResponse(text.toString()));
    }


    public boolean validate(String cmdline) {
        return (true);
    }
}
