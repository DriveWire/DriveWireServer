package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class DWCmdMidiStatus extends DWCommand {

    private final DWProtocolHandler dwProto;

    DWCmdMidiStatus(DWProtocolHandler dwProto, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProto;
    }

    public String getCommand() {
        return "status";
    }


    public String getShortHelp() {
        return "Show MIDI status";
    }


    public String getUsage() {
        return "dw midi status";
    }

    public DWCommandResponse parse(String cmdline) {
        return (doMidiStatus());
    }

    private DWCommandResponse doMidiStatus() {
        StringBuilder text = new StringBuilder();

        text.append("\r\nDriveWire MIDI status:\r\n\n");

        if (dwProto.getConfig().getBoolean("UseMIDI", true)) {
            text.append("Devices:\r\n");

            MidiDevice device;
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

            for (int i = 0; i < infos.length; i++) {
                try {
                    device = MidiSystem.getMidiDevice(infos[i]);
                    text.append("[").append(i).append("] ");
                    text.append(device.getDeviceInfo().getName()).append(" (").append(device.getClass().getSimpleName()).append(")\r\n");
                    text.append("    ").append(device.getDeviceInfo().getDescription()).append(", ");
                    text.append(device.getDeviceInfo().getVendor()).append(" ");
                    text.append(device.getDeviceInfo().getVersion()).append("\r\n");

                }
                catch (MidiUnavailableException e) {
                    return (new DWCommandResponse(false, DWDefs.RC_MIDI_UNAVAILABLE, e.getMessage()));
                }

            }

            text.append("\r\nCurrent MIDI output device: ");

            if (dwProto.getVPorts().getMidiDeviceInfo() == null) {

                text.append("none\r\n");
            }
            else {
                text.append(dwProto.getVPorts().getMidiDeviceInfo().getName()).append("\r\n");
            }
        }
        else {
            text.append("MIDI is disabled.\r\n");
        }

        return (new DWCommandResponse(text.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }

}
