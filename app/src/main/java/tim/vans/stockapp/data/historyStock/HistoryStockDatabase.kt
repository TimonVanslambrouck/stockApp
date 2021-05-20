package tim.vans.stockapp.data.historyStock

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tim.vans.stockapp.data.stock.Stock
import tim.vans.stockapp.data.stock.StockDao
import tim.vans.stockapp.data.stock.StockDatabase

// Source for all Android Room code: https://www.youtube.com/watch?v=lwAvI3WDXBY&t=809s & https://developer.android.com/training/data-storage/room#additional-resources

@Database(entities = [HistoryStock::class], version = 1, exportSchema = false )
abstract class HistoryStockDatabase: RoomDatabase() {

    abstract fun historyStockDao(): HistoryStockDao

    companion object{
        @Volatile
        private var INSTANCE: HistoryStockDatabase? = null

        fun getDatabase(context: Context): HistoryStockDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                // instance already exists
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    HistoryStockDatabase::class.java,
                    "history_stock_database"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}