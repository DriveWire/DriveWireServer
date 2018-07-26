package com.groupunix.drivewireserver.dwdisk.filesystem;

import com.groupunix.drivewireserver.dwdisk.DWDisk;
import com.groupunix.drivewireserver.dwdisk.DWDiskSector;
import com.groupunix.drivewireserver.dwexceptions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DWLW16FileSystem extends DWFileSystem {


    private static final String FSNAME = "LW16";
    private final List<String> fserrors = new ArrayList<>();

    DWLW16FileSystemSuperBlock superblock;


    public DWLW16FileSystem(DWDisk disk) throws IOException, DWDiskInvalidSectorNumber {
        super(disk);

        this.superblock = new DWLW16FileSystemSuperBlock(this.disk.getSector(0));
    }


    @Override
    public List<String> getFSErrors() {
        return this.fserrors;
    }


    @Override
    public List<DWFileSystemDirEntry> getDirectory(String path)
            throws IOException, DWDiskInvalidSectorNumber {

        List<DWFileSystemDirEntry> res = new ArrayList<>();

        if (path == null) {
            res.addAll(this.getRootDirectory());
        }
        else {
            System.out.println("req dir: ");
        }

        return res;
    }


    private List<DWLW16FileSystemDirEntry> getRootDirectory() throws DWDiskInvalidSectorNumber, IOException {
        List<DWLW16FileSystemDirEntry> res = new ArrayList<>();

        // get inode 0

        System.out.println("first inode: " + this.superblock.getFirstinodeblock());
        System.out.println("first data: " + this.superblock.getFirstdatablock());
        System.out.println("data blocks: " + this.superblock.getDatablocks());
        System.out.println("data bmps: " + this.superblock.getDatabmpblocks());
        System.out.println("tot inodes: " + this.superblock.getInodes());
        System.out.println();


        DWLW16FileSystemInode in0 = new DWLW16FileSystemInode(0, this.disk.getSector(this.superblock.getFirstinodeblock() + 1).getData());

        System.out.println(in0.toString());

        return res;
    }


    @Override
    public boolean hasFile(String filename) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public ArrayList<DWDiskSector> getFileSectors(String filename) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public DWFileSystemDirEntry getDirEntry(String filename) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public byte[] getFileContents(String filename) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void addFile(String filename, byte[] filecontents) {
        // TODO Auto-generated method stub

    }


    @Override
    public void format() {
        // TODO Auto-generated method stub

    }


    @Override
    public String getFSName() {
        return DWLW16FileSystem.FSNAME;
    }


    @Override
    public boolean isValidFS() {
        // valid superblock?
        if (this.superblock.isValid()) {
            // image size checks

            return (this.disk.getSectors().size() < 65536) && (this.superblock.getFirstdatablock() < this.disk.getSectors().size()) && (this.superblock.getFirstinodeblock() < this.disk.getSectors().size());

        }


        return false;
    }

}
