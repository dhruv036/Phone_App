package com.example.contentproviderdemo.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.contentproviderdemo.R
import com.example.contentproviderdemo.databinding.FragmentShowContactBinding
import com.example.contentproviderdemo.util.Contact

class ShowContactFragment(var contact: Contact) : Fragment() {

    private lateinit var binding: FragmentShowContactBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FragmentShowContactBinding.inflate(layoutInflater,container,false)
        setdata(contact)
        return binding.root
    }
    fun setdata(contact: Contact){
        setName(contact.name)
        setNumber(contact)
        setAddress(contact)
        setImage(contact)
        setEmail(contact)
    }
    fun setName(data1 : String?){
        binding.nameEdt.setText(data1)
    }
    fun setNumber(data1 : Contact){
        if(data1.number!=null && data1.number!!.size > 0){
            var count=0
            binding.pl.visibility = View.VISIBLE
            for ((key,value) in data1.number!!){
                binding.phoneEdt.apply {
                    val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val rowView: View = inflater.inflate(R.layout.phone_child, null)
                    binding.phoneEdt!!.addView(rowView,binding.phoneEdt!!.childCount)
                    visibility = View.VISIBLE
                    var row = binding.phoneEdt.getChildAt(count)
                    row.findViewById<TextView>(R.id.phone).setText(value)
                    row.findViewById<TextView>(R.id.label).setText(key)
                    row.findViewById<ImageView>(R.id.callbt).setOnClickListener(View.OnClickListener {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.apply {
                                setData(Uri.parse(  "tel:"+value))
                            }
                            startActivity(intent)
                        }catch (e :Exception){

                        }
                    })
                    row.findViewById<ImageView>(R.id.msgbt).setOnClickListener(View.OnClickListener {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            setData(Uri.parse("smsto:"+value))
                        }
                        startActivity(intent)
                    })
                    count++
                }
            }
        }
    }
    fun setEmail(data1 : Contact){
        if(data1.email!=null && data1.email!!.size > 0){
            var count =0
            binding.el.visibility = View.VISIBLE
            for ((key,value) in data1.email!!){
                binding.emailEdt.apply {
                    val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val rowView: View = inflater.inflate(R.layout.email_child, null)
                    binding.emailEdt!!.addView(rowView,binding.emailEdt!!.childCount)
                    visibility = View.VISIBLE
                    var row = binding.emailEdt.getChildAt(count)
                    row.findViewById<TextView>(R.id.email).setText(value)
                    row.findViewById<TextView>(R.id.label).setText(key)
                    row.findViewById<ImageView>(R.id.emailbt).setOnClickListener(View.OnClickListener {
                        try {
                            var intent = Intent(Intent.ACTION_SEND).apply {
                                setData(Uri.parse("mailto:"))
                                type = "text/plain"
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(value))
                            }
                            startActivity(Intent.createChooser(intent,"Choose a Email Client"))
                        }catch (e :Exception){
                            println(e.localizedMessage)
                        }
                    })
                    count++
                }
            }
        }
    }
    fun setAddress(data1 : Contact){
        if(data1.address!=null && data1.address!!.size > 0){
            var count=0
            binding.al.visibility = View.VISIBLE
            for ((key,value) in data1.address!!){
                binding.addressEdt.apply {
                    val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val rowView: View = inflater.inflate(R.layout.address_child, null)
                    binding.addressEdt!!.addView(rowView,binding.addressEdt!!.childCount)
                    visibility = View.VISIBLE
                    var row = binding.addressEdt.getChildAt(count)
                    row.findViewById<TextView>(R.id.address).setText(value)
                    row.findViewById<TextView>(R.id.label).setText(key)
                    count++
                }
            }
        }
    }
    fun setImage(data1 : Contact){
        if(!data1.image.equals("") && data1.image != null) {
            binding.myImg.visibility = View.VISIBLE
            var image : Bitmap? = null
            image = BitmapFactory.decodeFile(data1.image)
            if(image != null){
                binding.myImg.setImageBitmap(image)
            }else{
                binding.myImg.apply {
                    this.setImageDrawable(activity?.getDrawable(R.drawable.round))
                }
                binding.nameLabel.apply {
                    setText(data1.name!!.subSequence(0,1).toString().toUpperCase())
                    visibility = View.VISIBLE
                }
            }
        }
    }
}