package com.example.contentproviderdemo.view


import android.content.Intent

import android.os.Bundle

import android.view.View

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.contentproviderdemo.databinding.ActivityShowDetailBinding
import com.example.contentproviderdemo.modal.ContactRepository
import com.example.contentproviderdemo.modal.db.dao.ContactDao
import com.example.contentproviderdemo.modal.db.database.ContactDatabase
import com.example.contentproviderdemo.util.Contact
import com.example.contentproviderdemo.util.ViewModelTypes
import com.example.contentproviderdemo.viewmodel.MainViewModalFactory
import com.example.contentproviderdemo.viewmodel.ShowContactViewModel

class ShowDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityShowDetailBinding
    lateinit var userDao: ContactDao
    lateinit var repository : ContactRepository
    lateinit var viewModel: ShowContactViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDao = ContactDatabase.getInstance(this).contactDao()
        repository = ContactRepository(userDao)
        viewModel= ViewModelProvider(this,MainViewModalFactory(repository,this,ViewModelTypes.SHOWCONTACTVIEWMODEL)).get(ShowContactViewModel::class.java)


        var bundle: Bundle? = intent.getBundleExtra("user")
        var position = intent.getIntExtra("position",0)
        var list  = bundle?.getSerializable("contact") as MutableList<Contact>
        Toast.makeText(this,"${list.size}",Toast.LENGTH_SHORT).show()

        var adapter = ViewPagerAdapter(supportFragmentManager,lifecycle,list)
        binding.viewpager.adapter = adapter
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewpager.currentItem  = position

        binding.editBt.setOnClickListener(View.OnClickListener {
            var i  = Intent(this, ContactEditActvity::class.java)
            var bundle =Bundle()
            bundle.putParcelable("user",list.get(binding.viewpager.currentItem))
            i.putExtra("data",bundle)
            startActivity(i)
        })
        binding.deleteBt.setOnClickListener(View.OnClickListener {
            var curUser = binding.viewpager.currentItem
            viewModel.getDeleteUser(list.get(curUser).id!!)
            list.removeAt(curUser)
            adapter.notifyDataSetChanged()
//            binding.viewpager.currentItem  = curUser+1
            finish()
        })

//        setdata(bundle!!)
//        binding.editBt.setOnClickListener(View.OnClickListener {
//            var i  = Intent(this@ShowDetailActivity, ContactEditActvity::class.java)
//            i.putExtra("data",bundle)
//            startActivity(i)
//        })
    }

//    fun setdata(data : Bundle){
//        var data1 : Contact = data.get("contact") as Contact
//        setName(data1.name)
//        setNumber(data1)
//        setAddress(data1)
//        setImage(data1)
//        setEmail(data1)
//    }

//    fun setName(data1 : String?){
//        binding.nameEdt.setText(data1)
//    }
//    fun setNumber(data1 : Contact){
//        if(data1.number!=null && data1.number!!.size > 0){
//            var count=0
//            binding.pl.visibility = View.VISIBLE
//            for ((key,value) in data1.number!!){
//                binding.phoneEdt.apply {
//                    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val rowView: View = inflater.inflate(R.layout.phone_child, null)
//                    binding.phoneEdt!!.addView(rowView,binding.phoneEdt!!.childCount)
//                    visibility = View.VISIBLE
//                    var row = binding.phoneEdt.getChildAt(count)
//                    row.findViewById<TextView>(R.id.phone).setText(value)
//                    row.findViewById<TextView>(R.id.label).setText(key)
//                    row.findViewById<ImageView>(R.id.callbt).setOnClickListener(View.OnClickListener {
//                        try {
//                            val intent = Intent(Intent.ACTION_DIAL)
//                            intent.apply {
//                                setData(Uri.parse(  "tel:"+value))
//                            }
//                            startActivity(intent)
//                        }catch (e :Exception){
//
//                        }
//                    })
//                    row.findViewById<ImageView>(R.id.msgbt).setOnClickListener(View.OnClickListener {
//                        val intent = Intent(Intent.ACTION_SENDTO).apply {
//                            setData(Uri.parse("smsto:"+value))
//                        }
//                        startActivity(intent)
//                    })
//                    count++
//                }
//            }
//        }
//    }
//    fun setEmail(data1 : Contact){
//        if(data1.email!=null && data1.email!!.size > 0){
//            var count =0
//            binding.el.visibility = View.VISIBLE
//            for ((key,value) in data1.email!!){
//                binding.emailEdt.apply {
//                    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val rowView: View = inflater.inflate(R.layout.email_child, null)
//                    binding.emailEdt!!.addView(rowView,binding.emailEdt!!.childCount)
//                    visibility = View.VISIBLE
//                    var row = binding.emailEdt.getChildAt(count)
//                    row.findViewById<TextView>(R.id.email).setText(value)
//                    row.findViewById<TextView>(R.id.label).setText(key)
//                    row.findViewById<ImageView>(R.id.emailbt).setOnClickListener(View.OnClickListener {
//                        try {
//                            var intent = Intent(Intent.ACTION_SEND).apply {
//                                setData(Uri.parse("mailto:"))
//                                type = "text/plain"
//                                putExtra(Intent.EXTRA_EMAIL, arrayOf(value))
//                            }
//                            startActivity(Intent.createChooser(intent,"Choose a Email Client"))
//                        }catch (e :Exception){
//                            println(e.localizedMessage)
//                        }
//                    })
//                    count++
//                }
//            }
//        }
//    }
//    fun setAddress(data1 : Contact){
//        if(data1.address!=null && data1.address!!.size > 0){
//            var count=0
//            binding.al.visibility = View.VISIBLE
//            for ((key,value) in data1.address!!){
//                binding.addressEdt.apply {
//                    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                    val rowView: View = inflater.inflate(R.layout.address_child, null)
//                    binding.addressEdt!!.addView(rowView,binding.addressEdt!!.childCount)
//                    visibility = View.VISIBLE
//                    var row = binding.addressEdt.getChildAt(count)
//                    row.findViewById<TextView>(R.id.address).setText(value)
//                    row.findViewById<TextView>(R.id.label).setText(key)
//                    count++
//                }
//            }
//        }
//    }
//    fun setImage(data1 : Contact){
//        if(!data1.image.equals("") && data1.image != null) {
//            binding.myImg.visibility = View.VISIBLE
//            var image : Bitmap? = null
//            image = BitmapFactory.decodeFile(data1.image)
//            if(image != null){
//                binding.myImg.setImageBitmap(image)
//            }else{
//                binding.myImg.apply {
//                    this.setImageDrawable(getDrawable(R.drawable.round))
//                }
//                binding.nameLabel.apply {
//                    setText(data1.name!!.subSequence(0,1).toString().toUpperCase())
//                    visibility = View.VISIBLE
//                }
//            }
//        }
//    }
}