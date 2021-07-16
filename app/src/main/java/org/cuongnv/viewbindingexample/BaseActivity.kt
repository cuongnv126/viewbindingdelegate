package org.cuongnv.viewbindingexample

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import org.cuongnv.viewbindingdelegate.ViewBindingDelegate
import org.cuongnv.viewbindingdelegate.ViewBindingProxy

/**
 * Created by cuongnv on Jul 16, 2021
 */

open class BaseActivity : AppCompatActivity(), ViewBindingProxy {
    override val bindingDelegates: MutableList<ViewBindingDelegate<*>> = mutableListOf()
    override fun getViewLifecycleOwner(): LifecycleOwner = this
}