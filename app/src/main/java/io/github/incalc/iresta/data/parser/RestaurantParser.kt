package io.github.incalc.iresta.data.parser

import io.github.incalc.iresta.data.Meal


interface RestaurantParser {
    fun run(): List<Meal>
}
