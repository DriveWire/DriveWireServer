package com.groupunix.drivewireserver.dwdisk.filesystem;

import com.groupunix.drivewireserver.dwdisk.DWDisk;
import com.groupunix.drivewireserver.dwdisk.DWDiskSector;
import com.groupunix.drivewireserver.dwexceptions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class DWFileSystem {
    protected final DWDisk disk;

    DWFileSystem(DWDisk disk) {
        this.disk = disk;
    }


    public abstract List<DWFileSystemDirEntry> getDirectory(String path) throws IOException, DWFileSystemInvalidDirectoryException, DWDiskInvalidSectorNumber;

    public abstract boolean hasFile(String filename) throws IOException;

    public abstract ArrayList<DWDiskSector> getFileSectors(String filename) throws DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException;

    public abstract DWFileSystemDirEntry getDirEntry(String filename) throws DWFileSystemFileNotFoundException, IOException, DWFileSystemInvalidDirectoryException;

    public abstract byte[] getFileContents(String filename) throws DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException;

    public abstract void addFile(String filename, byte[] filecontents) throws DWFileSystemFullException, DWFileSystemInvalidFilenameException, DWFileSystemFileNotFoundException, DWFileSystemInvalidFATException, IOException, DWDiskInvalidSectorNumber, DWFileSystemInvalidDirectoryException;

    public abstract void format() throws DWInvalidSectorException, DWSeekPastEndOfDeviceException, DWDriveWriteProtectedException, IOException;

    public abstract String getFSName();

    public abstract boolean isValidFS();

    public abstract List<String> getFSErrors();


}
