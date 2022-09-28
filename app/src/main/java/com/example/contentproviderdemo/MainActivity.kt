package com.example.contentproviderdemo

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contentproviderdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var model : ContactViewModal
    lateinit var binding : ActivityMainBinding
    var PERMISSION_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ContactViewModal(applicationContext)
        checkPermission()

    }
    fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
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
        model.launch()
        model.getContacts().observe(this, Observer {
            binding.number.setText(it.size.toString()+" Contacts")
            binding.progress.visibility = View.GONE
            binding.recycleview.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                visibility =View.VISIBLE
                adapter = it?.let { it1 -> Adapter(it1,applicationContext) }
            }
        })
    }
}


