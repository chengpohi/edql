package com.github.chengpohi.repl

import java.io.IOException
import java.util

import jline.console.ConsoleReader
import jline.console.completer.{CandidateListCompletionHandler, CompletionHandler}

import scala.collection.JavaConversions._

/**
  * ELKCompletionHandler
  * Created by chengpohi on 3/25/16.
  */
class ELKCompletionHandler extends CompletionHandler {
  @throws[IOException]
  override def complete(reader: ConsoleReader, candidates: util.List[CharSequence], position: Int): Boolean = {
    val buf = reader.getCursorBuffer
    candidates.size() match {
      case 1 =>
        val value = candidates.head
        setBuffer(reader, value, position)
        true
      case i if i > 1 => {
        val value = candidates.head
        setBuffer(reader, value, position)
        CandidateListCompletionHandler.printCandidates(reader, candidates)
        reader.drawLine()
        true
      }
      case _ =>
        true
    }
  }

  @throws[IOException]
  def setBuffer(reader: ConsoleReader, value: CharSequence, offset: Int) {
    while ((reader.getCursorBuffer.cursor > offset) && reader.backspace) {
    }
    reader.putString(value)
    reader.setCursorPosition(offset + value.length)
  }
}
