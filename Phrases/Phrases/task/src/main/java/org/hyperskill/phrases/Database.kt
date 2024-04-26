package org.hyperskill.phrases

import android.app.Application
import androidx.room.*

@Entity(tableName = "phrases")
data class Phrase(
    @ColumnInfo(name = "phrase") var phrase: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)

@Database(entities = [Phrase::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getPhraseDao(): PhraseDao
}

class AppDatabaseWrapper : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "phrases.db"
        ).allowMainThreadQueries().build()
    }
}

@Dao
interface PhraseDao {
    @Insert
    fun insert(vararg phrase: Phrase)

    @Delete
    fun delete(phrase: Phrase)

    @Query("SELECT * FROM phrases")
    fun getAll(): List<Phrase>

    @Query("SELECT * FROM phrases ORDER BY random() LIMIT 1")
    fun get(): Phrase

    @Query("SELECT * FROM phrases WHERE phrase = :text")
    fun getByText(text: String): Phrase

}