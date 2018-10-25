package com.bprit.app.bprit.model

import android.app.Application
import android.content.Context
import io.realm.Realm
import io.realm.Realm.setDefaultConfiguration
import io.realm.RealmConfiguration
import io.realm.RealmObjectSchema
import io.realm.RealmSchema
import io.realm.DynamicRealm
import io.realm.RealmMigration





/**
 * TODO XML description
 */
class Global : Application()
{
    companion object {
        /**
         * TODO XML description
         */
        fun getPreferenceFileKey() : String = "SharedPreferences"

        /**
         * TODO XML description
         */
        fun getApiOrdersUrl() : String = "http://bpr-orders.f66c82vmic.eu-west-1.elasticbeanstalk.com"

        /**
         * TODO XML description
         */
        fun getApiCustomersUrl() : String = "http://bpr-customers.ysd42f9gnn.eu-west-1.elasticbeanstalk.com/"

        /**
         * TODO XML description
         */
        fun getMinuteTimeInterval() : Int = 5
    }

    private var context: Context? = null

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"

        // Debug realm
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build() // Only while developing the app!!!

        // Release realm
        // To make an migration bump "schemaVersion" 1 up and write the changes in the method "realmMigration".
        // TODO Public version has schema version 0! When next version is made public set schema version to 1 and collect migrations in "oldVersion == 0".
//        val config = RealmConfiguration.Builder().schemaVersion(5).migration(realmMigration).build()
        Realm.setDefaultConfiguration(config)
    }

    // Controls migrations in release
    var realmMigration: RealmMigration = RealmMigration { realm, oldVersion, newVersion ->
        var oldVersion = oldVersion
        // More info at: https://realm.io/docs/java/latest/#migrations
        val schema = realm.schema

        // ----------- DO NOT EDIT MIGRATIONS ABOVE THIS LINE --------------


        // Eksempel på migration
        //            if (oldVersion == 0) {
        //                schema.create("Person")
        //                        .addField("name", String.class)
        //                        .addField("age", int.class);
        //                oldVersion++;
        //            }
        //
        //            if (oldVersion == 1) {
        //                schema.get("Person")
        //                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
        //                        .addRealmObjectField("favoriteDog", schema.get("Dog"))
        //                        .addRealmListField("dogs", schema.get("Dog"));
        //                oldVersion++;
        //            }
    }
}