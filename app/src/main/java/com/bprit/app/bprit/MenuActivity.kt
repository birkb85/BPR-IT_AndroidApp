package com.bprit.app.bprit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bprit.app.bprit.ui.menu.MenuFragment
import android.content.Intent
import android.view.KeyEvent
import android.R.attr.fragment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bprit.app.bprit.interfaces.CallbackSynchronizeData
import com.bprit.app.bprit.models.SynchronizeData


class MenuActivity : AppCompatActivity() {

    // BB 2018-10-26. Unused (used in three dot menu)
//    private val MENU_APPROVE = Menu.FIRST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MenuFragment.newInstance())
                .commitNow()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                // Back button is clicked
                finish()

                // If I need to restart application, use this code below.
//                val intent = Intent(this, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
//                        Intent.FLAG_ACTIVITY_CLEAR_TASK // Afslut alle activities der måtte være i baggrunden.
//                startActivity(intent)

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_menu, menu)
        return true
    }

    // BB 2018-10-26. How to create three dots menu
//    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
////        val fragment = supportFragmentManager.findFragmentById(R.id.container) as MenuFragment
//        menu.clear()
////        if (fragment.getShowMenuOptionApprove()) menu.add(0, MENU_APPROVE, Menu.NONE, "Test")
//        menu.add(0, MENU_APPROVE, Menu.NONE, "Test")
//        return super.onPrepareOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.getItemId()
//
//        val fragment = supportFragmentManager.findFragmentById(R.id.container) as MenuFragment
//
//        when (id) {
//            MENU_APPROVE -> {
////                fragment.approveDay()
//                return true
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
}
