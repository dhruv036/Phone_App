package com.example.contentproviderdemo.view

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contentproviderdemo.R
import com.example.contentproviderdemo.databinding.ActivityMainBinding
import com.example.contentproviderdemo.modal.db.database.ContactDatabase
import com.example.contentproviderdemo.util.Contact
import com.example.contentproviderdemo.modal.ContactRepository
import com.example.contentproviderdemo.util.ViewModelTypes
import com.example.contentproviderdemo.viewmodel.MainViewModel
import com.example.contentproviderdemo.viewmodel.MainViewModalFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private lateinit var mainViewModal: MainViewModel
    var PERMISSION_CODE = 101
    lateinit var adapter1 : Adapter
    var contactList : MutableList<Contact> = mutableListOf()
    lateinit var repository : ContactRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var  userDao  = ContactDatabase.getInstance(applicationContext).contactDao()
         repository  = ContactRepository(userDao)
        mainViewModal =  ViewModelProvider(this,
            MainViewModalFactory(repository,applicationContext,ViewModelTypes.MAINVIEWMODEL)).get(MainViewModel::class.java)
        checkPermission()

        adapter1 = Adapter(mutableListOf(),this)
        binding.recycleview.adapter = adapter1


        binding.addContactBt.setOnClickListener(View.OnClickListener {
            binding.Lay.visibility = View.GONE
            supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.addContactyout, AddContactFragment()
            ).commit()
            Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show()
        })

       binding.searchBox.setOnQueryTextListener(object : OnQueryTextListener{
           override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
           }
           override fun onQueryTextChange(sequence: String?): Boolean {

               if(sequence.isNullOrEmpty()){
                   adapter1 = Adapter(contactList,applicationContext)
               }else{
                   adapter1 = Adapter(getFilteredList(sequence),applicationContext)
               }
               binding.recycleview.adapter = adapter1
               adapter1.notifyDataSetChanged()
               return true
           }
       })
    }
     fun getFilteredList(sequence: String): MutableList<Contact>{
         var filteredList: MutableList<Contact> = mutableListOf()
             var filterPattern = sequence.lowercase().trim()
             for (item in contactList) {
                 var isPresentInPhone =false
                 var isPresentInEmail =false
                 for((key,value) in item.number!!){
                     if(value.contains(sequence)){
                         isPresentInPhone = true
                         break
                     }
                 }
                 for((key,value) in item.email!!){
                     if(value.contains(sequence)){
                        isPresentInEmail = true
                         break
                     }
                 }
                 if(item.name!!.lowercase().contains(filterPattern) || isPresentInEmail || isPresentInPhone ){
                     filteredList.add(item)
                 }
             }
         return filteredList
     }


    fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestReadPermission()
        }else{
            fetchData()
        }
    }

    fun requestReadPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)){
            AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("This permission is needed to access your contacts")
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_CODE
                        )
                    })
                .setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create().show()

        }
        else {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_CONTACTS), PERMISSION_CODE);
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSION_CODE){
           if( grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED) ){
               fetchData()
           }else{
               checkPermission()
           }
       }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun fetchData(){
        binding.progress.visibility = View.VISIBLE
        binding.recycleview.visibility= View.GONE
        GlobalScope.launch(Dispatchers.IO) {
            if(repository.countOFContact() > 0){
                fromDb()
            }else{
                fromContentProvider()
            }
        }
    }

    suspend fun fromDb(){
        var contactsFromDB = mutableListOf<Contact>()

        withContext(Dispatchers.Main){
            repository.fetchAllContact().observeForever(Observer {
                Log.d("TAG",it?.size.toString())
                contactsFromDB =  repository.fetchDataFromDB(applicationContext,it)
                binding.number.setText(contactsFromDB.size.toString()+" Contacts")
                binding.progress.visibility = View.GONE
                contactList = contactsFromDB
                adapter1 = Adapter(contactsFromDB,applicationContext)
                binding.recycleview.apply {
                    layoutManager = LinearLayoutManager(applicationContext)
                    visibility =View.VISIBLE
                    this.adapter  = adapter1
                }
            })
        }
    }

   suspend fun fromContentProvider(){
        withContext(Dispatchers.Main){
            mainViewModal.contactListFromContentProvider.observeForever( Observer {
                binding.number.setText(it.size.toString()+" Contacts")
                binding.progress.visibility = View.GONE
                contactList = it
                adapter1 = Adapter(it,applicationContext)
                binding.recycleview.apply {
                    layoutManager = LinearLayoutManager(applicationContext)
                    visibility =View.VISIBLE
                    this.adapter  = adapter1
                }
                adapter1.notifyDataSetChanged()
            })
            mainViewModal.fetchDataFromContentProvider()
        }

    }

    override fun getSupportFragmentManager(): FragmentManager {
       if(binding.Lay.visibility == View.GONE){
           binding.Lay.visibility == View.VISIBLE
       }
        return super.getSupportFragmentManager()
    }

    override fun onRestart() {
        super.onRestart()
        binding.Lay.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        binding.Lay.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.Lay.visibility = View.VISIBLE
    }
}