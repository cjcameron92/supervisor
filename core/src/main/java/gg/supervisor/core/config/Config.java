package gg.supervisor.core.config;


import java.io.File;

public interface Config {

    void save();

    void reload();

    File getFile();

}
