package tim.vans.stockapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import tim.vans.stockapp.data.stock.StockDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Source: https://www.youtube.com/watch?v=UMZZHHJ37bo

        val stocksFragment = StocksFragment()
        val historyFragment = HistoryFragment()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        makeCurrentFragment(stocksFragment,R.string.bought_stocks)

        // Source: https://www.youtube.com/watch?v=fODp1hZxfng&t=147s

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.myStocksButton -> makeCurrentFragment(stocksFragment,R.string.bought_stocks)
                R.id.historyButton -> makeCurrentFragment(historyFragment, R.string.sold_stocks)
                }
            true
            }
        }

    fun test(view: View){
        print("test")
    }

    private fun makeCurrentFragment(fragment: Fragment, id: Int){
        setTitle(id)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_fragment,fragment)
            commit()
        }
    }

    }
