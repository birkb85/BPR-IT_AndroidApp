package com.bpr_it.app.bpr_it

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.bpr_it.app.bpr_it.ui.tasklist.TaskListFragment
import android.view.KeyEvent.KEYCODE_BACK



class TaskListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_list_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TaskListFragment.newInstance())
                .commitNow()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                // Man klikker på tilbage knappen
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
