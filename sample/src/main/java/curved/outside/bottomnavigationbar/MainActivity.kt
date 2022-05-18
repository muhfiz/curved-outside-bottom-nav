package curved.outside.bottomnavigationbar

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import curved.outside.curved_outside_bottom_nav.CurvedOutsideBottomNav

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val curvedOutsideBottomNav = findViewById<CurvedOutsideBottomNav>(R.id.nav)
//        curvedOutsideBottomNav.onItemSelectedListener = {prevPos, pos ->
//            supportFragmentManager.beginTransaction().apply {
//                replace(R.id.container, WebViewFragment.newInstance("https://www.google.com"))
//            }.commit()
//        }
//        curvedOutsideBottomNav.setItems(
//            CurvedOutsideBottomNav.Item("test1", R.drawable.ic_analytics),
//            CurvedOutsideBottomNav.Item("test2", R.drawable.ic_analytics),
//            CurvedOutsideBottomNav.Item("test3", R.drawable.ic_analytics),
//            CurvedOutsideBottomNav.Item("test4", R.drawable.ic_analytics),
//            CurvedOutsideBottomNav.Item("test5", R.drawable.ic_analytics)
//        )

        setupNavigationBar(curvedOutsideBottomNav)
    }

    private fun setupNavigationBar(curvedOutsideBottomNav: CurvedOutsideBottomNav) {
        val items = ArrayList<CurvedOutsideBottomNav.Item>()
        items.add(CurvedOutsideBottomNav.Item("Home", R.drawable.ic_home))
        items.add(CurvedOutsideBottomNav.Item("Market", R.drawable.ic_market))
        items.add(
            CurvedOutsideBottomNav.Item(
                "Analysis",
                R.drawable.ic_analytics
            )
        )
        items.add(CurvedOutsideBottomNav.Item("News", R.drawable.ic_news))
        items.add(
            CurvedOutsideBottomNav.Item(
                "Education",
                R.drawable.ic_education
            )
        )
        curvedOutsideBottomNav.setItems(*items.toTypedArray())
    }

}