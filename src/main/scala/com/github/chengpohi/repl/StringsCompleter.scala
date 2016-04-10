package com.github.chengpohi.repl

import java.util

import jline.console.completer.Completer

import scala.collection.JavaConversions._
import scala.collection.immutable.Iterable
import scala.math.min

/**
  * elasticshell
  * Created by chengpohi on 3/25/16.
  */
class StringsCompleter(completions: Set[String], words: Set[String]) extends Completer {

  def indentFilter(c: String, buffer: String): Boolean = {
    c.split("\\s+").map(s => s.head).mkString("") == buffer
  }

  def editDist[A](a: Iterable[A], b: Iterable[A]) =
    ((0 to b.size).toList /: a) ((prev, x) =>
      (prev zip prev.tail zip b).scanLeft(prev.head + 1) {
        case (h, ((d, v), y)) => min(min(h + 1, v + 1), d + (if (x == y) 0 else 1))
      }) last

  override def complete(buffer: String, cursor: Int, candidates: util.List[CharSequence]): Int = {
    if (buffer == null) candidates.addAll(completions)
    completions.contains(buffer) match {
      case true =>
        val strings: List[String] =
          completions.filter(_ != buffer).map(i => (i, editDist(i, buffer))).toList.sortBy(i => i._2).map(i => i._1)
        candidates.addAll(strings.take(3))
      case false =>
        val filters: Set[String] = completions.filter(c => c.startsWith(buffer))
        val indentFilters: Set[String] = completions.filter(c => indentFilter(c, buffer))
        val wordCompleter = termCompeleter(buffer)
        val strings: Set[String] = indentFilters ++ filters ++ wordCompleter
        candidates.addAll(strings)
    }

    candidates.isEmpty match {
      case true => -1
      case false => 0
    }
  }

  def termCompeleter(buffer: String): Set[String] = {
    val terms: Array[String] = buffer.split("\\s+")
    val lastWord = terms.last
    lastWord.length match {
      case 0 => Set()
      case _ =>
        words.filter(c => c.startsWith(lastWord)).map(s => (terms.dropRight(1).mkString(" ") + " " + s).trim)
    }
  }
}