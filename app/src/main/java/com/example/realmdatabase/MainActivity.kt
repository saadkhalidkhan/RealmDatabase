package com.example.realmdatabase

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.realmdatabase.model.EmployeeModel
import com.example.realmdatabase.model.SkillModel
import com.example.realmdatabase.utility.AppConstants.PROPERTY_AGE
import com.example.realmdatabase.utility.AppConstants.PROPERTY_NAME
import com.example.realmdatabase.utility.AppConstants.PROPERTY_SKILL
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mRealm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init realm
        mRealm = Realm.getDefaultInstance()

        init_listeners()

    }

    private fun init_listeners() {
        btnAdd.setOnClickListener(this)
        btnRead.setOnClickListener(this)
        btnUpdate.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnDeleteWithSkill.setOnClickListener(this)
        btnFilterByAge.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAdd -> addEmployee()
            R.id.btnRead -> readEmployeeRecords()
            R.id.btnUpdate -> updateEmployeeRecords()
            R.id.btnDelete -> deleteEmployeeRecord()
            R.id.btnDeleteWithSkill -> deleteEmployeeWithSkill()
            R.id.btnFilterByAge -> filterByAge()
        }
    }

    private fun addEmployee() {
        var realm: Realm? = null
        try {
            realm = Realm.getDefaultInstance()
            realm.executeTransaction(Realm.Transaction { realm ->
                try {
                    if (inName.text.toString().trim().isNotEmpty()) {
                        val employee = EmployeeModel()
                        employee.name = inName.text.toString().trim()
                        if (inAge.text.toString().trim().isNotEmpty()) employee.age =
                            inAge.text.toString().trim().toInt()
                        val languageKnown: String = inSkill.text.toString().trim()
                        if (languageKnown.isNotEmpty()) {
                            var skill: SkillModel? = realm.where<SkillModel>(SkillModel::class.java)
                                .equalTo(PROPERTY_SKILL, languageKnown).findFirst()
                            if (skill == null) {
                                skill =
                                    realm.createObject<SkillModel>(
                                        SkillModel::class.java,
                                        languageKnown
                                    )
                                realm.copyToRealm(skill)
                            }
                            employee.skills = RealmList<SkillModel>()
                            employee.skills!!.add(skill)
                        }
                        realm.copyToRealm(employee)
                    }
                } catch (e: RealmPrimaryKeyConstraintException) {
                    Toast.makeText(
                        applicationContext,
                        "Primary Key exists, Press Update instead",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } finally {
            realm?.close()
        }
    }


    private fun readEmployeeRecords() {
        mRealm.executeTransaction { realm ->
            val results: RealmResults<EmployeeModel> =
                realm.where<EmployeeModel>(EmployeeModel::class.java).findAll()
            textView.text = ""
            for (employee in results) {
                textView.append(employee.name.toString() + " age: " + employee.age + " skill: " + employee.skills!!.size)
            }
        }
    }

    private fun updateEmployeeRecords() {
        mRealm.executeTransaction { realm ->
            if (!inName.getText().toString().trim().isEmpty()) {
                var employee: EmployeeModel? = realm.where<EmployeeModel>(EmployeeModel::class.java)
                    .equalTo(PROPERTY_NAME, inName.getText().toString()).findFirst()
                if (employee == null) {
                    employee = realm.createObject<EmployeeModel>(
                        EmployeeModel::class.java,
                        inName.getText().toString().trim()
                    )
                }
                if (!inAge.getText().toString().trim().isEmpty()) employee!!.age =
                    inAge.getText().toString().trim().toInt()
                val languageKnown: String = inSkill.getText().toString().trim()
                var skill: SkillModel? = realm.where<SkillModel>(SkillModel::class.java)
                    .equalTo(PROPERTY_SKILL, languageKnown).findFirst()
                if (skill == null) {
                    skill = realm.createObject<SkillModel>(SkillModel::class.java, languageKnown)
                    realm.copyToRealm(skill)
                }
                if (!employee!!.skills!!.contains(skill)) employee.skills!!.add(skill)
            }
        }
    }

    private fun deleteEmployeeRecord() {
        mRealm.executeTransaction { realm ->
            val employee: EmployeeModel = realm.where<EmployeeModel>(EmployeeModel::class.java)
                .equalTo(PROPERTY_NAME, inName.getText().toString()).findFirst()
            if (employee != null) {
                employee.deleteFromRealm()
            }
        }
    }

    private fun deleteEmployeeWithSkill() {
        mRealm.executeTransaction { realm ->
            val employees: RealmResults<EmployeeModel> =
                realm.where<EmployeeModel>(EmployeeModel::class.java)
                    .equalTo("skills.skillName", inSkill.getText().toString().trim()).findAll()
            employees.deleteAllFromRealm()
        }
    }


    private fun filterByAge() {
        mRealm.executeTransaction { realm ->
            val results: RealmResults<EmployeeModel> =
                realm.where<EmployeeModel>(EmployeeModel::class.java)
                    .greaterThanOrEqualTo(PROPERTY_AGE, 25)
                    .findAllSortedAsync(PROPERTY_NAME)
            txtFilterByAge.setText("")
            for (employee in results) {
                txtFilterByAge.append(employee.name.toString() + " age: " + employee.age + " skill: " + employee.skills!!.size)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mRealm != null) {
            mRealm.close()
        }
    }

}