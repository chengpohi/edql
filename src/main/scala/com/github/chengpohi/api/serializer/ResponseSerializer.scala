package com.github.chengpohi.api.serializer

import com.github.chengpohi.helper.NumberSerializer
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.support.master.AcknowledgedResponse
import org.elasticsearch.common.xcontent.{
  ToXContent,
  XContentFactory,
  XContentType
}
import org.json4s._
import org.json4s.native.Serialization.write

import scala.collection.JavaConverters._

trait ResponseSerializer {

  implicit val formats = DefaultFormats + new NumberSerializer

  trait JSONSerializer[A] {
    def json(a: A): String
  }

  object JSONSerializer {

    implicit object XContentResponseJSONSerializer
        extends JSONSerializer[ToXContent] {
      override def json(a: ToXContent): String = {
        val builder = XContentFactory.contentBuilder(XContentType.JSON)
        builder.prettyPrint()
        builder.startObject()
        a.toXContent(builder, ToXContent.EMPTY_PARAMS)
        builder.endObject()
        builder.string()
      }
    }

    implicit object BulkResponseJSONSerializer
        extends JSONSerializer[BulkResponse] {
      override def json(response: BulkResponse): String = {
        val builder = XContentFactory.contentBuilder(XContentType.JSON)
        builder.startObject()
        builder.field("took", response.getTookInMillis)
        if (response.getIngestTookInMillis != BulkResponse.NO_INGEST_TOOK) {
          builder.field("intest_took", response.getIngestTookInMillis)
        }
        builder.field("errors", response.hasFailures)
        builder.startArray("items")
        response.forEach(i => {
          i.toXContent(builder, ToXContent.EMPTY_PARAMS)
        })
        builder.endArray()
        builder.endObject()
        builder.toString
      }
    }

    implicit object AcknowledgedResponseJSONSerializer
        extends JSONSerializer[AcknowledgedResponse] {
      override def json(response: AcknowledgedResponse): String = {
        write(("acknowledged", response.isAcknowledged))
      }
    }

    implicit object GetSettingsResponseJSONSerializer
        extends JSONSerializer[GetSettingsResponse] {
      override def json(response: GetSettingsResponse): String = {
        val builder = XContentFactory.contentBuilder(XContentType.JSON)
        builder.startObject()
        response.getIndexToSettings.asScala
          .filter(!_.value.getAsMap.isEmpty)
          .foreach(cursor => {
            builder.startObject(cursor.key)
            builder.startObject("settings")
            cursor.value.toXContent(builder, ToXContent.EMPTY_PARAMS)
            builder.endObject()
            builder.endObject()
          })
        builder.endObject()
        builder.string()
      }
    }

    implicit object GetMappingResponseJSONSerializer
        extends JSONSerializer[GetMappingsResponse] {
      override def json(response: GetMappingsResponse): String = {
        val builder = XContentFactory.jsonBuilder()
        builder.startObject()
        response.getMappings.asScala
          .filter(!_.value.isEmpty)
          .foreach(indexEntry => {
            builder.startObject(indexEntry.key)
            builder.startObject("mappings")
            indexEntry.value.asScala.foreach(typeEntry => {
              builder.field(typeEntry.key)
              builder.map(typeEntry.value.sourceAsMap())
            })
            builder.endObject()
            builder.endObject()
          })
        builder.endObject()
        builder.string()
      }
    }

  }

  trait SerializerOps[A] {
    val F: JSONSerializer[A]
    val value: A
    def json: String = F.json(value)
  }

  implicit def toSerializer[A: JSONSerializer](a: A)(
      implicit F0: JSONSerializer[A]): SerializerOps[A] = new SerializerOps[A] {
    override val F = F0
    override val value = a
  }

  implicit def toXContentSerializer[A <: ToXContent](a: A)(
      implicit F0: JSONSerializer[ToXContent]): SerializerOps[ToXContent] =
    new SerializerOps[ToXContent] {
      override val F = F0
      override val value = a
    }

  implicit def toAcknowledgedSerializer(a: AcknowledgedResponse)(
      implicit F0: JSONSerializer[AcknowledgedResponse])
    : SerializerOps[AcknowledgedResponse] =
    new SerializerOps[AcknowledgedResponse] {
      override val F = F0
      override val value = a
    }
}
