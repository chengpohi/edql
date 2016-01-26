package com.github.chengpohi

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.common.xcontent._
import scala.collection.JavaConverters._

/**
 * elasticshell
 * Created by chengpohi on 1/26/16.
 */
object ResponseGenerator {
  val MAPPINGS = new XContentBuilderString("mappings")

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

}
