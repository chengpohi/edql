package com.github.chengpohi.helper

/**
  * Created by chengpohi on 24/02/2017.
  */
trait IndexDocument {
  val filter: ((String, Any)) => Boolean

  def toMap: Map[String, Any] =
    (Map[String, Any]() /: this.getClass.getDeclaredFields) { (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(this))
    }.filter(i => filter(i))
}
