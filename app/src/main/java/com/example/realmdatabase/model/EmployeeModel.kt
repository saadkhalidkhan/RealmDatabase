package com.example.realmdatabase.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required


open class EmployeeModel : RealmObject() {

    @PrimaryKey
    @Required
    var name: String? = null
    var age = 0

    var skills: RealmList<SkillModel>? = null
}