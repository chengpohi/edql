package com.github.chengpohi.converter

import com.github.chengpohi.serializer.ResponseSerializer

trait ResponseConverter extends ResponseSerializer {

  trait Converter[A] {
    def as[T](a: A)(implicit mf: Manifest[T]): Stream[T]
  }

  object Converter {
  }
}
