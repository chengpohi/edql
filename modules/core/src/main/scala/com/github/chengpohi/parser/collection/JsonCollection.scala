package com.github.chengpohi.parser.collection

import scala.reflect.runtime.universe._

/**
  * eql
  * Created by chengpohi on 2/17/16.
  */
object JsonCollection {

  sealed trait Val extends Any {
    def value: Any

    def apply(i: Int): Val = this.asInstanceOf[Arr].value(i)

    def apply(s: java.lang.String): Val =
      this.asInstanceOf[Obj].value.find(_._1 == s).get._2

    def toJson: String

    def get(path: String): Option[Val]
    def \\(path: String): Option[Val] = get(path)
  }

  case class Str(value: java.lang.String) extends AnyVal with Val {
    override def toJson: String = "\"" + value + "\""

    override def get(path: String): Option[Val] = None
  }

  case class Obj(value: (java.lang.String, Val)*) extends AnyVal with Val {
    override def toJson: String =
      "{" + value
        .map {
          case (n, v) => "\"" + n + "\":" + v.toJson
        }
        .mkString(",") + "}"

    override def get(path: String): Option[Val] =
      value.find(p => p._1 == path).map(_._2)
  }

  case class Arr(value: Val*) extends AnyVal with Val {
    override def toJson: String =
      "[" + value.map(i => i.toJson).mkString(",") + "]"

    override def get(path: String): Option[Val] = None
  }

  case class Tuple(value: Val*) extends AnyVal with Val {
    override def toJson: String =
      "(" + value.map(i => i.toJson).mkString(",") + ")"

    override def get(path: String): Option[Val] = None
  }

  case class Num(value: Double) extends AnyVal with Val {
    override def toJson: String = value.toString

    override def get(path: String): Option[Val] = None
  }

  case object False extends Val {
    def value: Boolean = false

    override def toJson: String = value.toString

    override def get(path: String): Option[Val] = None
  }

  case object True extends Val {
    def value: Boolean = true

    override def toJson: String = value.toString

    override def get(path: String): Option[Val] = None
  }

  case object Null extends Val {
    def value: Option[Nothing] = None

    override def toJson: String = value.toString

    override def get(path: String): Option[Val] = None
  }

  implicit class JsonConverter(value: Val) {
    private[JsonCollection] def extract(tag: Type): Any = {
      if (tag <:< typeOf[Map[_, _]]) {
        val subType2: Type = tag.typeArgs(1)
        value
          .asInstanceOf[Obj]
          .value
          .toList
          .map(i => (i._1, i._2.extract(subType2)))
          .toMap
      } else if (tag <:< typeOf[List[(_, _)]]) {
        val subType: Type = tag.typeArgs.head
        value
          .asInstanceOf[Obj]
          .value
          .toList
          .map(i => (i._1, i._2.extract(subType.typeArgs(1))))
      } else if (tag <:< typeOf[List[_]]) {
        val subType: Type = tag.typeArgs.head
        value
          .asInstanceOf[Arr]
          .value
          .toList
          .map(i => {
            i.extract(subType)
          })
      } else if (tag <:< typeOf[(_, _)]) {
        val subType = tag.typeArgs
        val (tp1, tp2) = (subType.head, subType(1))
        val vals = value.asInstanceOf[Arr].value.toList
        (vals.head.extract(tp1), vals(1).extract(tp2))
      } else if (tag =:= typeOf[Int]) {
        value.asInstanceOf[Num].value
      } else {
        value.value
      }
    }

    def extract[T](implicit tag: TypeTag[T]): T =
      extract(tag.tpe).asInstanceOf[T]
  }

}
