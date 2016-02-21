package com.github.chengpohi.collection

import scala.reflect.runtime.universe._

/**
 * elasticshell
 * Created by chengpohi on 2/17/16.
 */
object JsonCollection {

  sealed trait Val extends Any {
    def value: Any

    def apply(i: Int): Val = this.asInstanceOf[Arr].value(i)

    def apply(s: java.lang.String): Val = this.asInstanceOf[Obj].value.find(_._1 == s).get._2

    def toJson: String
  }

  case class Str(value: java.lang.String) extends AnyVal with Val {
    override def toJson: String = "\"" + value + "\""
  }

  case class Obj(value: (java.lang.String, Val)*) extends AnyVal with Val {
    override def toJson: String = value.map {
      case (n, v) => "{\"" +  n + "\":" + v.toJson + "}"
    }.mkString(",")
  }

  case class Arr(value: Val*) extends AnyVal with Val {
    override def toJson: String = "[" + value.map(i => i.toJson).mkString(",") + "]"
  }

  case class Tuple(value: Val*) extends AnyVal with Val {
    override def toJson: String = "(" + value.map(i => i.toJson).mkString(",") + ")"
  }

  case class Num(value: Double) extends AnyVal with Val {
    override def toJson: String = value.toString
  }

  case object False extends Val {
    def value = false

    override def toJson: String = value.toString
  }

  case object True extends Val {
    def value = true

    override def toJson: String = value.toString
  }

  case object Null extends Val {
    def value = null

    override def toJson: String = value.toString
  }

  implicit def numToDouble(n: Num): java.lang.Double = {
    n.value
  }

  implicit def valToGeneric[T](v: Val): T = {
    v.value.asInstanceOf[T]
  }

  def chooseType(tp: Type) = {
    if (tp =:= typeOf[Int]) {
      typeTag[Int]
    } else {
      typeTag[String]
    }
  }

  def extract[T](v: Val)(implicit tag: TypeTag[T]): T = {
    if (tag.tpe <:< typeOf[List[_]]) {
      val subType: Type = tag.tpe.typeArgs.head
      v.asInstanceOf[Arr].value.toList.map(i => extract(i)(chooseType(subType))).asInstanceOf[T]
    } else if (tag.tpe =:= typeOf[Int]) {
      v.asInstanceOf[Num].value.toInt.asInstanceOf[T]
    } else {
      v.value.asInstanceOf[T]
    }
  }
}

