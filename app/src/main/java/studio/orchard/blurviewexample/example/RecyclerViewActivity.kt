package studio.orchard.blurviewexample.example

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import studio.orchard.blurview.BlurView
import studio.orchard.blurviewexample.BaseActivity
import studio.orchard.blurviewexample.R

class RecyclerViewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_example)
        setStatusBarNavigationBar()

        val appBar: AppBarLayout = findViewById(R.id.example_appbar)
        val toolbar: Toolbar = findViewById(R.id.example_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "RecyclerView Example"

        val itemList : MutableList<String> = mutableListOf()
        for(i in 0..100) {
            itemList.add(i.toString())
        }
        val recyclerView = findViewById<RecyclerView>(R.id.example_recyclerview)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.addItemDecoration(RecyclerViewAdapter.ItemDecoration(this, 3,10, getAppBarHeight()))
        recyclerView.itemAnimator = DefaultItemAnimator()
        val recyclerViewAdapter = RecyclerViewAdapter(itemList)
        recyclerViewAdapter.onItemClickListener = { _: View, i: Int ->
            Toast.makeText(this, "selected: ${itemList[i]}", Toast.LENGTH_SHORT).show();
        }
        recyclerView.adapter = recyclerViewAdapter

        val blurView = findViewById<BlurView>(R.id.example_blurview)
        val targetView = findViewById<FrameLayout>(R.id.example_targetview)
        blurView.setTarget(targetView).setBinding(appBar).setName("RecyclerViewActivity").enable()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}