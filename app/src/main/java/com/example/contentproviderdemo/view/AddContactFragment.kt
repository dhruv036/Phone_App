package com.example.contentproviderdemo.view

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar

import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contentproviderdemo.R
import com.example.contentproviderdemo.modal.db.database.ContactDatabase
import com.example.contentproviderdemo.util.Contact
import com.example.contentproviderdemo.modal.ContactRepository
import com.example.contentproviderdemo.util.ViewModelTypes
import com.example.contentproviderdemo.viewmodel.AddContactFragmentViewModel
import com.example.contentproviderdemo.viewmodel.MainViewModalFactory
import java.lang.Math.abs


class AddContactFragment : Fragment(){

    lateinit var removePhoneBt : ImageView
    lateinit var removeEmailBt : ImageView
    lateinit var removeAddressBt : ImageView
    lateinit var addNumberBt : ImageView
    lateinit var addEmailBt : ImageView
    lateinit var addAddressBt: ImageView
    lateinit var phoneContainer : LinearLayout
    lateinit var emailContainer: LinearLayout
    lateinit var addressContainer: LinearLayout
    lateinit var saveBt: TextView
    lateinit var name : EditText
    var emailCount=1
    var phoneCount=1
    var addressCount=1
    var phoneTypes = mutableMapOf<String,Boolean>("Home" to true,"Mobile" to false,"Work" to false,"Other" to false)
    var emailTypes = mutableMapOf<String,Boolean>("Home" to true,"Mobile" to false,"Work" to false,"Other" to false)
    var addressTypes = mutableMapOf<String,Boolean>("Home" to true,"Mobile" to false,"Work" to false,"Other" to false)
    var isClicked = MutableLiveData<Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view :View = inflater.inflate(R.layout.addcontactfragment,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        name = view.findViewById(R.id.nameField)
        saveBt = view.findViewById<TextView>(R.id.saveBt)
        removePhoneBt =view.findViewById<ImageView>(R.id.removephone)
        removeEmailBt =view.findViewById<ImageView>(R.id.removeemail)
        removeAddressBt =view.findViewById<ImageView>(R.id.removeaddress)
        addNumberBt =view.findViewById<ImageView>(R.id.addNumber)
        addEmailBt =view.findViewById<ImageView>(R.id.addEmail)
        addAddressBt =view.findViewById<ImageView>(R.id.addaddress)
        phoneContainer =view.findViewById<LinearLayout>(R.id.phoneLay)
        emailContainer =view.findViewById<LinearLayout>(R.id.emailLay)
        addressContainer =view.findViewById<LinearLayout>(R.id.addressLay)

        view.findViewById<ImageView>(R.id.backbt).setOnClickListener(View.OnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        })
        view.findViewById<EditText>(R.id.phonenew).setOnClickListener(View.OnClickListener {
            isClicked.value = true
        })
        view.findViewById<EditText>(R.id.emailnew).setOnClickListener(View.OnClickListener {
            isClicked.value = true
        })
        view.findViewById<EditText>(R.id.addressnew).setOnClickListener(View.OnClickListener {
            isClicked.value = true
        })
        view.findViewById<LinearLayout>(R.id.allFields).setOnClickListener(View.OnClickListener {
            isClicked.value = true
        })

        isClicked.observe(viewLifecycleOwner, Observer {
            if(it == true){
                view.findViewById<TextView>(R.id.saveBt).visibility =View.VISIBLE
            }
        })
        saveBt.setOnClickListener(View.OnClickListener {
            storeInDB()
        })

        removePhoneBt.setOnClickListener(View.OnClickListener {
           removeNumberField(it)
            isClicked.value= true
        })
        removeEmailBt.setOnClickListener(View.OnClickListener {
           removeEmailField(it)
            isClicked.value= true
        })
        removeAddressBt.setOnClickListener(View.OnClickListener {
           removeAddressField(it)
            isClicked.value= true
        })

        addNumberBt.setOnClickListener(View.OnClickListener {
            addNumberField()
            isClicked.value= true
        })

