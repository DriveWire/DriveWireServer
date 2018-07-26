package com.groupunix.drivewireserver.uicommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.DWUIClientThread;
import com.groupunix.drivewireserver.DriveWireServer;
import com.groupunix.drivewireserver.dwcommands.DWCommand;
import com.groupunix.drivewireserver.dwcommands.DWCommandResponse;
import com.groupunix.drivewireserver.dwexceptions.DWConnectionNotValidException;
import com.groupunix.drivewireserver.dwexceptions.DWPortNotValidException;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import com.groupunix.drivewireserver.dwprotocolhandler.DWVSerialProtocol;

public class UICmdInstancePortStatus extends DWCommand {

    private DWUIClientThread dwuithread = null;

    private DWVSerialProtocol gproto;

    public UICmdInstancePortStatus(DWUIClientThread dwuiClientThread) {
        this.dwuithread = dwuiClientThread;
    }


    public UICmdInstancePortStatus(DWVSerialProtocol dwProto) {
        this.gproto = dwProto;
    }


    @Override
    public String getCommand() {
        return "portstatus";
    }


    @Override
    public String getShortHelp() {
        return "show port status";
    }

    @Override
    public String getUsage() {
        return "ui instance portstatus";
    }

    @Override
    public DWCommandResponse parse(String cmdline) {
        StringBuilder res = new StringBuilder();

        if (this.gproto == null) {
            if ((DriveWireServer.isValidHandlerNo(this.dwuithread.getInstance()) && (DriveWireServer.getHandler(this.dwuithread.getInstance()).hasVSerial()))) {
                gproto = (DWVSerialProtocol) DriveWireServer.getHandler(this.dwuithread.getInstance());
            }
            else {
                return (new DWCommandResponse(false, DWDefs.RC_INSTANCE_WONT, "The operation is not supported by this instance"));
            }
        }


        if (!(gproto == null) && !(gproto.getVPorts() == null)) {


            for (int p = 0; p < gproto.getVPorts().getMaxPorts(); p++) {
                if (!gproto.getVPorts().isNull(p) && (p != gproto.getVPorts().getMaxNPorts() - 1)) {
                    try {
                        if (p < gproto.getVPorts().getMaxNPorts()) {
                            res.append("N|");
                        }
                        else {
                            res.append("Z|");
                        }

                        res.append(gproto.getVPorts().prettyPort(p)).append("|");

                        if (gproto.getVPorts().isOpen(p)) {
                            res.append("open|");

                            res.append(gproto.getVPorts().getOpen(p)).append("|");

                            res.append(gproto.getVPorts().getUtilMode(p)).append("|");

                            res.append(DWUtils.prettyUtilMode(gproto.getVPorts().getUtilMode(p))).append("|");

                            res.append(gproto.getVPorts().bytesWaiting(p)).append("|");

                            res.append(gproto.getVPorts().getConn(p)).append("|");

                            if (gproto.getVPorts().getConn(p) > -1) {
                                try {
                                    res.append(gproto.getVPorts().getHostIP(p)).append("|");
                                    res.append(gproto.getVPorts().getHostPort(p)).append("|");

                                }
                                catch (DWConnectionNotValidException e) {
                                    res.append("||");
                                }
                            }
                            else {
                                res.append("||");
                            }

                            res.append(gproto.getVPorts().getPD_INT(p)).append("|");
                            res.append(gproto.getVPorts().getPD_QUT(p));

                            // res += new String(gproto.getVPorts().getDD(p));


                        }
                        else {
                            res.append("closed");
                        }
                    }
                    catch (DWPortNotValidException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    res.append("\r\n");
                }
            }
        }
		
		/*
		Iterator<DWUIClientThread> itr = DriveWireServer.getDWUIThread().getUIClientThreads().iterator(); 
		
		while(itr.hasNext()) 
		{	
			DWUIClientThread client = itr.next();
			
			// filter for instance
			if ((client.getInstance() == -1) || (client.getInstance() == this.dwuithread.getInstance()))
			{
				res += "U|" + client.getInstance() + "|" + client.getState() + "|" + client.getCurCmd() + "|" + client.getSocket().getInetAddress().getHostAddress() + "|" + client.getSocket().getPort();
				res += "\r\n";
				
			}
		}
		*/

        return (new DWCommandResponse(res.toString()));
    }

    public boolean validate(String cmdline) {
        return (true);
    }
}
