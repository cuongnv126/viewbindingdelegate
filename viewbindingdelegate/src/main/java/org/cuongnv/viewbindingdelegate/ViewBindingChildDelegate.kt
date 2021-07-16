package org.cuongnv.viewbindingdelegate

import androidx.viewbinding.ViewBinding
import java.lang.ref.WeakReference
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Created by cuongnv on Jun 28, 2021
 */

abstract class ViewBindingChildDelegate<R : ViewBinding, T : ViewBinding>(
    provider: ViewBindingProxy,
    bindingType: KClass<out T>,
    parentBindingDelegate: ViewBindingDelegate<R>,
) : ViewBindingDelegate<T>(provider, bindingType) {

    private val refParentBindingDelegate = WeakReference(parentBindingDelegate)
    protected val parentBindingDelegate get() = refParentBindingDelegate.get()

    protected abstract fun createBinding(): T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (binding == null) binding = createBinding()

        return super.getValue(thisRef, property)
    }
}