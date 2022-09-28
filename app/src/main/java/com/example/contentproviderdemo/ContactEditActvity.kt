package com.example.contentproviderdemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import com.example.contentproviderdemo.databinding.ActivityContactEditBinding

class ContactEditActvity : AppCompatActivity() {
    lateinit var binding: ActivityContactEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle: Bundle? = intent.getBundleExtra("data")

        fetchData(bundle!!)

        binding.bt.setOnClickListener(View.OnClickListener {
            var row = binding.phoneLay.childCount-2
            for(i in 0..row){
               var i = binding.phoneLay.getChildAt(i) as View
                var text = i.findViewById<EditText>(R.id.phonenew).text.toString()
                Toast.makeText(this@ContactEditActvity,text.toString(),Toast.LENGTH_SHORT).show()
            }
        })
        binding.imageplus.setOnClickListener(View.OnClickListener {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView: View = inflater.inflate(R.layout.edit_phone_child, null)
            binding.phoneLay!!.addView(rowView,binding.phoneLay.childCount-1)
        })


    }
    fun fetchData(bundle : Bundle){
        var data = bundle.get("contact") as Contact

        binding.nameField.setText(data.name)
        var count =0
        var row:View
        for ((key,value) in data.number!!){
            binding.phoneLay.apply {
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView: View = inflater.inflate(R.layout.edit_phone_child, null)
                binding.phoneLay!!.addView(rowView,count)
                visibility = View.VISIBLE
                row = binding.phoneLay.getChildAt(count)
                row.findViewById<EditText>(R.id.phonenew).setText(value)
                row.findViewById<TextView>(R.id.label).setText(key)
                row.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
                    binding.phoneLay.removeView(row)
//                    row.visibility =View.GONE
                    Log.e("button","clicked")
                })
//                Toast.makeText(this@ContactEditActvity,key+" "+value,Toast.LENGTH_SHORT).show()
                count++
            }
        }

    }
}