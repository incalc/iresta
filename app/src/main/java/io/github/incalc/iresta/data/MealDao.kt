package io.github.incalc.iresta.data

import androidx.room.*


@Dao
interface MealDao {
    @Delete
    fun delete(meal: Meal)

    @Delete
    fun deleteAll(meals: List<Meal>)

    @Query("SELECT * FROM meals")
    fun getAll(): List<Meal>

    @Insert
    fun insert(meal: Meal)

    @Insert
    fun insert(vararg meals: Meal)

    @Insert
    fun insertAll(meals: List<Meal>)

    @Update
    fun update(meal: Meal)

    @Update
    fun updateAll(meals: List<Meal>)
}
