package com.at.t10;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * @create 2023-10-21
 */
public class M3 {
    public static void main(String[] args) throws Exception {


        String javaFile = "package com.at.test;\n"
                + "\n"
                + "public class Main {\n"
                + "    public static void main(String[] args) {\n"
                + "        System.out.println(\"........\");\n"
                + "    }\n"
                + "}\n";


        final boolean compile = executeCompile(javaFile, "Main", "com.at.test");

        System.out.println("========================================");

        String classFile = "D:\\tmp\\com\\at\\test\\Main.class";


        final FileInputStream in = new FileInputStream(classFile);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int read;

        while ((read = in.read(buf)) != -1){
            bos.write(buf,0,read);
        }

        final byte[] classBytes = bos.toByteArray();
        System.out.println(classBytes);


        System.out.println("=======================================");



        final URL[] urls = {new URL("file:" + classFile)};

        URLClassLoader classLoader = new URLClassLoader(urls);
        final Class<?> main = classLoader.loadClass("com.at.test.Main");

        final Object instance = main.newInstance();

        System.out.println(instance);

        final Method mainMethod = main.getMethod("main", String[].class);
        mainMethod.invoke(null,(Object) null);

    }

    public static boolean executeCompile(String fileSource, String className, String packageName) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject fileObject = (JavaFileObject) new JavaStringObject(className, fileSource);
        // 编译过程
        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, null, null,
                Arrays.asList("-d", "/tmp"), null,
                Arrays.asList(fileObject));
        if (!task.call()) {
            System.out.println("编译失败!");
            return false;
        } else {
            System.out.println("编译成功！");

            return true;
        }




    }

    static class JavaStringObject extends SimpleJavaFileObject {
        private String code;

        public JavaStringObject(String name, String code) {
            super(URI.create(name + ".java"), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
