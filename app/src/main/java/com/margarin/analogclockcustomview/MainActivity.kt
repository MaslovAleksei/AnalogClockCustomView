package com.margarin.analogclockcustomview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.margarin.analogclockcustomview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onNavigationItemSelectedListener()
        binding.bottomNavigationView.selectedItemId = R.id.bottom_menu_first
    }

    private fun onNavigationItemSelectedListener() {
        var currentPageId = -1
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (currentPageId == item.itemId) {
                return@setOnItemSelectedListener false
            } else {
                when (item.itemId) {
                    R.id.bottom_menu_first -> {
                        currentPageId = item.itemId
                        replaceFragment(FirstFragment())
                    }

                    R.id.bottom_menu_second -> {
                        currentPageId = item.itemId
                        replaceFragment(SecondFragment())
                    }
                }
                true
            }
        }
        binding.bottomNavigationView.menu.findItem(binding.bottomNavigationView.selectedItemId)
    }
}