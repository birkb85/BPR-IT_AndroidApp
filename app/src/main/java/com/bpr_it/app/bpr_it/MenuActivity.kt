package com.bpr_it.app.bpr_it

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bpr_it.app.bpr_it.ui.menu.MenuFragment
import android.content.Intent
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK


class MenuActivity : AppCompatActivity() {

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
                // Man klikker på tilbage knappen
                // Genstarter applikation
                finish()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK // Afslut alle activities der måtte være i baggrunden.
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
