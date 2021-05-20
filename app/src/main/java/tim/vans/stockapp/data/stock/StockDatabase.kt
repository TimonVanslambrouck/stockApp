package tim.vans.stockapp.data.stock

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Source for all Android Room code: https://www.youtube.com/watch?v=lwAvI3WDXBY&t=809s & https://developer.android.com/training/data-storage/room#additional-resources

@Database(entities = [Stock::class], version = 1, exportSchema = false )
abstract class StockDatabase: RoomDatabase() {

    abstract fun stockDao(): StockDao

    companion object{
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getDatabase(context: Context): StockDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                // instance already exists
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    StockDatabase::class.java,
                    "stock_database"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}