package curved.outside.bottomnavigationbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import curved.outside.curved_outside_bottom_nav.CurvedOutsideBottomNav
import curved.outside.curved_outside_bottom_nav.CurvedOutsideBotNavItem

class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val curvedOutsideBottomNav = findViewById<CurvedOutsideBottomNav>(R.id.nav)

        setupNavigationBar(curvedOutsideBottomNav)
    }

    private fun setupNavigationBar(curvedOutsideBottomNav: CurvedOutsideBottomNav){
        val items = ArrayList<CurvedOutsideBotNavItem>()
        items.add(CurvedOutsideBotNavItem("Home", R.drawable.ic_home, View.generateViewId()))
        items.add(CurvedOutsideBotNavItem("Market", R.drawable.ic_market, View.generateViewId()))
        items.add(CurvedOutsideBotNavItem("Analysis", R.drawable.ic_analytics, View.generateViewId()))
        items.add(CurvedOutsideBotNavItem("News", R.drawable.ic_news, View.generateViewId()))
        items.add(CurvedOutsideBotNavItem("Education", R.drawable.ic_education, View.generateViewId()))
        curvedOutsideBottomNav.addItemsMenu(items)
    }

}