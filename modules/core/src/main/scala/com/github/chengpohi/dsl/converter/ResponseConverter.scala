package com.github.chengpohi.dsl.converter

import com.github.chengpohi.dsl.serializer.ResponseSerializer
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.SearchHit
import org.json4s._
import org.json4s.jackson.JsonMethods._

trait ResponseConverter extends ResponseSerializer {

  trait Converter[A] {
    def as[T](a: A)(implicit mf: Manifest[T]): Stream[T]
  }

  object Converter {

    implicit object IndexResponseConverter extends Converter[IndexResponse] {
      def as[T](a: IndexResponse)(implicit mf: Manifest[T]): Stream[T] =
        Stream.empty[T]
    }

    implicit object SearchResponseConverter extends Converter[SearchResponse] {
      def as[T](a: SearchResponse)(implicit mf: Manifest[T]): Stream[T] = {
        a.getHits.getHits.toStream.map(t => {
          mapSearchHit(t)(mf)
        })
      }
    }

    implicit object GetResponseConverter extends Converter[GetResponse] {
      def as[T](a: GetResponse)(implicit mf: Manifest[T]): Stream[T] =
        Stream(mapGetResponse(a))
    }

    implicit object SearchHitConverter extends Converter[SearchHit] {
      def as[T](a: SearchHit)(implicit mf: Manifest[T]): Stream[T] = {
        Stream.apply(mapSearchHit(a)(mf))
      }
    }

    implicit object StreamSearchResponseConverter
        extends Converter[Stream[SearchHit]] {
      def as[T](a: Stream[SearchHit])(implicit mf: Manifest[T]): Stream[T] = {
        a.map(t => {
          mapSearchHit(t)(mf)
        })
      }
    }

    def mapSearchHit[T](searchHit: SearchHit)(implicit mf: Manifest[T]): T = {
      val j = parse(searchHit.getSourceAsString())
      val r = j merge JObject("id" -> JString(searchHit.getId))
      r.extract(formats, mf)
    }

    def mapGetResponse[T](getResponse: GetResponse)(
        implicit mf: Manifest[T]): T = {
      val j = parse(getResponse.getSourceAsString)
      val r = j merge JObject("id" -> JString(getResponse.getId))
      r.extract(formats, mf)
    }
  }

}
