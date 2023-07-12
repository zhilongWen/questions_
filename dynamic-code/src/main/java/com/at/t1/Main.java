package com.at.t1;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Main {

    // https://www.cnblogs.com/kitor/p/12709436.html

    public static void main(String[] args) throws Exception {
        //1.创建需要动态编译的代码字符串
        String nr = "\r\n";//回车换行
        String source = "package com.at;" + nr +
                "    public class Hello{" + nr +
                "     public static void main(String[] args){" + nr +
                "     System.out.println(\"helloworld!\");" + nr +
                "}" + nr +
                "}";
        //2.将预动态编译的代码写入文件中1:创建临时目录 2:写入临时文件
        File dir = new File(System.getProperty("user.dir") + "/temp");//临时目录
        //如果/temp目录不存在创建temp目录
        if (!dir.exists()) {
            dir.mkdir();
        }
        FileWriter writer = new FileWriter(new File(dir, "Hello.java"));
        writer.write(source);//将字符串写入文件中
        writer.flush();
        writer.close();
        //3:取得当前系统java编译器
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        //4:获取一个文件管理器StandardJavaFileManage
        StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(null, null, null);
        //5.文件管理器根与文件连接起来
        Iterable it = javaFileManager.getJavaFileObjects(new File(dir, "Hello.java"));
        //6.创建编译的任务
        JavaCompiler.CompilationTask task = javaCompiler.getTask(null,
                javaFileManager, null, Arrays.asList("-d", "./temp"), null, it);
        //执行编译
        task.call();
        javaFileManager.close();

        //动态执行
        Class cls = Class.forName("com.at.Hello");//返回与带有给定字符串名的类 或接口相关联的 Class 对象。
        Method method = cls.getDeclaredMethod("call", String.class);//返回一个 Method 对象，该对象反映此 Class 对象所表示的类或接口的指定已声明方法
        String result= (String)method.invoke(null, null);//静态方法第一个参数可为null,第二个参数为实际传参
        System.out.println(result);


    }
}
