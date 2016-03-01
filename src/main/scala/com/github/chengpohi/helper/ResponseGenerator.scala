package com.github.chengpohi.helper

import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.support.master.AcknowledgedResponse
import org.elasticsearch.common.xcontent._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write

import scala.collection.JavaConverters._

/**
 * elasticshell
 * Created by chengpohi on 1/26/16.
 */
class ResponseGenerator {
  val MAPPINGS = new XContentBuilderString("mappings")
  val TOOK = new XContentBuilderString("took")
  val ERRORS = new XContentBuilderString("errors")
  val ITEMS = new XContentBuilderString("items")

  implicit val formats = DefaultFormats

  def buildGetMappingResponse(getMappingsResponse: GetMappingsResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    getMappingsResponse.getMappings.asScala.filter(!_.value.isEmpty).foreach(indexEntry => {
      builder.startObject(indexEntry.key, XContentBuilder.FieldCaseConversion.NONE)
      builder.startObject(MAPPINGS)
      indexEntry.value.asScala.foreach(typeEntry => {
        builder.field(typeEntry.key)
        builder.map(typeEntry.value.sourceAsMap())
      })
      builder.endObject()
      builder.endObject()
    })
    builder.endObject()
    builder.bytes().toUtf8
  }

  def buildAnalyzeResponse(analyzeResponse: AnalyzeResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    analyzeResponse.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.bytes().toUtf8
  }

  def buildBulkResponse(bulkResponse: BulkResponse): String = {
    write(("hasFailures", bulkResponse.hasFailures))
  }

  def buildGetResponse(getResponse: GetResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    getResponse.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.bytes().toUtf8
  }

  def buildXContent(toXContent: ToXContent): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    toXContent.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.bytes().toUtf8
  }


  def extractJSON(json: String, filterName: String): String = {
    val jObj = parse(json)
    val filtered = jObj filterField {
      case JField(`filterName`, _) => true
      case _ => false
    }
    write(filtered)
  }

  def beautyJSON(json: String): String = {
    pretty(render(parse(json)))
  }

  def buildAcknowledgedResponse(acknowledgedResponse: AcknowledgedResponse): String = {
    write(("acknowledged", acknowledgedResponse.isAcknowledged))
  }

  def buildSearchResponse(searchResponse: SearchResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    searchResponse.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.bytes().toUtf8
  }
}
