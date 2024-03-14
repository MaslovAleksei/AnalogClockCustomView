package com.margarin.analogclockcustomview

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.replaceFragment(fragment: Fragment) {
    supportFragmentManager.popBackStack()
    supportFragmentManager
        .beginTransaction()
        .replace(R.id.main_container, fragment)
        .commit()
}