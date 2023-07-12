package com.at.t4;

public class FileRunner {

    //https://cloud.tencent.com/developer/article/2013675

    public static void main(String[] args) throws Exception {
        FileMonitor fileMonitor = new FileMonitor(1000);
        fileMonitor.monitor("./files/", new FileListener());
        fileMonitor.start();
    }

}
