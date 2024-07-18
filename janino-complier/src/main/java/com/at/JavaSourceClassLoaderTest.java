package com.at;

import org.codehaus.janino.JavaSourceClassLoader;

import java.io.File;

public class JavaSourceClassLoaderTest {

    public static void main(String[] args) throws Exception {

//        ClassLoader cl = new JavaSourceClassLoader();
        ClassLoader cl =  new JavaSourceClassLoader(
                JavaSourceClassLoaderTest.class.getClassLoader(),
                new File[]{new File("/Users/wenzhilong/warehouse/space/questions_/janino-complier/files/")},
                "utf-8"
        );

        Object o = cl.loadClass("pkg1.A").newInstance();
        ((Runnable) o).run();
    }
}
