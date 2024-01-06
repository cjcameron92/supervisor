package gg.llama.supervisor.api;


import java.io.File;

public interface Config {

    void save();

    void reload();

    File getFile();

}
