package com.bpr_it.app.bpr_it

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bpr_it.app.bpr_it.ui.taskdetails.TaskDetailsFragment

class TaskDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_details_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TaskDetailsFragment.newInstance())
                .commitNow()
        }
    }

}