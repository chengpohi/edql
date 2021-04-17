package com.github.chengpohi.dsl.annotation

import scala.annotation.StaticAnnotation
import scala.annotation.meta.field

@field
final class Analyzer extends StaticAnnotation

@field
final class CopyTo extends StaticAnnotation

@field
final class Index extends StaticAnnotation

@field
final class Alias extends StaticAnnotation
