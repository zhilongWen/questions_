package com.at.t6;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * @create 2023-07-22
 */
public class CustomLoader extends ClassLoader implements Closeable {

    static {
        registerAsParallelCapable();
    }

    private CustomLoader() {
        super(CustomLoader.class.getClassLoader());
    }

    private static volatile CustomLoader customLoader;

    private final Map<String, Object> objectPool = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final List<CustomJar> jars = Lists.newArrayList();

    public static CustomLoader getInstance() {
        if (null == customLoader) {
            synchronized (CustomLoader.class) {
                if (null == customLoader) {
                    customLoader = new CustomLoader();
                }
            }
        }
        return customLoader;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        final String path = String.join("", name.replace(".", "/"), ".class");

        System.out.println(path);


        for (CustomJar jar : jars) {
            final ZipEntry entry = jar.jarFile.getEntry(path);
            if (Objects.nonNull(entry)) {
                try {
                    final int index = name.lastIndexOf(".");
                    if (index != -1) {
                        final String packageName = name.substring(0, index);
                        definePackageInternal(packageName, jar.jarFile.getManifest());
                    }

                    final byte[] data = ByteStreams.toByteArray(jar.jarFile.getInputStream(entry));
                    return defineClass(name, data, 0, data.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        throw new ClassNotFoundException(String.format("Class name is %s not found.", name));
    }


    private void definePackageInternal(final String packageName, final Manifest manifest) {
        if (null != getPackage(packageName)) return;

        Attributes attributes = manifest.getMainAttributes();
        String specTitle = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
        String specVersion = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
        String specVendor = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
        String implTitle = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        String implVersion = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        String implVendor = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);

        definePackage(packageName, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, null);

    }

//    @Override
//    protected Enumeration<URL> findResources(String name) throws IOException {
//        ArrayList<URL> resource = Lists.newArrayList();
//
//        for (CustomJar jar : jars) {
//            final JarEntry entry = jar.jarFile.getJarEntry(name);
//            if (Objects.nonNull(entry)){
//               try {
//                   final String format = String.format("jar:file:%s!/%s", jar.sourcePath.getAbsolutePath(), name);
//                   System.out.println("format = " + format);
//                   resource.add(new URL(format));
//               }catch (final MalformedURLException e){
//
//               }
//            }
//        }
//
//        return Collections.enumeration(resource);
//
//    }

    @Override
    public void close() {
        for (CustomJar each : jars) {
            try {
                each.jarFile.close();
            } catch (final IOException ex) {
                System.out.println("关闭jar");
                ex.printStackTrace();
            }
        }
    }



    public List<Object> loadExtendJar(final String path) {

        File[] jarFiles = JarPathBuilder.getJatPath(path).listFiles(file -> {
            final String fileName = file.getName();
            System.out.println("jar file name = " + fileName);
            return fileName.endsWith(".jar");
        });

        if (null == jarFiles) {
            return Collections.emptyList();
        }

        List<Object> results = new ArrayList<>();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            for (File file : Objects.requireNonNull(jarFiles)) {

                outputStream.reset();

                final JarFile jar = new JarFile(file, true);

                jars.add(new CustomJar(jar, file));

                final Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {

                    final JarEntry entry = entries.nextElement();

                    final String entryName = entry.getName();

                    if (entryName.endsWith(".class") && !entryName.contains("$")) {
                        final String className = entryName.substring(0, entryName.length() - 6).replaceAll("/", ".");

                        final Object instance = getOrCreateInstance(className);

                        if (Objects.nonNull(instance)) {
                            results.add(instance);
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return results;


    }

    private <T> T getOrCreateInstance(final String className) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        if (objectPool.containsKey(className)) {
            System.out.println("从缓存中获取的className为【" + className + "】");
            return (T) objectPool.get(className);
        }

        lock.lock();

        try {

            System.out.println("开始创建className为【" + className + "】的实例");

            Object instance = objectPool.get(className);

            if (Objects.isNull(instance)) {
                final Class<?> clazz = Class.forName(className, true, this);
//                final Object newInstance = clazz.newInstance();
                instance = clazz.getDeclaredConstructor().newInstance();
                objectPool.put(className, instance);
            }

            System.out.println("创建className为【" + className + "】的实例结束");
            return (T) instance;

        } finally {
            lock.unlock();
        }

    }


    static class CustomJar {

        private final JarFile jarFile;

        private final File sourcePath;

        CustomJar(final JarFile jarFile, final File sourcePath) {
            this.jarFile = jarFile;
            this.sourcePath = sourcePath;
        }

    }

}
