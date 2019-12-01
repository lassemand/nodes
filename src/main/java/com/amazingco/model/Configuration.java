package com.amazingco.model;

import com.beust.jcommander.Parameter;

public class Configuration {

    @Parameter(names = {"-p", "--path"}, description = "number of participants")
    private String backupPath = "test.bin";


    public String getBackupPath() {
        return backupPath;
    }
}
