package com.github.chengpohi.annotation

import scala.annotation.StaticAnnotation
import scala.annotation.meta.field

@field
final class Analyzer(name: String = "standard") extends StaticAnnotation
