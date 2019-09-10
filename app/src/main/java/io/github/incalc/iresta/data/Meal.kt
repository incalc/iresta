package io.github.incalc.iresta.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.incalc.iresta.utils.DateUtils
import java.util.*


@Entity(tableName = "meals")
data class Meal(
    val date: Date,
    val category: String,
    val menus: List<String>,
    val price: Number
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun show() {
        println("date: ${DateUtils.format(date)}, category: $category, menus: $menus, price: $price")
    }
}
