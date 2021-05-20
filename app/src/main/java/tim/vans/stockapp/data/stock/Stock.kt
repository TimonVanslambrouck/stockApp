package tim.vans.stockapp.data.stock

import androidx.room.*
import kotlin.math.round

// Source for all Android Room code: https://www.youtube.com/watch?v=lwAvI3WDXBY&t=809s & https://developer.android.com/training/data-storage/room#additional-resources

@Entity(tableName = "stock_table")
data class Stock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name_stock")
    val nameStock: String,
    @ColumnInfo(name = "bought_amount")
    var boughtAmount: Int,
    @ColumnInfo(name = "bought_price")
    val boughtPrice: Double,
    @ColumnInfo(name = "current_price")
    var currentPrice: Double,
    @ColumnInfo(name = "stat_money")
    var statMoney: Double = roundMoney(boughtAmount, boughtPrice, currentPrice),
    @ColumnInfo(name = "stat_percent")
    var statPercent: Double = roundPercent(currentPrice, boughtPrice)
) {
    fun updateStats() {
        statMoney = roundMoney(boughtAmount, boughtPrice, currentPrice)
        statPercent = roundPercent(currentPrice, boughtPrice)
    }
}

fun roundMoney(boughtAmount: Int, boughtPrice: Double, currentPrice: Double): Double {
    var money = (currentPrice-boughtPrice)*boughtAmount
    money *= 100
    money = round(money)
    money /= 100
    return money
}

fun roundPercent(currentPrice: Double, boughtPrice: Double): Double {
    var percent = ((currentPrice/boughtPrice)-1)*100
    percent *= 1000
    percent = round(percent)
    percent /= 1000
    return percent
}



