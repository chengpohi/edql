package com.github.chengpohi.dsl.converter

import com.github.chengpohi.dsl.serializer.ResponseSerializer

trait ResponseConverter extends ResponseSerializer {

  trait Converter[A] {
    def as[T](a: A)(implicit mf: Manifest[T]): Stream[T]
  }

  object Converter {
  }
}
