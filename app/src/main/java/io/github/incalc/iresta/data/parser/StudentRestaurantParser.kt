package io.github.incalc.iresta.data.parser

import io.github.incalc.iresta.data.Meal
import org.jsoup.Jsoup
import java.util.*


class StudentRestaurantParser : RestaurantParser {
    override fun run(): List<Meal> {
        val document = Jsoup.connect("http://www.inha.ac.kr/diet/kr/2/view.do").post()
        val days = document.select(".objHeading_h2")
        val types = document.select(".hSp15.objHeading_h3")
        val tables = document.select("#viewForm .foodTable table")
        val meals = mutableListOf<Meal>()
        for ((i, table) in tables.withIndex()) {
            val day = days[i].text()
            val type = types[i].text().split(" ")[0]
            val rows = table.select("tbody tr")
            for (row in rows) {
                val category = row.select("td.bold").text()
                if (category.isNotEmpty()) {
                    val menus = row.select("td.left")
                        .text()
                        .split(" ")
                        .toMutableList()
                    val prices = row.select("td:not(.bold, .left)")
                        .text()
                        .replace(Regex("""[^\d/]"""), "")
                        .split(Regex("""\s*/\s*"""))
                        .map { it.toInt() }
                        .toList()
                    if (prices.size == 2 && menus.last().contains("★세트메뉴_")) {
                        menus[menus.lastIndex] = "${menus.last()} ￦${(prices[1] - prices[0])}"
                    }
                    val meal = Meal(Date(), "${type}_${category}", menus, prices[0])
                    meal.show()
                    meals += meal
                }
            }
        }
        return meals
    }
}
