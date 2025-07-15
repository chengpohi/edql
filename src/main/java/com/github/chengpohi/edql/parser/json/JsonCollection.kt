package com.github.chengpohi.edql.parser.json

import com.fasterxml.jackson.core.io.JsonStringEncoder
import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal

object JsonCollection {
    sealed interface Val {
        val value: Any?

        fun apply(i: Int): Val = (this as Arr).value[i]

        fun apply(s: String): Val =
            (this as Obj).value.find { it.first.value == s }?.second
                ?: throw NoSuchElementException("Key not found: $s")

        fun toJson(): String

        fun vars(): List<Var> = emptyList()

        fun funs(): List<Fun> = emptyList()

        fun get(path: String): Val?

        fun copy(): Val
    }

    data class Str(override val value: String) : Val, Arith {
        override fun toJson(): String {
            return "\"${String(JsonStringEncoder.getInstance().quoteAsString(value))}\""
        }

        fun raw(): String = value

        override fun get(path: String): Val? = null

        override fun plus(i: Arith): Arith = when (i) {
            is Num -> Str(value + i.value.toString())
            is Str -> Str(value + i.value)
            else -> throw RuntimeException("not support + type: $i")
        }

        override fun minus(i: Arith): Arith = throw RuntimeException("not support - type: $i")

        override fun multiply(i: Arith): Arith = throw RuntimeException("not support * type: $i")

        override fun div(i: Arith): Arith = throw RuntimeException("not support / type: $i")

        override fun copy(): Val = this
    }

    abstract class Dynamic : Val {
        abstract fun clean()

        var realValue: Val? = null
    }

    data class Var(override val value: String) : Dynamic() {
        override fun toJson(): String = realValue?.toJson() ?: ""

        override fun get(path: String): Val? = null

        override fun vars(): List<Var> = listOf(this)

        override fun copy(): Val {
            val va = Var(value)
            va.realValue = realValue?.copy()
            return va.realValue ?: this
        }

        override fun clean() {
            realValue = null
        }
    }

    data class Fun(override val value: Pair<String, List<Val>>) : Dynamic() {

        override fun toJson(): String = realValue?.toJson() ?: ""

        override fun get(path: String): Val? = null

        override fun funs(): List<Fun> = listOf(this)

        override fun copy(): Val {
            val f = Fun(value.first to value.second.map { it.copy() })
            return realValue?.copy() ?: this
        }

        override fun clean() {
            value.second.filterIsInstance<Dynamic>().forEach { it.clean() }
            realValue = null
        }
    }

    data class ArithTree(
        var a: Val,
        var op: String?,
        var b: Val?,
        var order: Int? = null
    ) : Dynamic() {
        override val value: Triple<Val, String?, Val?> = Triple(a, op, b)

        override fun toJson(): String = realValue?.toJson() ?: a.toJson()

        override fun get(path: String): Val? = null

        override fun copy(): Val {
            val tree = ArithTree(a.copy(), op, b?.copy(), null)
            return realValue?.copy() as? Arith ?: tree
        }

        override fun clean() {
            when (a) {
                is Dynamic -> (a as Dynamic).clean()
                else -> {}
            }
            a.vars().forEach { it.clean() }
            b?.let { o ->
                if (o is Dynamic) {
                    o.clean()
                }
                o.vars().forEach { it.clean() }
            }
            realValue = null
        }
    }

    data class Obj(override val value: List<Pair<Val, Val>>) : Val, Arith {
        constructor(vararg pairs: Pair<Val, Val>) : this(pairs.toList())

        override fun toJson(): String {
            val valueJson = value.map { (n, v) ->
                val j = v.toJson()
                "${n.toJson()}:$j"
            }.filter { StringUtils.isNotBlank(it) }.joinToString(",")
            return "{$valueJson}"
        }

        override fun get(path: String): Val? =
            value.find { it.first.value == path }?.second

        override fun vars(): List<Var> = value.flatMap { (k, v) -> k.vars() + v.vars() }

        override fun funs(): List<Fun> = value.flatMap { (k, v) -> k.funs() + v.funs() }

        fun remove(v: String): Obj {
            val nvs = value.filter { it.first !is Str || (it.first as Str).value != v }
            return Obj(nvs)
        }

        fun add(k: String, v: Val): Obj {
            val nvs = value + (Str(k) to v)
            return Obj(nvs)
        }

        override fun copy(): Val = Obj(value.map { (k, v) -> k.copy() to v.copy() })

        override fun plus(i: Arith): Arith = when (i) {
            is Obj -> Obj(value + i.value)
            else -> throw RuntimeException("not support + type: $i")
        }

