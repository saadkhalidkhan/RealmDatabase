package com.example.realmdatabase.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required


open class SkillModel : RealmObject() {

    @PrimaryKey
    @Required
    var skillName: String? = null
}