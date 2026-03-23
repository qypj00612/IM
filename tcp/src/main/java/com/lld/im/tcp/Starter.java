package com.lld.im.tcp;

import com.lld.im.codec.config.BootstrapConfig;
import com.lld.im.tcp.server.ImServer;
import com.lld.im.tcp.server.ImWebServer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class Starter {
    public static void main(String[] args) {
        if (args.length >0) {
            start(args[0]);
        }
    }

    public static void start(String path){
        try {
            Yaml yaml = new Yaml();
            FileInputStream fileInputStream = new FileInputStream(path);
            BootstrapConfig bootstrapConfig = yaml.loadAs(fileInputStream, BootstrapConfig.class);
            new ImServer(bootstrapConfig.getIm()).start();
            new ImWebServer(bootstrapConfig.getIm()).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(500);
        }
    }
}
