package com.at.t9;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @create 2023-07-23
 */
public class LoadExtJarFactory {

//    private static final String DEFAULT_EXT_PLUGIN_PATH = "/ext-lib/";
    private static final String DEFAULT_EXT_PLUGIN_PATH = "C:\\Users\\zero\\Videos\\jar";

    public static final long interval = 1000L;
    private static FileMonitor monitor;


    static ConsumUrlClassLoader classLoader;

    static {
        try {
            createClassLoader();

            monitor = new FileMonitor(interval);
            final FilterListener listener = new FilterListener();
            monitor.monitor(DEFAULT_EXT_PLUGIN_PATH, listener);
            monitor.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void close(){
        try {
            classLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void createClassLoader(){
        classLoader = new ConsumUrlClassLoader(new URL[]{});

        final File file = new File(DEFAULT_EXT_PLUGIN_PATH);

        final Collection<File> classFiles = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(".jar"), DirectoryFileFilter.INSTANCE);

        classFiles.stream()
                .forEach(jarF -> {
                    try {
                        System.out.println("jarF = " + jarF.getName());
                        System.out.println("jarF = " + jarF.toURI());
                        System.out.println("jarF = " + jarF.toURI().toURL());


                        classLoader.addURL(jarF.toURI().toURL());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                });
    }

    public static void getClass(String classPath){

        try {

            final Class<?> clazz = Class.forName(classPath, true, classLoader);

            final Object newInstance = clazz.getDeclaredConstructor().newInstance();

            final Method m = clazz.getMethod("say", String.class);

            m.invoke(newInstance,"ppppp");


        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }




}
