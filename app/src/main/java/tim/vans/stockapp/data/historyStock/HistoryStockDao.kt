package tim.vans.stockapp.data.historyStock

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Source for all Android Room code: https://www.youtube.com/watch?v=lwAvI3WDXBY&t=809s
// https://developer.android.com/training/data-storage/room#additional-resources
// https://www.youtube.com/watch?v=CcaCpRCACzU

@Dao
interface HistoryStockDao {

    // if book already exists don't add it again
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrReplaceStock(historyStock: HistoryStock)

    @Query("SELECT * from history_stock_table ORDER BY id ASC")
    fun getAllData(): List<HistoryStock>

}