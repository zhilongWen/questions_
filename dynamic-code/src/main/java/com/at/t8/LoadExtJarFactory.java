package com.at.t8;

import com.at.t8.ConsumUrlClassLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

/**
 * @create 2023-07-23
 */
public class LoadExtJarFactory {

//    private static final String DEFAULT_EXT_PLUGIN_PATH = "/ext-lib/";
    private static final String DEFAULT_EXT_PLUGIN_PATH = "C:\\Users\\zero\\Videos\\jar";

    static ConsumUrlClassLoader classLoader;

    static {
        createClassLoader();
    }

    static void createClassLoader(){
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
