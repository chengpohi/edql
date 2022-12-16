package com.github.chengpohi.parser

import com.github.chengpohi.edql.parser.EDQLParserDefinition
import com.github.chengpohi.edql.parser.psi.{EDQLActionExpr, EDQLConnectionExpr, EDQLExpr, EDQLFile, EDQLImportop}
import com.github.chengpohi.parser.collection.JsonCollection
import org.intellij.grammar.LightPsi

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.{Success, Try}


class EQLParser extends EQLInstructionParser {
  def generateInstructions(script: String): Try[Seq[Instruction2]] = {
    val file = LightPsi.parseLight(script, new EDQLParserDefinition()).asInstanceOf[EDQLFile]
    val headExprs = file.getEDQLHeadExprs;
    val exprs = file.getEDQLExprs;
    val instructions = headExprs.asScala.flatMap(i => {
      i match {
        case i: EDQLImportop =>
          Seq(ImportInstruction(i.getText))
        case i: EDQLConnectionExpr =>
          val ss = mutable.Seq[Instruction2]()
          val host = i.getHostExpr
          if (host != null) {
            ss :+ EndpointBindInstruction(host.getHostPath.getText)
          }
          val timeout = i.getTimeoutExpr
          if (timeout != null) {
            ss :+ TimeoutInstruction(timeout.getNumber.getText.toInt)
          }

          val auth = i.getAuthExpr
          val basicToken = auth.getBasicToken
          if (basicToken != null) {
            Some(basicToken.getIdentifier0.getValue).getOrElse(basicToken.getDoubleQuotedString.getText)
          }
          ss
        case _ => Seq()
      }
    })

    exprs.asScala.map(i => {
      case i: EDQLActionExpr => {
        val methodType = i.getMethodType.getText

        methodType match {
          case "POST" =>
            i.getObjList
            i.getArrList
            i.getArrcompList
            PostActionInstruction(i.getIdentifier0.getValue + i.getQuery.getText, )
        }
        i.getQuery.getText
      }
    })

    Success(instructions.toSeq)
  }

  def parseJson(source: String): Try[JsonCollection.Val] = {
    null
  }

}
