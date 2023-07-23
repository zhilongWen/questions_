package com.at.t7;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * @author zero
 * @create 2023-07-23
 */
public class CustomLoader extends ClassLoader implements Closeable {

    private static final CustomLoader customLoader = new CustomLoader();

    private CustomLoader() {
        super(CustomLoader.class.getClassLoader());
    }

    public static CustomLoader getInstance() {
        return customLoader;
    }

    static {
        registerAsParallelCapable();
    }

    private final Map<String, Object> objectPool = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final List<CustomJar> jars = Lists.newArrayList();

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        System.out.println("findClass parma = " + name);

        final String classNamePath = name.replace('.', '/').concat(".class");
        System.out.println("classNamePath = " + classNamePath);

        for (CustomJar jar : jars) {

            final ZipEntry entry = jar.jarFile.getEntry(classNamePath);
            System.out.println("entry = " + entry.toString());

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

    @Override
    public void close() throws IOException {

    }


    public List<Object> loadExtJar(String path) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        final File jarPath = JarPathBuilder.getJarPath(path);

        final File[] jarFiles = jarPath.listFiles(file -> {
            final String fileName = file.getName();

            System.out.println("fileName = " + fileName);

            return fileName.endsWith(".jar");
        });

        if (Objects.isNull(jarFiles)) return Collections.emptyList();

        List<Object> results = new ArrayList<>();

        try ( ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            for (File file : Objects.requireNonNull(jarFiles)) {

                outputStream.reset();

                final JarFile jar = new JarFile(file, true);

                System.out.println("jar name = " + jar.getName());

                jars.add(new CustomJar(jar, file));

                final Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {

                    final JarEntry entry = entries.nextElement();

                    final String entryName = entry.getName();

                    System.out.println("entryName = " + entryName);

                    if (entryName.endsWith(".class") && !entryName.contains("$")) {

                        // 此处如何处理，findClass 方法中就应该如何逆处理

                        final String substringClassName = entryName.substring(0, entryName.length() - 6);
                        System.out.println("substringClassName = " + substringClassName);

                        final String className = substringClassName.replaceAll("/", ".");

                        System.out.println("className = " + className);

                        final Object instance = getOrCreateInstance(className);

                        if (Objects.nonNull(instance)) {
                            results.add(instance);
                        }
                    }
                }
            }
        }

        return results;

    }


    public <T> T getOrCreateInstance(final String className) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        if (objectPool.containsKey(className)) {
            System.out.println("从缓存中获取对象 className = " + className);
            return (T) objectPool.get(className);
        }

        lock.lock();

        try {

            System.out.println("开始创建 className = " + className + " 的实例");

            Object instance = objectPool.get(className);

            if (Objects.isNull(instance)) {

                final Class<?> clazz = Class.forName(className, true, this);

                instance = clazz.getDeclaredConstructor().newInstance();

                objectPool.put(className, instance);

            }

            System.out.println("创建 className = " + className + " 的实例结束");

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
