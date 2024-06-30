package com.at

import scala.reflect.runtime.universe.{Type, typeOf, typeTag}
import reflect.runtime.universe.TypeTag;

// https://juejin.cn/post/7009260754260099079

object MM {

  def main(args: Array[String]): Unit = {

    val tt = typeTag[List[List[String]]]
    println(tt) // TypeTag[List[List[String]]]
    println(tt.tpe) // List[List[String]]
    println(typeOf[List[List[String]]]) // List[List[String]]

    //

    val a1 = List[List[String]]()
    println(getTypeTag(a1).toString) // List[List[String]]

    //

    typeOfMap(Map[String, String]()) // this is a Map[Int,Int]

    //
    
    typeOfMap1(Map[String, Int]())
    // key's type is String!
    // value's type is Int!

    // Type 之间的比较方法。设 A 和 B 都属于 Type 类型：
    // A =:= B ，表示 A 与 B 是同一种类型，包含它们的类型参数
    // A <:< B ，表示 A 是 B 的子类型，或者是同一种类型。

    //
    println(typeTag[B[Father]].tpe <:< typeTag[B[Father]].tpe) // true
    println(typeTag[B[Father]].tpe =:= typeTag[B[Father]].tpe) // true

    println(typeTag[B[Son]].tpe <:< typeTag[B[Father]].tpe)  // false
    println(typeTag[A[Father]].tpe <:< typeTag[B[Father]].tpe)  // true
    println(typeTag[A[Son]].tpe <:< typeTag[B[Father]].tpe)     // false

    // +U +T  倘若 T 和 U 是协变的，这意味着对于 B[Father] 类型，A[Father] ， A[Son] 或者是 B[Son] 都可认为是它的子类型（包括它自身）。对于 A[Father] 而言，A[Son] 又可以作为它的子类型。
    


  }

  private def typeOfMap1[K: TypeTag, V: TypeTag](map: Map[K, V]): Unit = {

    typeOf[K] match {
      case ktp if ktp =:= typeOf[String] => println("key's type is String!")
      case ktp if ktp =:= typeOf[Int] => println("key's type is Int!")
      case _ => println("key is neither String or Int.")
    }

    typeOf[V] match {
      case vtp if vtp =:= typeOf[String] => println("value's type is String!")
      case vtp if vtp =:= typeOf[Int] => println("value's type is Int!")
      case _ => println("value is neither String or Int.")
    }

  }

  private def typeOfMap(map: Map[_, _]): Unit = {
    map match {
      // 实际上无论是何种类型参数的 Map，在编译时会全部被擦除成 Map[Any,Any]，因此总会执行第一个分支
      case _: Map[Int, Int] => println("this is a Map[Int,Int]")
      case _: Map[Int, String] => println("this is a Map[Int,String]")
      case _ => println("not a valid map.")
    }
  }

  private def getTypeTag[T: TypeTag](o: T): Type = typeOf[T]

}

trait B[+T] {}

trait A[+U] extends B[U] {}


class Father {}

class Son extends Father {}