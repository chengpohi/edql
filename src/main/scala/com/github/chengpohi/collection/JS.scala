package com.github.chengpohi.collection


/**
 * elasticshell
 * Created by chengpohi on 2/17/16.
 */
object Js {
  sealed trait Val extends Any {
    def value: Any

    def apply(i: Int): Val = this.asInstanceOf[Arr].value(i)

    def apply(s: java.lang.String): Val = this.asInstanceOf[Obj].value.find(_._1 == s).get._2
  }

  case class Str(value: java.lang.String) extends AnyVal with Val

  case class Obj(value: (java.lang.String, Val)*) extends AnyVal with Val

  case class Arr(value: Val*) extends AnyVal with Val
  case class Tuple(value: Val*) extends AnyVal with Val

  case class Num(value: Double) extends AnyVal with Val

  case object False extends Val {
    def value = false
  }

  case object True extends Val {
    def value = true
  }

  case object Null extends Val {
    def value = null
  }

  implicit def numToDouble(n: Num): java.lang.Double = {
    n.value
  }

  implicit def valToGeneric[T](v: Val): T = {
    v.value.asInstanceOf[T]
  }
}

