package com.github.chengpohi.dsl.serializer

import org.json4s.jackson.Serialization.write

trait ResponseSerializer extends JSONOps {

  trait JSONSerializer[A] {
    def json(a: A): String
  }

  object JSONSerializer {
    implicit object StringResponseJSONSerializer
      extends JSONSerializer[String] {
      override def json(response: String): String = response
    }

    implicit object StreamMapSearchHitResponseJSONSerializer
      extends JSONSerializer[Stream[Map[String, AnyRef]]] {
      override def json(response: Stream[Map[String, AnyRef]]): String = {
        write(response)
      }
    }

    implicit object MapSearchHitResponseJSONSerializer
      extends JSONSerializer[Map[String, AnyRef]] {
      override def json(response: Map[String, AnyRef]): String =
        toJson(response)
    }

  }

  trait SerializerOps[A] {
    val F: JSONSerializer[A]
    val value: A

    def json: String = F.json(value)
  }

  implicit def toSerializerOps[A: JSONSerializer](a: A)(
    implicit F0: JSONSerializer[A]): SerializerOps[A] = new SerializerOps[A] {
    override val F = F0
    override val value = a
  }
}
