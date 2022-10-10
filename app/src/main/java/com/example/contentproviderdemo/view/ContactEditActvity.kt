package com.example.contentproviderdemo.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contentproviderdemo.R
import com.example.contentproviderdemo.databinding.ActivityContactEditBinding
import com.example.contentproviderdemo.modal.ContactRepository
import com.example.contentproviderdemo.modal.db.dao.ContactDao
import com.example.contentproviderdemo.modal.db.database.ContactDatabase
import com.example.contentproviderdemo.util.Contact
import com.example.contentproviderdemo.util.ViewModelTypes
import com.example.contentproviderdemo.viewmodel.AddContactFragmentViewModel
import com.example.contentproviderdemo.viewmodel.EditContactViewModel
import com.example.contentproviderdemo.viewmodel.MainViewModalFactory

class ContactEditActvity : AppCompatActivity() {

    lateinit var binding :ActivityContactEditBinding
    var phoneTypes = mutableMapOf<String,Boolean>("Home" to false,"Mobile" to false,"Work" to false,"Other" to false)
    var emailTypes = mutableMapOf<String,Boolean>("Home" to false,"Mobile" to false,"Work" to false,"Other" to false)
    var addressTypes = mutableMapOf<String,Boolean>("Home" to false,"Mobile" to false,"Work" to false,"Other" to false)
    var emailCount=0
    var phoneCount=0
    var addressCount=0
    lateinit var viewModal: EditContactViewModel
    lateinit var userDao : ContactDao
    lateinit var  repository :ContactRepository
    var uid =0L
    var isClicked = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_contact_edit)

        isClicked.observe(this, Observer {
            if(it == true){
                binding.edtbt.visibility =View.VISIBLE
            }

        })
        binding.edtbt.setOnClickListener(View.OnClickListener {
            saveContact()
        })
        binding.backbt.setOnClickListener(View.OnClickListener {
            finish()
        })

        userDao = ContactDatabase.getInstance(applicationContext).contactDao()
        repository = ContactRepository(userDao)
        viewModal = ViewModelProvider(this, MainViewModalFactory(repository,applicationContext,ViewModelTypes.EDITCONTACTVIEWMODEL)).get(EditContactViewModel::class.java)

        var bundle : Bundle? = intent.getBundleExtra("data")
        fetchData(bundle?.get("user") as Contact)

        binding.addNumber.setOnClickListener(View.OnClickListener {
            isClicked.value = true
            addPhoneField()
        })
        binding.addEmail.setOnClickListener(View.OnClickListener {
            isClicked.value = true
            addEmailField()
        })
        binding.addaddress.setOnClickListener(View.OnClickListener {
            isClicked.value = true
            addAddressField()
        })
        binding.nameField.setOnClickListener(View.OnClickListener {
            isClicked.value = true
        })
    }
    fun getName(): String{
        return binding.nameField.text.toString()
    }

    fun getALLPhones() : Pair<MutableMap<String,String>,Boolean>{
        var phones = mutableMapOf<String,String>("Home" to "","Mobile" to "","Work" to "","Other" to "")
        var numbercount = binding.phoneLay.childCount
        var isPhoneNumberCorrect = true
        if(numbercount > 0){
            for(i in 0..numbercount-1){
                var view = binding.phoneLay.get(i)
                var type  = view.findViewById<TextView>(R.id.phonelabel)?.text.toString()
                var number  = view.findViewById<EditText>(R.id.phonenew)?.text.toString()

                if(!type.isNullOrEmpty()){
                    if(!number.isNullOrEmpty() && number.length >=10){
                        isPhoneNumberCorrect =true
                        phones[type] =number
                    }else{
                        isPhoneNumberCorrect =false
                         Toast.makeText(this@ContactEditActvity,"Incorrect Number" ,Toast.LENGTH_SHORT).show()
                        break
                    }

                }
            }
        }
        return Pair(phones,isPhoneNumberCorrect)
    }

    fun getALLEmails(): Pair<MutableMap<String,String>,Boolean>{
        var emails = mutableMapOf<String,String>("Home" to "","Mobile" to "","Work" to "","Other" to "")
        var emailcount = binding.emailLay.childCount
        var isEmailCorrect = true
        if(emailcount > 0){
            for(i in 0..emailcount-1){
                var view = binding.emailLay.get(i)
                var type = view.findViewById<TextView>(R.id.emaillabel)?.text.toString()
                var email = view.findViewById<EditText>(R.id.emailnew)?.text.toString()
                if(!type.isNullOrEmpty()){
                    if(!email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        isEmailCorrect =true
                        emails[type] = email
                    }else{
                        isEmailCorrect =false
                        Toast.makeText(this@ContactEditActvity,"Incorrect Email" ,Toast.LENGTH_SHORT).show()
                        break
                    }

                }
            }
        }
        return Pair(emails,isEmailCorrect)
    }

    fun getALLAddress(): MutableMap<String,String>{
        var address = mutableMapOf<String,String>("Home" to "","Mobile" to "","Work" to "","Other" to "")
        var addresscount = binding.addressLay.childCount
        if(addresscount > 0){
            for(i in 0..addresscount-1){
                var view = binding.addressLay.get(i)
                var type = view.findViewById<TextView>(R.id.addresslabel)?.text.toString()
                var useraddress = view.findViewById<EditText>(R.id.addressnew)?.text.toString()
                if(!type.isNullOrEmpty()){
                    address[type] = useraddress
                }
//                Toast.makeText(this@ContactEditActvity,"Address type $type  Number  $useraddress  ${address.size}" ,Toast.LENGTH_SHORT).show()
            }
        }
        return address
    }

    private fun saveContact() {
        if(!getName().isNullOrEmpty()){
            if(getALLPhones().second == true && getALLEmails().second == true){
                viewModal.editContact(Contact(uid,getName(),getALLPhones().first,getALLEmails().first,getALLAddress(),null))
                Toast.makeText(this@ContactEditActvity,"Saved" ,Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
            }

        }
    }

    private fun addAddressField() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(addressCount <=3){
            for((key,value) in addressTypes){
                if(value == false){
                    val rowView: View = inflater.inflate(R.layout.edit_address_child, null,true)
                    rowView.findViewById<TextView>(R.id.addresslabel).setText(key)
                    rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                    rowView.findViewById<EditText>(R.id.addressnew).setOnClickListener( View.OnClickListener { isClicked.value=true })
                    addressTypes[key] = true
                    addressCount++
                    binding.addressLay.addView(rowView,binding.addressLay.childCount)
                    rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
                        binding.addressLay.removeView((it.parent) as View)
                        addressTypes[it.tag.toString()] = false
                        addressCount--
                    })
                    break
                }
            }
        }
    }

    private fun addPhoneField() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(phoneCount <=3){
            for((key,value) in phoneTypes){
                if(value == false){
                    val rowView: View = inflater.inflate(R.layout.edit_phone_child, null,true)
                    rowView.findViewById<TextView>(R.id.phonelabel).setText(key)
                    rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                    rowView.findViewById<EditText>(R.id.phonenew).setOnClickListener( View.OnClickListener { isClicked.value=true })
                    phoneTypes[key] = true
                    phoneCount++
                    binding.phoneLay.addView(rowView,binding.phoneLay.childCount)
                    rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
                        binding.phoneLay.removeView((it.parent) as View)
                        phoneTypes[it.tag.toString()] = false
                        phoneCount--
                    })
                    break
                }
            }
        }
    }

    private fun addEmailField() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(emailCount <=3){
            for((key,value) in emailTypes){
                if(value == false){
                    val rowView: View = inflater.inflate(R.layout.edit_email_child, null,true)
                    rowView.findViewById<TextView>(R.id.emaillabel).setText(key)
                    rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                    rowView.findViewById<EditText>(R.id.emailnew).setOnClickListener( View.OnClickListener { isClicked.value=true })
                    emailTypes[key] = true
                    emailCount++
                    binding.emailLay.addView(rowView,binding.emailLay.childCount)
                    rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
                        binding.emailLay.removeView((it.parent) as View)
                        emailTypes[it.tag.toString()] = false
                        emailCount--
                    })
                    break
                }
            }
        }
    }

    private fun makeNumberField(data : Contact){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(!data.number.isNullOrEmpty()){
            for((key,value) in data.number!!){
                val rowView: View = inflater.inflate(R.layout.edit_phone_child, null,true)
                rowView.findViewById<TextView>(R.id.phonelabel).setText(key)
                rowView.findViewById<TextView>(R.id.phonenew).let {
                    it.text = value
                    it.setOnClickListener(View.OnClickListener {
                        isClicked.value = true
                    })
                }
                rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                phoneTypes[key] =true
                phoneCount++

                binding.phoneLay.addView(rowView,binding.phoneLay.childCount)
                rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
                    Toast.makeText(this@ContactEditActvity,"${it.tag}",Toast.LENGTH_SHORT).show()
                    binding.phoneLay.removeView((it.parent) as View)
                    phoneTypes[it.tag.toString()] = false
                    phoneCount--
                })
            }
        }
    }

    private fun makeEmailField(data : Contact){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(!data.email.isNullOrEmpty()){
            for((key,value) in data.email!!){
                val rowView: View = inflater.inflate(R.layout.edit_email_child, null,true)
                rowView.findViewById<TextView>(R.id.emaillabel).setText(key)
                rowView.findViewById<TextView>(R.id.emailnew).let {
                    it.text = value
                    it.setOnClickListener(View.OnClickListener {
                        isClicked.value = true
                    })
                }
                rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                emailTypes[key] =true
                emailCount++

                binding.emailLay.addView(rowView,binding.emailLay.childCount)
                rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
                    Toast.makeText(this@ContactEditActvity,"${it.tag}",Toast.LENGTH_SHORT).show()
                    binding.emailLay.removeView((it.parent) as View)
                    emailTypes[it.tag.toString()] = false
                    emailCount--
                })
            }
        }
    }

    private fun makeAddressField(data : Contact){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(!data.address.isNullOrEmpty()){
            for((key,value) in data.address!!){
                val rowView: View = inflater.inflate(R.layout.edit_address_child, null,true)
                rowView.findViewById<TextView>(R.id.addresslabel).setText(key)
                rowView.findViewById<TextView>(R.id.addressnew).let {
                    it.text = value
                    it.setOnClickListener(View.OnClickListener {
                        isClicked.value = true
                    })
                }
                rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                addressTypes[key] =true
                addressCount++

                binding.addressLay.addView(rowView,binding.addressLay.childCount)
                rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
                    Toast.makeText(this@ContactEditActvity,"${it.tag}",Toast.LENGTH_SHORT).show()
                    binding.addressLay.removeView((it.parent) as View)
                    addressTypes[it.tag.toString()] = false
                    addressCount--
                })
            }
        }
    }

    private fun fetchData(data : Contact){
        uid = data.id!!
        binding.nameField.setText(data.name)
        makeNumberField(data)
        makeEmailField(data)
        makeAddressField(data)
    }

}