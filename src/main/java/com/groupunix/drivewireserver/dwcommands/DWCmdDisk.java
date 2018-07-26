package com.groupunix.drivewireserver.dwcommands;


import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

public class DWCmdDisk extends DWCommand {

    static final String command = "disk";
    private final DWCommandList commands;

    DWCmdDisk(DWProtocolHandler dwProto, DWCommand parent) {
        setParentCmd(parent);

        commands = new DWCommandList(dwProto, dwProto.getCMDCols());

        commands.addcommand(new DWCmdDiskShow(dwProto, this));
        commands.addcommand(new DWCmdDiskEject(dwProto, this));
        commands.addcommand(new DWCmdDiskInsert(dwProto, this));
        commands.addcommand(new DWCmdDiskReload(dwProto, this));
        commands.addcommand(new DWCmdDiskWrite(dwProto, this));
        commands.addcommand(new DWCmdDiskCreate(dwProto, this));
        commands.addcommand(new DWCmdDiskSet(dwProto, this));
        commands.addcommand(new DWCmdDiskDos(dwProto, this));
    }


    public String getCommand() {
        return command;
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (new DWCommandResponse(this.commands.getShortHelp()));
        }
        return (commands.parse(cmdline));
    }

    public DWCommandList getCommandList() {
        return (this.commands);
    }


    public String getShortHelp() {
        return "Manage disks and disksets";
    }


    public String getUsage() {
        return "dw disk [command]";
    }

    public boolean validate(String cmdline) {
        return (commands.validate(cmdline));
    }

}
