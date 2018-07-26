package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;

public class DWCmdMidiSynthShowChannels extends DWCommand {

    private final DWProtocolHandler dwProto;

    DWCmdMidiSynthShowChannels(DWProtocolHandler dwProto, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProto;
    }

    public String getCommand() {
        return "channels";
    }


    public String getShortHelp() {
        return "Show internal synth channel status";
    }


    public String getUsage() {
        return "dw midi synth show channels";
    }

    public DWCommandResponse parse(String cmdline) {
        StringBuilder text;

        text = new StringBuilder("\r\nInternal synthesizer channel status:\r\n\n");

        if (dwProto.getVPorts().getMidiSynth() != null) {
            MidiChannel[] midchans = dwProto.getVPorts().getMidiSynth().getChannels();

            Instrument[] instruments = dwProto.getVPorts().getMidiSynth().getLoadedInstruments();

            text.append("Chan#  Instr#  Orig#   Instrument\r\n");
            text.append("-----------------------------------------------------------------------------\r\n");

            for (int i = 0; i < midchans.length; i++) {
                if (midchans[i] != null) {
                    text.append(String.format(" %2d      %-3d    %-3d    ", (i + 1), midchans[i].getProgram(), dwProto.getVPorts().getGMInstrumentCache(i)));

                    if (midchans[i].getProgram() < instruments.length) {
                        text.append(instruments[midchans[i].getProgram()].getName());
                    }
                    else {
                        text.append("(unknown instrument or no soundbank loaded)");
                    }
                    text.append("\r\n");
                }
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
