package com.example.networktest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.networktest.model.remote.Network

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         Thread(Runnable {
            Network().executeNetworkCall(
                "pride prejudice",
                5,
                "books"
            )
        }).start()
    }
}

