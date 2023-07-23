package com.at.t9;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * @create 2023-07-24
 */
public class FilterListener extends FileAlterationListenerAdaptor {

    public FilterListener() {
        super();
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
//        System.out.println("filter listener onStart...");
    }

    // =============================================================
    // ======= directory
    // =============================================================

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("directory = " + directory.getName() + " create...");
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("directory = " + directory.getName() + " change...");
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("directory = " + directory.getName() + " delete...");
    }

    // =============================================================
    // ======= file
    // =============================================================

    @Override
    public void onFileCreate(File file) {
        System.out.println("file = " + file.getName() + " create...");
        handler(file);
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("file = " + file.getName() + " change...");
        handler(file);
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("file = " + file.getName() + " delete...");
        handler(file);
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
//        System.out.println("filter listener onStop...");
    }

    // =============================================================
    // ======= handler
    // =============================================================

    public void handler(File file){
        LoadExtJarFactory.close();
        LoadExtJarFactory.createClassLoader();
    }

}
