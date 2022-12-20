package com.github.chengpohi.dsl.serializer

import java.time.format.DateTimeFormatter
import java.time.temporal.{TemporalAccessor, TemporalQuery}
import java.time.{Instant, LocalDateTime, LocalTime, ZonedDateTime}

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

object JavaTimeSerializers {
  val defaults =
    InstantSerializer ::
      LocalTimeSerializer ::
      LocalDateTimeSerializer ::
      ZonedDateTimeSerializer ::
      Nil

  /** The default `InstantSerializer` for ISO-8601 strings. */
  object InstantSerializer extends InstantSerializer(DateTimeFormatter.ISO_INSTANT)

  /** The default `LocalTimeSerializer` for ISO-8601 strings. */
  object LocalTimeSerializer extends LocalTimeSerializer(DateTimeFormatter.ISO_LOCAL_TIME)

  /** The default `LocalDateTimeSerializer` for ISO-8601 strings. */
  object LocalDateTimeSerializer extends LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

  /** The default `ZonedDateTimeSerializer` for ISO-8601 strings. */
  object ZonedDateTimeSerializer extends ZonedDateTimeSerializer(DateTimeFormatter.ISO_ZONED_DATE_TIME)

  /** A `CustomSerializer` for `java.time.Instant`. */
  class InstantSerializer private[JavaTimeSerializers](val format: DateTimeFormatter) extends CustomSerializer[Instant](_ => ( {
    case JString(s) => format.parse(s, asQuery(Instant.from))
  }, {
    case t: Instant => JString(format.format(t))
  }
  ))

  /** A `CustomSerializer` for `java.time.LocalTime`. */
  class LocalTimeSerializer private[JavaTimeSerializers](val format: DateTimeFormatter) extends CustomSerializer[LocalTime](_ => ( {
    case JString(s) => format.parse(s, asQuery(LocalTime.from))
  }, {
    case t: LocalTime => JString(format.format(t))
  }
  ))

  /** A `CustomSerializer` for `java.time.LocalDateTime`. */
  class LocalDateTimeSerializer private[JavaTimeSerializers](val format: DateTimeFormatter) extends CustomSerializer[LocalDateTime](_ => ( {
    case JString(s) => format.parse(s, asQuery(LocalDateTime.from))
  }, {
    case t: LocalDateTime => JString(format.format(t))
  }
  ))

  /** A `CustomSerializer` for `java.time.ZonedDateTime`. */
  class ZonedDateTimeSerializer private[JavaTimeSerializers](val format: DateTimeFormatter) extends CustomSerializer[ZonedDateTime](_ => ( {
    case JString(s) => format.parse(s, asQuery(ZonedDateTime.from))
  }, {
    case t: ZonedDateTime => JString(format.format(t))
  }
  ))

  def asQuery[A](f: TemporalAccessor => A): TemporalQuery[A] =
    new TemporalQuery[A] {
      override def queryFrom(temporal: TemporalAccessor): A = f(temporal)
    }

}
