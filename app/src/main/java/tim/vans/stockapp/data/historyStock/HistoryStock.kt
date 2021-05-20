package tim.vans.stockapp.data.historyStock

import androidx.room.*
import kotlin.math.round


// Source for all Android Room code: https://www.youtube.com/watch?v=lwAvI3WDXBY&t=809s & https://developer.android.com/training/data-storage/room#additional-resources

@Entity(tableName = "history_stock_table")
data class HistoryStock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name_stock")
    val nameStock: String,
    @ColumnInfo(name = "bought_amount")
    val boughtAmount: Int,
    @ColumnInfo(name = "bought_price")
    val boughtPrice: Double,
    @ColumnInfo(name = "sold_amount")
    val soldAmount: Int,
    @ColumnInfo(name = "sold_price")
    val soldPrice: Double,
    @ColumnInfo(name = "stat_money")
    val statMoney: Double = roundMoney(soldAmount,soldPrice,boughtPrice),
    @ColumnInfo(name = "stat_percent")
    val statPercent: Double = roundPercent(soldPrice,boughtPrice)
)

fun roundMoney(soldAmount: Int, soldPrice: Double, boughtPrice: Double): Double {
    var money = (soldAmount*soldPrice)-(soldAmount*boughtPrice)
    money *= 100
    money = round(money)
    money /= 100
    return money
}

fun roundPercent(soldPrice: Double, boughtPrice: Double): Double {
    var percent = (soldPrice/boughtPrice)-1
    percent *= 1000
    percent = round(percent)
    percent /= 1000
    return percent
}



