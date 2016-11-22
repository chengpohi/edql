package com.github.chengpohi.helper

import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse
import org.elasticsearch.action.delete.DeleteResponse
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
  implicit val formats = DefaultFormats

  def buildGetMappingResponse(getMappingsResponse: GetMappingsResponse): String = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    getMappingsResponse.getMappings.asScala.filter(!_.value.isEmpty).foreach(indexEntry => {
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

  def buildAnalyzeResponse(analyzeResponse: AnalyzeResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    analyzeResponse.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.string()
  }

  def buildGetResponse(getResponse: GetResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    getResponse.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.string()
  }

  def buildClusterSettingsResponse(response: ClusterUpdateSettingsResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject("persistent")
    response.getPersistentSettings.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()

    builder.startObject("transient")
    response.getTransientSettings.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.string()
  }

  def buildGetSettingsResponse(response: GetSettingsResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    response.getIndexToSettings.asScala.filter(!_.value.getAsMap.isEmpty).foreach(cursor => {
      builder.startObject(cursor.key)
      builder.startObject("settings")
      cursor.value.toXContent(builder, ToXContent.EMPTY_PARAMS)
      builder.endObject()
      builder.endObject()
    })
    builder.endObject()
    builder.string()
  }


  def buildXContent(toXContent: ToXContent): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    toXContent.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.string()
  }

  def buildStreamMapTupels(tuples: Stream[Map[String, Object]]): String = {
    write(tuples)
  }

  def buildStream(s: Stream[String]): String = {
    write(s)
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
    write(("isFound", deleteResponse.status()))
  }

  def buildSearchResponse(searchResponse: SearchResponse): String = {
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    builder.startObject()
    searchResponse.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.endObject()
    builder.string()
  }

  def buildIsCreated(isCreated: Boolean): String = write(("isCreated", isCreated))

  def buildIdResponse(id: String): String = write(("id", id))
}
