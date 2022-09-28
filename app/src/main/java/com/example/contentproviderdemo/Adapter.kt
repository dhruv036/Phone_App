package com.example.contentproviderdemo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class Adapter(var list: MutableList<Contact>, var context : Context) : RecyclerView.Adapter<Adapter.ContactViewHolder>() {

    var mylist = list



    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var name = itemView.findViewById<TextView>( R.id.name)
        var main = itemView.findViewById<CardView>(R.id.childMain)
        var image = itemView.findViewById<ImageView>(R.id.image)
        var text = itemView.findViewById<TextView>(R.id.head)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.child_layout,parent,false)
        return ContactViewHolder(v)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {

//        mylist.get(position).address!!.let {
//            holder.address.visibility  = View.VISIBLE
//            for (x in it){
//                holder.address.append("Address: "+x.value)
//            }
//        }
        var current = mylist.get(position)
        current.name!!.let {
            holder.name.visibility  = View.VISIBLE
            var index = it.indexOf(" ")
            if(index > 0){
                holder.name.setText(it.subSequence(0,index))
            }else{
                holder.name.setText(it)
            }
        }

        holder.main.setOnClickListener(View.OnClickListener {
            var bundle =Bundle()
            bundle?.putParcelable("contact",current)
            var i = Intent(context.applicationContext,ShowDetailActivity::class.java)
            i.putExtra("user",bundle)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        })

        if(!current.image.equals("") && current.image != null) {
            holder.image.visibility = View.VISIBLE
            var image :Bitmap? = null
            image = BitmapFactory.decodeFile(mylist.get(position).image)
            if(image != null){
                holder.image.setImageBitmap(image)
                holder.text.visibility = View.GONE
            }else{
                holder.image.setImageDrawable(context.getDrawable(R.drawable.round))
                holder.text.setText(current.name!!.subSequence(0,1).toString().uppercase())
                holder.text.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}