package io.github.incalc.iresta.data.parser

import io.github.incalc.iresta.data.Meal
import io.github.incalc.iresta.utils.DateUtils
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jsoup.Jsoup
import java.net.URL


val titleRegex = Regex("""\d+\.\d+\.\d+""")
val numberRegex = Regex("""\d+(\.\d+)?""")
val dateRegex = Regex("""(\d+)월\s*(\d+)일""")

class DormitoryRestaurantParser : RestaurantParser {
    override fun run(): List<Meal> {
        val meals = mutableListOf<Meal>()

        val listDocument =
            Jsoup.connect("http://dept.inha.ac.kr/user/indexSub.do?codyMenuSeq=8420&siteId=dorm")
                .get()
        val postAnchor =
            listDocument.select(".title a").find { titleRegex.containsMatchIn(it.text()) }
        val postDocument = Jsoup.connect("http://dept.inha.ac.kr${postAnchor?.attr("href")}").get()
        val fileAnchor = postDocument.select(".file a").find { it.text().contains(".xlsx") }
        val fileUrl = URL("http://dept.inha.ac.kr${fileAnchor?.attr("href")}")
        val connection = fileUrl.openConnection()
        connection.setRequestProperty("Referer", "http://dept.inha.ac.kr/user/indexSub.do")
        val workbook = XSSFWorkbook(connection.getInputStream())
        val sheet = workbook.getSheetAt(0)
        val dates = dateRegex.find(sheet.getRow(1).getCell(1).toString())?.let {
            val (_, month, date) = it.groupValues
            DateUtils.getDaysOfWeek(month.toInt(), date.toInt())
        }!!

        val textCols = mutableListOf<MutableList<String>>(
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf()
        )
        for ((rowIndex, row) in sheet.withIndex()) {
            if (rowIndex >= 2) {
                for ((colIndex, cell) in row.withIndex()) {
                    if (colIndex in 1..7 && cell.toString().isNotBlank()) {
                        textCols[colIndex - 1].add(cell.toString())
                    }
                }
            }
        }
        for ((day, textCol) in textCols.withIndex()) {
            val date = dates[day]
            var menus = mutableListOf<String>()
            var count = 0
            for (text in textCol) {
                if (numberRegex.matches(text)) {
                    val type = count.let {
                        if (day in 0..4) { // weekdays
                            when (it) {
                                0, 1 -> "조식"
                                2 -> "중식"
                                3 -> "석식"
                                else -> ""
                            }
                        } else { // weekends
                            when (it) {
                                0 -> "조식"
                                1 -> "중식"
                                2 -> "석식"
                                else -> ""
                            }
                        }
                    }
                    if (menus.indexOf("<한식>") >= 0)
                        menus = menus.drop(menus.indexOf("<한식>")).toMutableList()
                    if (menus.indexOf("<일품식>") >= 0)
                        menus = menus.drop(menus.indexOf("<일품식>")).toMutableList()
                    var category = menus[0]
                        .replace("<", "")
                        .replace(">", "")
                    val menu = menus.drop(1)
                    val price = 4000
                    val meal = Meal(date, "${type}_${category}", menu, price)
                    meal.show()
                    meals += meal
                    menus = mutableListOf()
                    count += 1
                } else {
                    menus.add(text)
                }
            }
        }
        return meals
    }
}
