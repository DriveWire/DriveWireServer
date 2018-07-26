package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocolHandler;

import javax.sound.midi.Instrument;

public class DWCmdMidiSynthShowInstr extends DWCommand {


    private final DWProtocolHandler dwProto;

    DWCmdMidiSynthShowInstr(DWProtocolHandler dwProto, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProto;
    }

    public String getCommand() {
        return "instr";
    }


    public String getShortHelp() {
        return "Show internal synth instruments";
    }


    public String getUsage() {
        return "dw midi synth show instr";
    }

    public DWCommandResponse parse(String cmdline) {
        StringBuilder text;

        text = new StringBuilder("\r\nInternal synthesizer instrument list:\r\n\n");

        if (dwProto.getVPorts().getMidiSynth() != null) {
            Instrument[] instruments = dwProto.getVPorts().getMidiSynth().getLoadedInstruments();

            if (instruments.length == 0) {
                text.append("No instruments found, you may need to load a soundbank.\r\n");
            }

            for (int i = 0; i < instruments.length; i++) {
                text.append(String.format("%3d:%-15s", i, instruments[i].getName()));

                if ((i % 4) == 0) {
                    text.append("\r\n");
                }

            }
            //	instruments[i].getDataClass().getSimpleName()
            text.append("\r\n");
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
