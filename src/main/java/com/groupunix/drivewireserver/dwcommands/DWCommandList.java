package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwexceptions.DWHelpTopicNotFoundException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class DWCommandList {

    private final List<DWCommand> commands = new ArrayList<>();
    private int outputcols = 80;
    private final DWProtocol dwProto;

    public DWCommandList(DWProtocol dwProto, int outputcols) {
        if (outputcols > 0) {
            this.outputcols = outputcols;
        }

        this.dwProto = dwProto;
    }

    public DWCommandList(DWProtocol dwProto) {
        this.dwProto = dwProto;

    }


    public void addcommand(DWCommand dwCommand) {
        commands.add(dwCommand);
    }

    public List<DWCommand> getCommands() {
        return (this.commands);
    }


    public DWCommandResponse parse(String cmdline) {

        String[] args = cmdline.split(" ");

        if ((null == cmdline) || (cmdline.length() == 0)) {
            // ended here, show commands..
            return (new DWCommandResponse(getShortHelp()));
        }

        int matches = numCommandMatches(args[0]);

        if (matches == 0) {
            return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Unknown command '" + args[0] + "'"));
        }
        else if (matches > 1) {
            return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "Ambiguous command, '" + args[0] + "' matches " + getTextMatches(args[0])));
        }
        else {
            if ((args.length == 2) && args[1].equals("?")) {
                return (getLongHelp(getCommandMatch(args[0])));
            }
            else if ((args.length == 2) && args[1].equals("*")) {
                return (getCmdTree(getCommandMatch(args[0])));
            }
            else {
                return (getCommandMatch(args[0]).parse(DWUtils.dropFirstToken(cmdline)));
            }
        }
    }


    private DWCommandResponse getCmdTree(DWCommand cmd) {
        String text;


        // figure out whole command..
        StringBuilder cmdline = new StringBuilder(cmd.getCommand());

        DWCommand tmp = cmd;

        while (tmp.getParentCmd() != null) {
            tmp = tmp.getParentCmd();
            cmdline.insert(0, tmp.getCommand() + " ");
        }

        text = "Tree for " + cmdline + "\r\n\n";

        text += makeTreeString(cmd, 0);


        if (this.outputcols <= 32) {
            text = text.toUpperCase();
        }

        return (new DWCommandResponse(text));
    }


    private String makeTreeString(DWCommand cmd, int depth) {

        StringBuilder res = new StringBuilder();

        res.append(String.format("%-30s", String.format(("%-" + (depth + 1) * 4 + "s"), " ") + cmd.getCommand())).append(cmd.getShortHelp()).append("\r\n");

        if (cmd.getCommandList() != null) {
            for (DWCommand c : cmd.getCommandList().commands) {
                res.append(makeTreeString(c, depth + 1));
            }
        }

        return res.toString();
    }

    private DWCommandResponse getLongHelp(DWCommand cmd) {
        String text;


        // figure out whole command..
        StringBuilder cmdline = new StringBuilder(cmd.getCommand());

        DWCommand tmp = cmd;

        while (tmp.getParentCmd() != null) {
            tmp = tmp.getParentCmd();
            cmdline.insert(0, tmp.getCommand() + " ");
        }

        text = cmd.getUsage() + "\r\n\r\n";
        text += cmd.getShortHelp() + "\r\n";

        if (this.dwProto != null) {
            try {
                text = dwProto.getHelp().getTopicText(cmdline.toString());
            }
            catch (DWHelpTopicNotFoundException ignored) {
            }
        }

        if (this.outputcols <= 32) {
            text = text.toUpperCase();
        }

        return (new DWCommandResponse(text));
    }


    public String getShortHelp() {
        String txt;

        ArrayList<String> ps = new ArrayList<>();

        for (DWCommand cmd : this.commands) {
            ps.add(cmd.getCommand());

        }

        Collections.sort(ps);
        txt = DWCommandList.colLayout(ps, this.outputcols);

        txt = "Possible commands:\r\n\r\n" + txt;

        return (txt);
    }


    private String getTextMatches(String arg) {
        StringBuilder matchtxt = new StringBuilder();

        for (DWCommand cmd : this.commands) {
            if (cmd.getCommand().startsWith(arg.toLowerCase())) {
                if (matchtxt.length() == 0) {
                    matchtxt.append(cmd.getCommand());
                }
                else {
                    matchtxt.append(" or ").append(cmd.getCommand());
                }
            }
        }

        return matchtxt.toString();
    }

    private int numCommandMatches(String arg) {
        int matches = 0;

        for (DWCommand command : this.commands) {
            if (command.getCommand().startsWith(arg.toLowerCase())) {
                matches++;
            }
        }

        return matches;
    }


    private DWCommand getCommandMatch(String arg) {
        DWCommand cmd;

        for (DWCommand command : this.commands) {
            cmd = command;

            if (cmd.getCommand().startsWith(arg.toLowerCase())) {
                return (cmd);
            }
        }

        return null;
    }


    public boolean validate(String cmdline) {
        String[] args = cmdline.split(" ");

        if (cmdline.length() == 0) {
            // we ended here
            return true;
        }

        int matches = numCommandMatches(args[0]);

        if (matches == 0) {
            // no match
            return false;
        }
        else if (matches > 1) {
            // ambiguous
            return false;
        }
        else {
            return (getCommandMatch(args[0]).validate(DWUtils.dropFirstToken(cmdline)));
        }
    }


    public static String colLayout(ArrayList<String> ps, int cols) {
        cols = cols - 1;
        StringBuilder text = new StringBuilder();

        Iterator<String> it = ps.iterator();
        int maxlen = 1;
        while (it.hasNext()) {
            int curlen = it.next().length();
            if (curlen > maxlen) {
                maxlen = curlen;
            }
        }

        // leave spaces between cols
        if (cols > 32) {
            maxlen += 2;
        }
        else {
            maxlen++;
        }

        it = ps.iterator();
        int i = 0;
        int ll = cols / maxlen;
        while (it.hasNext()) {
            String itxt = String.format("%-" + maxlen + "s", it.next());
            if ((i > 0) && ((i % ll) == 0)) {
                text.append("\r\n");
            }

            if (cols <= 32) {
                itxt = itxt.toUpperCase();
            }

            text.append(itxt);

            i++;
        }

        text.append("\r\n");

        return (text.toString());
    }


    public ArrayList<String> getCommandStrings() {
        return (this.getCommandStrings(this, ""));
    }

    private ArrayList<String> getCommandStrings(DWCommandList commands, String prefix) {
        ArrayList<String> res = new ArrayList<>();

        for (DWCommand cmd : commands.getCommands()) {
            res.add(prefix + " " + cmd.getCommand());
            if (cmd.getCommandList() != null) {
                res.addAll(this.getCommandStrings(cmd.getCommandList(), prefix + " " + cmd.getCommand()));
            }
        }

        return (res);
    }


}
