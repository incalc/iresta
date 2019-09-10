package io.github.incalc.iresta.data.parser

import io.github.incalc.iresta.data.Meal
import org.jsoup.Jsoup
import java.util.*


class FacultyRestaurantParser : RestaurantParser {
    override fun run(): List<Meal> {
        val document = Jsoup.connect("http://www.inha.ac.kr/diet/kr/1/view.do").post()
        val days = document.select(".objHeading_h2")
        val types = document.select(".hSp15.objHeading_h3")
        val tables = document.select("#viewForm .foodTable table")
        val meals = mutableListOf<Meal>()
        for ((i, table) in tables.withIndex()) {
            val date = days[i].text()
            val rows = table.select("tbody tr")
            for (row in rows) {
                val category = row.select("td.bold").text()
                if (category.isNotEmpty()) {
                    val menus = row.select("td.left")
                        .text()
                        .split(" ")
                        .toMutableList()
                    val price = row.select("td:not(.bold, .left)")
                        .text()
                        .replace(Regex("""[^\d/]"""), "")
                        .toInt()
                    val meal = Meal(Date(), category, menus, price)
                    meal.show()
                    meals += meal
                }
            }
        }
        return meals
    }
}
