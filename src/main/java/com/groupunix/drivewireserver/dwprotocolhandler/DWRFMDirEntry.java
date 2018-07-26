package com.groupunix.drivewireserver.dwprotocolhandler;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;


public class DWRFMDirEntry {
    private String fileName; // 0 - 47

    // 48 - 52

    private byte filePerms = 0; // 57


    public DWRFMDirEntry(FileObject fo) throws FileSystemException {
        // TODO sanity checks

        if (fo != null) {
            this.fileName = fo.getName().getBaseName();

            if (fo.getType() == FileType.FOLDER) {
                this.filePerms = (byte) 0x80;
            }

            if (fo.isReadable()) {
                this.filePerms += (byte) 4;
            }

            if (fo.isWriteable()) {
                this.filePerms += (byte) 2;
            }

            // TODO lat mod time
        }
    }


    public byte[] getEntry() {
        byte[] res = new byte[64];

        if (this.fileName != null) {
            //TODO  incomplete

            System.arraycopy(this.fileName.getBytes(), 0, res, 0, Math.max(48, this.fileName.length()));

            res[57] = this.filePerms;

        }

        return res;
    }
}
