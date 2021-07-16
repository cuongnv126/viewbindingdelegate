package org.cuongnv.viewbindingdelegate

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.viewbinding.ViewBinding
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Created by cuongnv on Jun 28, 2021
 *
 * Supply [ViewBinding] instance by delegate through [ViewBindingDelegate.getValue]
 * Using lifecycle aware, binding [T] will auto wipe after view destroyed.
 */

open class ViewBindingDelegate<T : ViewBinding>(
    provider: ViewBindingProxy,
    val bindingType: KClass<out T>,
) : ReadOnlyProperty<Any?, T> {

    protected var binding: T? = null

    private val refProvider = WeakReference(provider)
    private val provider get() = refProvider.get()

    private val lifeCycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> addViewLifeCycleObserver()
            Lifecycle.Event.ON_DESTROY -> removeLifeCycleObserver()
            else -> {
                // do nothing
            }
        }
    }

    private val viewLifeCycleEventObserver by lazy {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                binding = null
                removeViewLifeCycleObserver()
            }
        }
    }

    init {
        addLifeCycleObserver()
    }

    private fun addLifeCycleObserver() {
        provider?.lifecycle?.addObserver(lifeCycleEventObserver)
    }

    private fun removeLifeCycleObserver() {
        provider?.lifecycle?.removeObserver(lifeCycleEventObserver)
    }

    private fun addViewLifeCycleObserver() {
        provider?.getViewLifecycleOwner()?.lifecycle?.addObserver(viewLifeCycleEventObserver)
    }

    private fun removeViewLifeCycleObserver() {
        provider?.getViewLifecycleOwner()?.lifecycle?.removeObserver(viewLifeCycleEventObserver)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = binding!!

    fun opt() = binding

    inline fun <reified R> isBindingDelegateOf() = R::class == bindingType

    override fun toString() = "Delegate <${this::class}> with binding <$bindingType>"
}