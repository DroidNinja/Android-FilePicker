package droidninja.filepicker.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity

import droidninja.filepicker.R
import droidninja.filepicker.fragments.BaseFragment


object FragmentUtil {

    fun hadFragment(activity: AppCompatActivity): Boolean {
        return activity.supportFragmentManager.backStackEntryCount != 0
    }

    fun replaceFragment(activity: AppCompatActivity, contentId: Int, fragment: BaseFragment) {
        val transaction = activity.supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)

        transaction.replace(contentId, fragment, fragment.javaClass.simpleName)

        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun addFragment(activity: AppCompatActivity, contentId: Int, fragment: BaseFragment) {
        val transaction = activity.supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)

        transaction.add(contentId, fragment, fragment.javaClass.simpleName)

        transaction.commit()
    }

    fun removeFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
    }


    fun showFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.supportFragmentManager.beginTransaction()
                .show(fragment)
                .commit()
    }

    fun hideFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.supportFragmentManager.beginTransaction()
                .hide(fragment)
                .commit()
    }

    fun attachFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.supportFragmentManager.beginTransaction()
                .attach(fragment)
                .commit()
    }

    fun detachFragment(activity: AppCompatActivity, fragment: BaseFragment) {
        activity.supportFragmentManager.beginTransaction()
                .detach(fragment)
                .commit()
    }

    fun getFragmentByTag(appCompatActivity: AppCompatActivity, tag: String): Fragment? {
        return appCompatActivity.supportFragmentManager.findFragmentByTag(tag)
    }

}