package com.at


import java.lang.reflect.Method
import scala.reflect.runtime.universe.{Type, TypeTag, runtimeMirror, typeOf, typeTag}
import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.IMain
import scala.tools.reflect.ToolBox

object ScalaComplierTest {

  private val toolbox:ToolBox[scala.reflect.runtime.universe.type] = runtimeMirror(this.getClass.getClassLoader).mkToolBox()

  def main(args: Array[String]): Unit = {

    // 创建编译器设置
    val settings = new Settings()
    settings.usejavacp.value = true

    // 创建Scala解释器
    val interpreter = new IMain(settings)

    // 定义要动态编译的Scala代码
//    val scalaCode =
//      """
//        |class HelloWorld {
//        |  def sayHello(): Unit = {
//        |    println("Hello, World!")
//        |  }
//        |}
//        |""".stripMargin
//
//    // 动态编译并加载类
//    interpreter.compileString(scalaCode)
//
//    // 创建类实例并调用方法
//    val helloWorld = interpreter.classLoader.loadClass("HelloWorld").newInstance()
//    helloWorld.getClass.getMethod("sayHello").invoke(helloWorld)

    val coder = """
                   |public class Test {
                   |    public static void f(int[] arr){
                   |        if (arr == null){
                   |            System.out.println("arr is null");
                   |            return;
                   |        }
                   |        for (int i = 0; i < arr.length; i++) {
                   |            System.out.println("idx = " + i + " => " + arr[i]);
                   |        }
                   |    }
                   |    public static void f1(){
                   |        System.out.println("hello");
                   |    }
                   |}
                   |""".stripMargin

    interpreter.compileString(coder)
    val test = interpreter.classLoader.loadClass("Test").newInstance()
    test.getClass.getMethod("f1").invoke(test)

  }

}
