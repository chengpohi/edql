package com.github.chengpohi.parser

import com.github.chengpohi.base.{ElasticCommand, ElasticBase}
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse
import org.elasticsearch.common.xcontent.{ToXContent, XContentType, XContentFactory}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source

/**
 * elasticservice
 * Created by chengpohi on 1/18/16.
 */
object ELKCommand {

  val TUPLE = """\((.*),(.*)\)""".r

  val h: Seq[String] => String = health
  val c: Seq[String] => String = count
  val d: Seq[String] => String = delete
  val q: Seq[String] => String = query
  val r: Seq[String] => String = reindex
  val i: Seq[String] => String = index
  val u: Seq[String] => String = update
  val ci: Seq[String] => String = createIndex
  val a: Seq[String] => String = analysis

  def createIndex(parameters: Seq[String]): String = {
    val createResponse = ElasticCommand.createIndex(parameters.head)
    Await.result(createResponse, Duration.Inf).isAcknowledged.toString
  }

  def health(parameters: Seq[String]): String = {
    ElasticCommand.clusterHealth()
  }

  def count(parameters: Seq[String]): String = {
    ElasticCommand.countCommand(parameters.head)
  }

  def delete(parameters: Seq[String]): String = {
    ElasticCommand.deleteIndex(parameters.head)
    s"delete ${parameters.head} success"
  }

  def query(parameters: Seq[String]): String = {
    ElasticCommand.getAllDataByIndexName(parameters.head).toString
  }

  def update(parameters: Seq[String]): String = {
    val (indexName, indexType, updateFields) = (parameters.head, parameters(1), parameters(2))
    val uf: (String, String) = updateFields match {
      case TUPLE(field, value) => (field.trim, value.trim)
      case _ => null
    }
    ElasticCommand.update(indexName, indexType, uf)
  }

  def reindex(parameters: Seq[String]): String = {
    val (sourceIndex, targetIndex, sourceIndexType, fields) = (parameters.head, parameters(1), parameters(2), parameters(3))
    ElasticCommand.reindex(sourceIndex, targetIndex, sourceIndexType, fields.split(",").map(_.trim))
  }

  def index(parameters: Seq[String]): String = {
    val (indexName, indexType, fields) = (parameters.head, parameters(1), parameters(2))
    val uf: (String, String) = fields match {
      case TUPLE(field, value) => (field.trim, value.trim)
      case _ => null
    }
    ElasticCommand.index(indexName, indexType, uf)
  }
  def analysis(parameters: Seq[String]): String = {
    val (analyzer, doc) = (parameters.head, parameters(1))
    val builder = XContentFactory.contentBuilder(XContentType.JSON)
    val analyzeResponse: AnalyzeResponse = Await.result(ElasticCommand.analysis(analyzer, doc), Duration.Inf)
    analyzeResponse.toXContent(builder, ToXContent.EMPTY_PARAMS)
    builder.bytes().toUtf8
  }
}
