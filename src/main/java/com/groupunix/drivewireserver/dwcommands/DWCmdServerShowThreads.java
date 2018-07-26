package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class DWCmdServerShowThreads extends DWCommand {

    DWCmdServerShowThreads(DWCommand parent) {
        setParentCmd(parent);
    }

    public String getCommand() {
        return "threads";
    }


    public String getShortHelp() {
        return "Show server threads";
    }


    public String getUsage() {
        return "dw server show threads";
    }

    public DWCommandResponse parse(String cmdline) {
        StringBuilder text = new StringBuilder();

        text.append("\r\nDriveWire Server Threads:\r\n\n");

        Thread[] threads = getAllThreads();

        for (Thread thread : threads) {
            if (thread != null) {
                text.append(String.format("%40s %3d %-8s %-14s", shortenname(thread.getName()), thread.getPriority(), thread.getThreadGroup().getName(), thread.getState().toString())).append("\r\n");

            }
        }

        return (new DWCommandResponse(text.toString()));
    }


    private Object shortenname(String name) {

        // TODO: Shorten the name?
        return name;
    }

    private Thread[] getAllThreads() {
        final ThreadGroup root = DWUtils.getRootThreadGroup();
        final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
        int nAlloc = thbean.getThreadCount();
        int n;
        Thread[] threads;
        do {
            nAlloc *= 2;
            threads = new Thread[nAlloc];
            n = root.enumerate(threads, true);
        }
        while (n == nAlloc);
        Thread[] copy = new Thread[threads.length];
        System.arraycopy(threads, 0, copy, 0, threads.length);
        return copy;
    }

    public boolean validate(String cmdline) {
        return (true);
    }

}
