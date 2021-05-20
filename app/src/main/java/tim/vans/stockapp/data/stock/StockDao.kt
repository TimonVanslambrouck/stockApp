package tim.vans.stockapp.data.stock

import androidx.room.*

// Source for all Android Room code: https://www.youtube.com/watch?v=lwAvI3WDXBY&t=809s
// https://developer.android.com/training/data-storage/room#additional-resources
// https://www.youtube.com/watch?v=CcaCpRCACzU
// https://stackoverflow.com/a/46937134

@Dao
interface StockDao {

    // if book already exists don't add it again
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrReplaceStock(stock: Stock)

    @Query("SELECT * from stock_table ORDER BY id ASC")
    fun getAllData(): List<Stock>

    @Query("SELECT * FROM stock_table WHERE id=:id ")
    fun getStockByID(id: Int): Stock

    @Delete
    fun deleteStock(stock: Stock)

}