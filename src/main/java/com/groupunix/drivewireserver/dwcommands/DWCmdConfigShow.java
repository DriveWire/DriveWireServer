package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWProtocol;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;

public class DWCmdConfigShow extends DWCommand {

    final DWProtocol dwProto;

    DWCmdConfigShow(DWProtocol dwProtocol, DWCommand parent) {
        setParentCmd(parent);
        this.dwProto = dwProtocol;
    }

    public String getCommand() {
        return "show";
    }


    public String getShortHelp() {
        return "Show current instance config (or item)";
    }


    public String getUsage() {
        return "dw config show [item]";
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() > 0) {
            return (doShowConfig(cmdline));
        }
        return (doShowConfig());
    }

    private DWCommandResponse doShowConfig(String item) {
        String text = "";

        if (dwProto.getConfig().containsKey(item)) {
            String value = StringUtils.join(dwProto.getConfig().getStringArray(item), ", ");

            text += item + " = " + value;
            return (new DWCommandResponse(text));
        }
        else {
            return (new DWCommandResponse(false, DWDefs.RC_CONFIG_KEY_NOT_SET, "Key '" + item + "' is not set."));
        }


    }


    public boolean validate(String cmdline) {

        return true;
    }


    @SuppressWarnings("unchecked")
    private DWCommandResponse doShowConfig() {
        StringBuilder text = new StringBuilder();

        text.append("Current protocol handler configuration:\r\n\n");

        for (Iterator<String> i = dwProto.getConfig().getKeys(); i.hasNext(); ) {
            String key = i.next();
            //String value = StringUtils.join(dwProto.getConfig().getStringArray(key), ", ");
            String value = dwProto.getConfig().getProperty(key).toString();

            text.append(key).append(" = ").append(value).append("\r\n");

        }

        return (new DWCommandResponse(text.toString()));
    }


}
