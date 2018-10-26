package com.bprit.app.bprit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bprit.app.bprit.ui.componentlist.ComponentListFragment

class ComponentListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_list_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ComponentListFragment.newInstance())
                .commitNow()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                // Back button is clicked
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_component_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_sync -> {
                Toast.makeText(this, "Clicked: Menu Sync", Toast.LENGTH_SHORT).show();
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