        override fun minus(i: Arith): Arith = throw RuntimeException("not support - type: $i")

        override fun multiply(i: Arith): Arith = throw RuntimeException("not support * type: $i")

        override fun div(i: Arith): Arith = throw RuntimeException("not support / type: $i")
    }

    data class Arr(override val value: List<Val>) : Val {
        constructor(vararg values: Val) : this(values.toList())
        override fun toJson(): String =
            "[${value.joinToString(",") { it.toJson() }}]"

        override fun get(path: String): Val? = null

        override fun vars(): List<Var> = value.flatMap { it.vars() }

        override fun funs(): List<Fun> = value.flatMap { it.funs() }

        override fun copy(): Val = Arr(value.map { it.copy() })
    }

    data class Tuple(override val value: List<Val>) : Val {
        constructor(vararg values: Val) : this(values.toList())

        override fun toJson(): String =
            "(${value.joinToString(",") { it.toJson() }})"

        override fun get(path: String): Val? = null

        override fun vars(): List<Var> = value.flatMap { it.vars() }

        override fun funs(): List<Fun> = value.flatMap { it.funs() }

        override fun copy(): Val = Tuple(value.map { it.copy() })
    }

    interface Arith : Val {
        fun plus(i: Arith): Arith
        fun minus(i: Arith): Arith
        fun multiply(i: Arith): Arith
        fun div(i: Arith): Arith
    }

    data class Num(override val value: Number) : Val, Arith {
        override fun toJson(): String {
            return when (value) {
                is BigDecimal -> value.toString()
                else -> BigDecimal.valueOf(value.toDouble()).stripTrailingZeros().toPlainString()
            }
        }

        override fun get(path: String): Val? = null

        override fun plus(i: Arith): Arith = when (i) {
            is Num -> Num(addNumbers(value, i.value))
            is Str -> Str(value.toString() + i.value)
            else -> throw RuntimeException("not support + type: $i")
        }

        override fun minus(i: Arith): Arith = when (i) {
            is Num -> Num(minusNumbers(value, i.value))
            else -> throw RuntimeException("not support - type: $i")
        }

        override fun multiply(i: Arith): Arith = when (i) {
            is Num -> Num(multiplyNumbers(value, i.value))
            else -> throw RuntimeException("not support * type: $i")
        }

        override fun div(i: Arith): Arith = when (i) {
            is Num -> Num(divNumbers(value, i.value))
            else -> throw RuntimeException("not support / type: $i")
        }

        override fun copy(): Val = this
    }

    fun addNumbers(a: Number, b: Number): Number {
        return when {
            a is Double || b is Double -> a.toDouble() + b.toDouble()
            a is Float || b is Float -> a.toFloat() + b.toFloat()
            a is Long || b is Long -> a.toLong() + b.toLong()
            else -> a.toInt() + b.toInt()
        }
    }

    fun minusNumbers(a: Number, b: Number): Number {
        return when {
            a is Double || b is Double -> a.toDouble() - b.toDouble()
            a is Float || b is Float -> a.toFloat() - b.toFloat()
            a is Long || b is Long -> a.toLong() - b.toLong()
            else -> a.toInt() - b.toInt()
        }
    }

    object False : Val {
        override val value: Boolean = false

        override fun toJson(): String = value.toString()

        override fun get(path: String): Val? = null

        override fun copy(): Val = this
    }

    object True : Val {
        override val value: Boolean = true

        override fun toJson(): String = value.toString()

        override fun get(path: String): Val? = null

        override fun copy(): Val = this
    }

    object Null : Val {
        override val value: Nothing? = null

        override fun toJson(): String = value?.toString() ?: "null"

        override fun get(path: String): Val? = null

        override fun copy(): Val = this
    }

    fun multiplyNumbers(a: Number, b: Number): Number {
        return when {
            a is Double || b is Double -> a.toDouble() * b.toDouble()
            a is Float || b is Float -> a.toFloat() * b.toFloat()
            a is Long || b is Long -> a.toLong() * b.toLong()
            else -> a.toInt() * b.toInt()
        }
    }

    fun divNumbers(a: Number, b: Number): Number {
        return when {
            a is Double || b is Double -> a.toDouble() / b.toDouble()
            a is Float || b is Float -> a.toFloat() / b.toFloat()
            a is Long || b is Long -> a.toLong() / b.toLong()
            else -> a.toInt() / b.toInt()
        }
    }

    object Comment : Val {
        override val value: Nothing? = null

        override fun toJson(): String = value.toString()

        override fun get(path: String): Val? = null

        override fun copy(): Val = this
    }
}