        addEmailBt.setOnClickListener(View.OnClickListener {
            addEmailField()
            isClicked.value= true
        })
        addAddressBt.setOnClickListener(View.OnClickListener {
            addAddressField()
            isClicked.value= true
        })
        name.setOnClickListener(View.OnClickListener {
            isClicked.value=true
        })
    }

    fun storeInDB(){
        lateinit var viewModal: AddContactFragmentViewModel
        val userDao = ContactDatabase.getInstance(requireContext()).contactDao()
        val repository = ContactRepository(userDao)
        viewModal =  ViewModelProvider(this, MainViewModalFactory(repository,requireContext(),ViewModelTypes.ADDCONTACTFRAGMENTVIEWMODEL)).get(
            AddContactFragmentViewModel::class.java)
        val random : Long = abs((0..999999999999).random())
        if(!getName().isNullOrEmpty()){
            if(getALLNumber().second == true && getALLEmails().second == true){
                viewModal.storeContactInDB(Contact(random,getName(),getALLNumber().first,getALLEmails().first,getALLAddress(),null))
                Toast.makeText(context,"Contact Save",Toast.LENGTH_SHORT).show()
                onDetach()
            }
        }else{
            Toast.makeText(context,"Enter your Name",Toast.LENGTH_SHORT).show()
        }
    }



    fun getName(): String? {
        if(!(name.text.toString().isNullOrEmpty())){
            return name.text.toString()
        }
        return null
    }

    fun getALLNumber() : Pair<MutableMap<String,String>,Boolean>{
        var phones = mutableMapOf<String,String>()
        var isNumberCorrect =true
        var numbercount = phoneContainer.childCount
        if(numbercount > 0){
            for(i in 0..numbercount-1){
                var view = phoneContainer.get(i)
                var type  = view.findViewById<TextView>(R.id.phonelabel)?.text.toString()
                var number  = view.findViewById<EditText>(R.id.phonenew)?.text.toString()
                if(!type.isNullOrEmpty() && !number.isNullOrEmpty()){
                    if(number.length == 10){
                        phones.set(type,number)
                        isNumberCorrect =true
                    }else{
                        Toast.makeText(context,"Incorrect Number" ,Toast.LENGTH_SHORT).show()
                        isNumberCorrect =false
                        break
                    }
                }
            }
        }
        return Pair(phones,isNumberCorrect)
    }

    fun getALLEmails(): Pair<MutableMap<String,String>,Boolean>{
        var emails = mutableMapOf<String,String>()
        var emailcount = emailContainer.childCount
        var isEmailCorrect= true
        if(emailcount > 0){
            for(i in 0..emailcount-1){
                var view = emailContainer.get(i)
                var type = view.findViewById<TextView>(R.id.emaillabel)?.text.toString()
                var email = view.findViewById<EditText>(R.id.emailnew)?.text.toString()
                if(!type.isNullOrEmpty() && !email.isNullOrEmpty()){
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        isEmailCorrect =true
                        emails.set(type,email)
                    }else{
                        isEmailCorrect =false
                        Toast.makeText(context,"Incorrect Email ",Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            }
        }
        return Pair(emails,isEmailCorrect)
    }

    fun getALLAddress(): MutableMap<String,String>{
        var address = mutableMapOf<String,String>()
        var addresscount = addressContainer.childCount
        if(addresscount > 0){
            for(i in 0..addresscount-1){
                var view = addressContainer.get(i)
                var type = view.findViewById<TextView>(R.id.addresslabel)?.text.toString()
                var useraddress = view.findViewById<EditText>(R.id.addressnew)?.text.toString()
                if(!type.isNullOrEmpty() && !useraddress.isNullOrEmpty()){
                    address.set(type,useraddress)
                }
//                Toast.makeText(context,"Address type $type  Number  $useraddress  ${address.size}" ,Toast.LENGTH_SHORT).show()
            }
        }
        return address
    }

    fun removeNumberField(it : View){
        phoneContainer.removeView((it.parent) as View)
        phoneTypes[it.tag.toString()] = false
        phoneCount--
//        Toast.makeText(context,"Remove clicked"+it.tag,Toast.LENGTH_SHORT).show()
    }

    fun removeEmailField(it :View){
        emailContainer.removeView((it.parent) as View)
        emailTypes[it.tag.toString()] = false
        emailCount--
//        Toast.makeText(context,"Remove clicked"+it.tag,Toast.LENGTH_SHORT).show()
    }

    fun removeAddressField(it:View){
        addressContainer.removeView((it.parent) as View)
        addressTypes[it.tag.toString()] = false
        addressCount--
//        Toast.makeText(context,"Remove clicked"+it.tag,Toast.LENGTH_SHORT).show()
    }

    fun addNumberField(){
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(phoneCount <= 3){
            for((key,value) in phoneTypes){
                if(value == false){
                    val rowView: View = inflater.inflate(R.layout.edit_phone_child, null,true)
                    rowView.findViewById<TextView>(R.id.phonelabel).setText(key)
                    rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                    phoneCount++
                    phoneTypes[key] = true
                    phoneContainer.addView(rowView,phoneContainer.childCount)
                    rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
//                        Toast.makeText(context,"Dynamic view removed"+it.tag,Toast.LENGTH_SHORT).show()
                        phoneCount--
                        phoneTypes[it.tag.toString()] = false
                        phoneContainer.removeView((it.parent) as View)
                    })
                    break
                }
            }
        }
    }

    fun addEmailField(){
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(emailCount <= 3){
            for((key,value) in emailTypes){
                if(value == false){
                    val rowView: View = inflater.inflate(R.layout.edit_email_child, null,true)
                    rowView.findViewById<TextView>(R.id.emaillabel).setText(key)
                    rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                    emailCount++
                    emailTypes[key] = true
                    emailContainer.addView(rowView,emailContainer.childCount)
                    rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
//                        Toast.makeText(context,"Dynamic view removed"+it.tag,Toast.LENGTH_SHORT).show()
                        emailCount--
                        emailTypes[it.tag.toString()] = false
                        emailContainer.removeView((it.parent) as View)
                    })
                    break
                }
            }
        }
    }

    fun addAddressField(){
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(addressCount <= 3){
            for((key,value) in addressTypes){
                if(value == false){
                    val rowView: View = inflater.inflate(R.layout.edit_address_child, null,true)
                    rowView.findViewById<TextView>(R.id.addresslabel).setText(key)
                    rowView.findViewById<ImageView>(R.id.imageminus).tag = key
                    addressCount++
                    addressTypes[key] = true
                    addressContainer.addView(rowView,addressContainer.childCount)
                    rowView.findViewById<ImageView>(R.id.imageminus).setOnClickListener(View.OnClickListener {
//                        Toast.makeText(context,"Dynamic view removed"+it.tag,Toast.LENGTH_SHORT).show()
                        addressCount--
                        addressTypes[it.tag.toString()] = false
                        addressContainer.removeView((it.parent) as View)
                    })
                    break
                }
            }
        }
    }
}