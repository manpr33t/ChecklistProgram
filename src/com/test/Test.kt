package com.test

// Messing around with kotlin

fun odd(x: Int) = x % 2 == 1
fun even(x: Int) = x % 2 == 0

fun size(s: String) = s.length

fun doThis(f: (String) -> Int): (String) -> Int {
    return {s -> f.invoke(s)}
}

fun not(f: (Int) -> Boolean): (Int) ->Boolean {
    return {n -> !f.invoke(n)}
}

val notOdd = not(::odd)
val notEven = not(::even)
val stringSize = doThis(::size)

fun main(args: Array<String>) {
    println(notOdd(5))
    println(stringSize("Hello"))
}
