/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.example.inventory.provider.MyProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        var ITEM_ID = "id"
        var ITEM_COUNTRY = "Country"
        var ITEM_CAPITAL = "Capital"
    }

    private lateinit var navController: NavController
    lateinit var  fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val Button1: Button = findViewById(R.id.fav_button2)
        createnotification()
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
//        Button1.setOnClickListener {
//            sendNotification()
//        }

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(this, navController)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
//            R.id.nav_ring->Toast.makeText(this,"Ring",Toast.LENGTH_SHORT).show()
//            R.id.nav_locate->Toast.makeText(this,"Locate",Toast.LENGTH_SHORT).show()
            R.id.nav_ring->sendNotification()
            R.id.nav_locate->checkLocationPremission()
        }
        return super.onOptionsItemSelected(item)
    }
    private  val channel_id ="channel_id_01"
    private  val notificationid =101



    private fun createnotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name ="Notification Title"
            val descript ="Notification Desp"
            val importance : Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channel_id, name, importance).apply {
                description = descript
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){

        val intent = Intent(this,MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent : PendingIntent = PendingIntent.getActivity(this,0,intent,0)


        val builder =NotificationCompat.Builder(this, channel_id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("You get a notification!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("This is the final project of 2022 mobile application dev class, click to go back"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationid,builder.build())
        }
    }

    private fun checkLocationPremission() {
        val task =fusedLocationProviderClient.lastLocation
        if(ActivityCompat.checkSelfPermission(this ,android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED&& ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }
        task.addOnSuccessListener {
            if(it!=null){
                Toast.makeText(applicationContext,"${it.latitude} ${it.longitude} ", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /**
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
