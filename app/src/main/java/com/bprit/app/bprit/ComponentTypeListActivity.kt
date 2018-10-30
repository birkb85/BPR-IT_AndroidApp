package com.bprit.app.bprit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bprit.app.bprit.interfaces.CallbackSynchronizeData
import com.bprit.app.bprit.models.SynchronizeData
import com.bprit.app.bprit.ui.componenttypelist.ComponentTypeListFragment

/**
 * Component type list activity.
 */
class ComponentTypeListActivity : AppCompatActivity() {

    /**
     * Method called when activity is created.
     * @param savedInstanceState variable holding data if activity is recreated after being destroyed.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_type_list_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ComponentTypeListFragment.newInstance())
                .commitNow()
        }
    }

    /**
     * Method called when key is pressed on device.
     * @param keyCode the key code of the key.
     * @param event the event of the key.
     * @return returns true if key is handled in method, else event is returned.
     */
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

    /**
     * Method called when options menu is created.
     * @param menu the menu instance object created.
     * @return true if menu is created
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_component_type_list, menu)
        return true
    }
}
