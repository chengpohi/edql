package com.github.chengpohi.dsl

import org.elasticsearch.client.RestClient

import scala.jdk.CollectionConverters._;

/**
 * ElasticBase function
 * Created by chengpohi on 6/28/15.
 */
trait ElasticBase {
  implicit val eqlClient: EDQLClient
  val restClient: RestClient = eqlClient.restClient

  def toJavaMap[A](m: Map[A, _]): java.util.Map[A, _] = {
    val res = m.map((b: (A, _)) => {
      val r = b._2 match {
        case a: Iterable[_] => a.asJava
        case a: Int => Integer.parseInt(a.toString)
        case a: Double => java.lang.Double.parseDouble(a.toString)
        case a: Long => java.lang.Long.parseLong(a.toString)
        case a: Float => java.lang.Float.parseFloat(a.toString)
        case a: BigInt => Integer.parseInt(a.toString())
        case a: BigDecimal => a.bigDecimal
        case a => a
      }
      (b._1, r)
    })
    res.asJava
  }
}
