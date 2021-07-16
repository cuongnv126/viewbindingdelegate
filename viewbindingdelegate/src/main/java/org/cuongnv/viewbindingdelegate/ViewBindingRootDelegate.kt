package org.cuongnv.viewbindingdelegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * Created by cuongnv on Jun 28, 2021
 */

class ViewBindingRootDelegate<T : ViewBinding>(
    provider: ViewBindingProxy,
    bindingType: KClass<out T>,
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T,
) : ViewBindingDelegate<T>(provider, bindingType) {

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View {
        if (binding == null || binding!!.root.parent != null) {
            binding = inflate(inflater, container, false)
        }
        return binding!!.root
    }
}