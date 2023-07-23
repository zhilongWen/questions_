package com.at.t9;

/**
 * @create 2023-07-24
 */
public class FileListenerMain {

    public static void main(String[] args) throws Exception {

        final FileMonitor monitor = new FileMonitor(3000);

        monitor.monitor("C:\\Users\\zero\\Videos\\jar",new FilterListener());

        monitor.start();


    }

}
