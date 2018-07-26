package com.groupunix.drivewireserver.dwcommands;

import com.groupunix.drivewireserver.DWDefs;
import com.groupunix.drivewireserver.dwprotocolhandler.DWUtils;
import org.apache.commons.vfs2.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DWCmdServerList extends DWCommand {

    DWCmdServerList(DWCommand parent) {
        setParentCmd(parent);
    }


    public String getCommand() {
        return "list";
    }


    public String getShortHelp() {
        return "List contents of file on server";
    }


    public String getUsage() {
        return "dw server list URI/path";
    }

    public DWCommandResponse parse(String cmdline) {
        if (cmdline.length() == 0) {
            return (new DWCommandResponse(false, DWDefs.RC_SYNTAX_ERROR, "dw server list requires a URI or local file path as an argument"));
        }
        return (doList(cmdline));
    }


    private DWCommandResponse doList(String path) {
        FileSystemManager fsManager;
        InputStream ins = null;
        FileObject fileobj = null;
        FileContent fc = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            fsManager = VFS.getManager();

            path = DWUtils.convertStarToBang(path);

            fileobj = fsManager.resolveFile(path);

            fc = fileobj.getContent();

            ins = fc.getInputStream();

            byte[] buffer = new byte[256];
            int sz;

            while ((sz = ins.read(buffer)) >= 0) {
                baos.write(buffer, 0, sz);
            }

            ins.close();
        }
        catch (FileSystemException e) {
            return (new DWCommandResponse(false, DWDefs.RC_SERVER_FILESYSTEM_EXCEPTION, e.getMessage()));
        }
        catch (IOException e) {
            return (new DWCommandResponse(false, DWDefs.RC_SERVER_IO_EXCEPTION, e.getMessage()));
        }
        finally {
            DWCmdServerPrint.closeObjects(ins, fileobj, fc);

        }

        return (new DWCommandResponse(baos.toByteArray()));
    }

    public boolean validate(String cmdline) {
        return true;
    }

}
