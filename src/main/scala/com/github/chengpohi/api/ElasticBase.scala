package com.github.chengpohi.api

import org.elasticsearch.client.{Client, ClusterAdminClient, IndicesAdminClient}

/**
  * ElasticBase function
  * Created by chengpohi on 6/28/15.
  */
trait ElasticBase {
  val client: Client
  val clusterClient: ClusterAdminClient
  val indicesClient: IndicesAdminClient

  def toJavaMap[A](m: Map[A, _]): java.util.Map[A, _] = {
    import scala.collection.JavaConverters._
    val res = m.map(b => {
      val r = b._2 match {
        case a: Iterable[_] => a.asJava
        case a: Int         => Integer.parseInt(a.toString)
        case a: Double      => java.lang.Double.parseDouble(a.toString)
        case a: Long        => java.lang.Long.parseLong(a.toString)
        case a: Float       => java.lang.Float.parseFloat(a.toString)
        case a: BigInt      => Integer.parseInt(a.toString())
        case a: BigDecimal  => a.bigDecimal
        case a              => a
      }
      (b._1, r)
    })
    res.asJava
  }
}
