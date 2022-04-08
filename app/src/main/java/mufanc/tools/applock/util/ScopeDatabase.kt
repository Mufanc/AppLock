package mufanc.tools.applock.util

import androidx.room.*
import mufanc.tools.applock.MyApplication

@Database(entities = [ScopeDatabase.AppEntity::class], version = 1, exportSchema = false)
abstract class ScopeDatabase : RoomDatabase() {

    companion object {
        private val database = Room.databaseBuilder(
            MyApplication.context,
            ScopeDatabase::class.java, "scope"
        ).allowMainThreadQueries().build()

        fun readScope(): MutableSet<String> {
            return database.dao().query().map { it.packageName }.toMutableSet()
        }

        fun writeScope(scope: MutableSet<String>) {
            database.dao().clear()
            database.dao().insert(*scope.map { AppEntity(0, it) }.toTypedArray())
        }
    }

    @Entity
    data class AppEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "packageName") val packageName: String = ""
    )

    @Dao
    interface Scope {
        @Insert
        fun insert(vararg apps: AppEntity)

        @Query("DELETE FROM AppEntity")
        fun clear()

        @Query("SELECT * FROM AppEntity")
        fun query(): List<AppEntity>
    }

    abstract fun dao(): Scope
}