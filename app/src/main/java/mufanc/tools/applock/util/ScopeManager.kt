package mufanc.tools.applock.util

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.room.*
import mufanc.tools.applock.MyApplication
import mufanc.tools.applock.R
import mufanc.tools.applock.core.shizuku.ShizukuHelper
import mufanc.tools.applock.core.xposed.AppLockManager

@Database(entities = [ScopeManager.AppEntity::class], version = 1, exportSchema = false)
abstract class ScopeManager : RoomDatabase() {

    companion object {

        val scope = mutableSetOf<String>()

        private lateinit var database: ScopeManager

        fun init(context: Context) {
            database = Room.databaseBuilder(context, ScopeManager::class.java, "scope")
                .allowMainThreadQueries().build()

            scope.clear()
            scope.addAll(
                database.dao().query().mapNotNull {  // 过滤已卸载应用
                    try {
                        context.packageManager.getApplicationInfo(
                            it.packageName,
                            PackageManager.MATCH_DISABLED_COMPONENTS
                        )
                        it.packageName
                    } catch (err: PackageManager.NameNotFoundException) {
                        null
                    }
                }
            )
        }

        fun commit() {
            database.dao().clear()
            database.dao().insert(*scope.map { AppEntity(0, it) }.toTypedArray())

            when (Settings.WORK_MODE.value) {
                Settings.WorkMode.XPOSED -> {
                    AppLockManager.client?.apply {
                        updateWhitelist(scope.toTypedArray())
                        Toast.makeText(MyApplication.context, R.string.scope_saved, Toast.LENGTH_SHORT).show()
                    }
                }
                Settings.WorkMode.SHIZUKU -> {
                    ShizukuHelper.writePackageList(scope.toList())
                }
            }
        }
    }

    @Entity
    data class AppEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "packageName") val packageName: String
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