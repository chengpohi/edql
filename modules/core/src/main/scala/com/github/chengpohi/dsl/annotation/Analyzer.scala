package com.github.chengpohi.dsl.annotation

import scala.annotation.StaticAnnotation
import scala.annotation.meta.field

@field
final class Analyzer(name: String = "standard") extends StaticAnnotation

@field
final class CopyTo(name: String) extends StaticAnnotation

@field
final class Index(name: String) extends StaticAnnotation

@field
final class Alias(name: String) extends StaticAnnotation
