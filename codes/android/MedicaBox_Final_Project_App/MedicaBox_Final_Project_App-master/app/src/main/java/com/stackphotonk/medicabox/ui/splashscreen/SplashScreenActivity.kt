package com.stackphotonk.medicabox.ui.splashscreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stackphotonk.medicabox.R
import com.stackphotonk.medicabox.databinding.ActivitySplashScreenBinding
import com.stackphotonk.medicabox.ui.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.subTitle.translationX = -1500F

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.subTitle.animate().translationX(0F)
                .setDuration(
                    resources.getInteger(android.R.integer.config_longAnimTime).toLong()
                )
            Handler(Looper.getMainLooper()).postDelayed({
                binding.logo.animate().translationX(1500F)
                    .setDuration(
                        resources.getInteger(android.R.integer.config_longAnimTime).toLong()
                    )

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.main.gravity = Gravity.TOP
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, 300)
                }, 500)
            }, 1500)
        }, 500)
    }
}