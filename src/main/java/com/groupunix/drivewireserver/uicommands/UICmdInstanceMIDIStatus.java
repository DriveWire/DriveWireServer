package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;
import org.apache.commons.configuration.HierarchicalConfiguration;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.List;

public class UICmdInstanceMIDIStatus extends DWCommand {

    private DWUIClientThread dwuithread = null;
    private DWProtocol gproto = null;

    public UICmdInstanceMIDIStatus(DWUIClientThread dwuiClientThread) {
        this.dwuithread = dwuiClientThread;

    }


    public UICmdInstanceMIDIStatus(DWProtocol dwProto) {
        this.gproto = dwProto;
    }


    @Override
    public String getCommand() {
        return "midistatus";
    }


    @Override
    public String getShortHelp() {
        return "show MIDI status";
    }

    @Override
    public String getUsage() {
        return "ui instance midistatus";
    }

    @Override
    public DWCommandResponse parse(String cmdline) {

        StringBuilder res = new StringBuilder("enabled|false\n\n");

        if (this.gproto == null) {
            if (DriveWireServer.isValidHandlerNo(this.dwuithread.getInstance())) {
                this.gproto = DriveWireServer.getHandler(this.dwuithread.getInstance());
            }
            else {
                return (new DWCommandResponse(res.toString()));
            }
        }


        if (this.gproto.hasMIDI()) {
            DWProtocolHandler dwProto = (DWProtocolHandler) gproto;

            if (!(dwProto == null) && !(dwProto.getVPorts() == null) && !(dwProto.getVPorts().getMidiDeviceInfo() == null)) {
                try {
                    res = new StringBuilder("enabled|" + dwProto.getConfig().getBoolean("UseMIDI", false) + "\r\n");
                    res.append("cdevice|").append(dwProto.getVPorts().getMidiDeviceInfo().getName()).append("\r\n");
                    res.append("cprofile|").append(dwProto.getVPorts().getMidiProfileName()).append("\r\n");

                    MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

                    for (int j = 0; j < infos.length; j++) {
                        MidiDevice.Info i = infos[j];
                        MidiDevice dev = MidiSystem.getMidiDevice(i);

                        res.append("device|").append(j).append("|").append(dev.getClass().getSimpleName()).append("|").append(i.getName()).append("|").append(i.getDescription()).append("|").append(i.getVendor()).append("|").append(i.getVersion()).append("\r\n");

                    }

                    @SuppressWarnings("unchecked")
                    List<HierarchicalConfiguration> profiles = DriveWireServer.serverconfig.configurationsAt("midisynthprofile");

                    for (HierarchicalConfiguration mprof : profiles) {
                        res.append("profile|").append(mprof.getString("[@name]")).append("|").append(mprof.getString("[@desc]")).append("\r\n");
                    }
                }
                catch (MidiUnavailableException e) {
                    res = new StringBuilder("enabled|false\n\n");
                }
            }
        }

        return (new DWCommandResponse(res.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
