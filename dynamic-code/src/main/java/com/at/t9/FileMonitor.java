package com.at.t9;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * @create 2023-07-24
 */
public class FileMonitor {

    private FileAlterationMonitor monitor;

    public FileMonitor(long interval){
        monitor = new FileAlterationMonitor(interval);
    }

    public void monitor(String path, FileAlterationListener listener){

        System.out.println("监听路径 = " + path);

        File file = new File(path);
        final FileAlterationObserver observer = new FileAlterationObserver(file);
        monitor.addObserver(observer);

        observer.addListener(listener);
    }

    public void start() throws Exception {
        System.out.println("启动监听器...");
        monitor.start();
    }

}
