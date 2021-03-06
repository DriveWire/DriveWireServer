package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class UICmdServerShowNet extends DWCommand {

    @Override
    public String getCommand() {
        return "net";
    }


    @Override
    public String getShortHelp() {
        return "show available network interfaces";
    }

    @Override
    public String getUsage() {
        return "ui server show net";
    }

    @Override
    public DWCommandResponse parse(String cmdline) {
        StringBuilder res = new StringBuilder();

        Enumeration<NetworkInterface> nets;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {

                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    res.append(inetAddress.getHostAddress()).append("|").append(netint.getName()).append("|").append(netint.getDisplayName()).append("\r\n");
                }

            }

        }
        catch (SocketException e) {
            return (new DWCommandResponse(false, DWDefs.RC_NET_IO_ERROR, e.getMessage()));
        }


        return (new DWCommandResponse(res.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
