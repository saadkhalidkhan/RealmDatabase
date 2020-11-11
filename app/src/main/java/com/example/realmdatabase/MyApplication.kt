package com.example.realmdatabase

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initRealm()
    }

    private fun initRealm() {

        Realm.init(applicationContext)

        //default instance
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)


//        val config = RealmConfiguration.Builder()
//            .name("test.db")
//            .schemaVersion(1)
//            .deleteRealmIfMigrationNeeded()
//            .build()
//
//        val mRealm = Realm.getInstance(config)
    }

}
