package org.cuongnv.viewbindingdelegate

import android.view.View
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * Created by cuongnv on Jun 28, 2021
 */

class ViewBindingIncludeDelegate<R : ViewBinding, T : ViewBinding>(
    provider: ViewBindingProxy,
    bindingType: KClass<out T>,
    parentBindingDelegate: ViewBindingDelegate<R>,
    private val bind: (View) -> T,
) : ViewBindingChildDelegate<R, T>(provider, bindingType, parentBindingDelegate) {

    override fun createBinding(): T {
        val parentBinding = parentBindingDelegate
                ?.opt()
                ?: throw IllegalStateException("parentBinding must be alive")

        return bind(parentBinding.root)
    }
}