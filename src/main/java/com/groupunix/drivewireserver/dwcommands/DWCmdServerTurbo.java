package com.groupunix.drivewireserver.dwcommands;


import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWSerialDevice;

public class DWCmdServerTurbo extends DWCommand {

    private final DWProtocol dwProto;

    DWCmdServerTurbo(DWProtocol dwProto, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProto;
    }

    public String getCommand() {
        return "turbo";
    }


    public String getShortHelp() {
        return "Turn on DATurbo mode (testing only)";
    }


    public String getUsage() {
        return "dw server turbo";
    }

    public DWCommandResponse parse(String cmdline) {
        return (doServerTurbo());
    }

    private DWCommandResponse doServerTurbo() {


        DWSerialDevice serdev = (DWSerialDevice) this.dwProto.getProtoDev();

        serdev.enableDATurbo();

        return (new DWCommandResponse("Device is now in DATurbo mode"));

    }

    public boolean validate(String cmdline) {
        return (true);
    }

}
