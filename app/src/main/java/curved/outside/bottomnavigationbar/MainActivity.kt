package curved.outside.bottomnavigationbar

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import curved.outside.curved_outside_bottom_nav.CurvedOutsideBotNavItem
import curved.outside.curved_outside_bottom_nav.CurvedOutsideBottomNav

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val curvedOutsideBottomNav = findViewById<CurvedOutsideBottomNav>(R.id.nav)
        curvedOutsideBottomNav.onItemSelectedListener = {prevPos, pos ->
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.container, WebViewFragment.newInstance("https://www.google.com"))
            }.commit()
        }

        setupNavigationBar(curvedOutsideBottomNav)
    }

    private fun setupNavigationBar(curvedOutsideBottomNav: CurvedOutsideBottomNav) {
        val items = ArrayList<CurvedOutsideBotNavItem>()
        items.add(CurvedOutsideBotNavItem("Home", R.drawable.ic_home))
        items.add(CurvedOutsideBotNavItem("Market", R.drawable.ic_market))
        items.add(
            CurvedOutsideBotNavItem(
                "Analysis",
                R.drawable.ic_analytics
            )
        )
        items.add(CurvedOutsideBotNavItem("News", R.drawable.ic_news))
        items.add(
            CurvedOutsideBotNavItem(
                "Education",
                R.drawable.ic_education
            )
        )
        curvedOutsideBottomNav.setItemsMenu(items)
    }

}