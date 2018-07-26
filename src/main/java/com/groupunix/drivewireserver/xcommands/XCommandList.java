package com.groupunix.drivewireserver.xcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;

import java.util.ArrayList;
import java.util.List;


public class XCommandList {

    private final List<XCommand> commands = new ArrayList<>();


    public XCommandList(DWProtocol dwProtocol) {
        if (dwProtocol != null) {
            // commands requiring an instance

        }

        commands.add(new XCmdServerMemory());
        commands.add(new XCmdServerVersion());

    }


    public List<XCommand> getCommands() {
        return (this.commands);
    }


    public XCommandResponse parse(String cmdline) {
        XCommand xcmd = getCommandMatch(cmdline);

        if (xcmd == null) {
            return (new XCommandResponse(DWDefs.RC_SYNTAX_ERROR));
        }
        else {
            return (xcmd.parse(cmdline));
        }

    }


    private XCommand getCommandMatch(String arg) {
        XCommand cmd;

        for (XCommand command : this.commands) {
            cmd = command;

            if (cmd.getCommand().startsWith(arg.toLowerCase())) {
                return (cmd);
            }
        }

        return null;
    }

    public boolean validate(String cmd) {
        return getCommandMatch(cmd) != null;

    }


}
