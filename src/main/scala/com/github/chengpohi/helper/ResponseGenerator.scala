package com.github.chengpohi.helper

import com.sksamuel.elastic4s.mappings.GetMappingsResult
import com.sksamuel.elastic4s.{BulkResult, RichGetResponse}
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse
import org.elasticsearch.action.delete.DeleteResponse
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

  def buildGetMappingResponse(getMappingsResponse: GetMappingsResult): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    getMappingsResponse.original.getMappings.asScala.filter(!_.value.isEmpty).foreach(indexEntry => {
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

  def buildBulkResponse(bulkResponse: BulkResult): String = {
    write(("hasFailures", bulkResponse.hasFailures))
  }

  def buildGetResponse(getResponse: RichGetResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    getResponse.original.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.bytes().toUtf8
  }

  def buildClusterSettingsResponse(response: ClusterUpdateSettingsResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject("persistent")
    response.getPersistentSettings.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()

    builder.startObject("transient")
    response.getTransientSettings.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.bytes().toUtf8
  }

  def buildGetSettingsResponse(response: GetSettingsResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    response.getIndexToSettings.asScala.filter(!_.value.getAsMap.isEmpty).foreach(cursor => {
      builder.startObject(cursor.key, XContentBuilder.FieldCaseConversion.NONE)
      builder.startObject("settings")
      cursor.value.toXContent(builder, ToXContent.EMPTY_PARAMS)
      builder.endObject()
      builder.endObject()
    })
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

  def buildStreamMapTupels(tuples: Stream[Map[String, AnyRef]]): String = {
    write(tuples)
  }


  def extractJSON(json: String, filterName: String): String = {
    val jObj = parse(json)
    val result = filterName.split("\\.").foldLeft(jObj) { (o, i) => o \ i }
    write(result)
  }

  def beautyJSON(json: String): String = {
    pretty(render(parse(json)))
  }

  def buildAcknowledgedResponse(acknowledgedResponse: AcknowledgedResponse): String = {
    write(("acknowledged", acknowledgedResponse.isAcknowledged))
  }

  def buildIsFound(deleteResponse: DeleteResponse): String = {
    write(("isFound", deleteResponse.isFound))
  }

  def buildSearchResponse(searchResponse: SearchResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    searchResponse.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.bytes().toUtf8
  }

  def buildIsCreated(isCreated: Boolean): String = write(("isCreated", isCreated))

  def buildIdResponse(id: String): String = write(("id", id))
}
