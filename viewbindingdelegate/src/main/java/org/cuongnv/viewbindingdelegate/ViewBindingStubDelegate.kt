package org.cuongnv.viewbindingdelegate

import android.view.View
import android.view.ViewStub
import androidx.viewbinding.ViewBinding
import org.cuongnv.viewbindingdelegate.HideApi
import kotlin.reflect.KClass

/**
 * Created by cuongnv on Jun 28, 2021
 */

class ViewBindingStubDelegate<R : ViewBinding, T : ViewBinding>(
    provider: ViewBindingProxy,
    bindingType: KClass<out StubViewBinding<T>>,
    parentBindingDelegate: ViewBindingDelegate<R>,
    private val bind: (View) -> T,
    private val selector: R.() -> ViewStub,
) : ViewBindingChildDelegate<R, StubViewBinding<T>>(provider, bindingType, parentBindingDelegate) {

    override fun createBinding(): StubViewBinding<T> {
        val parentBinding = parentBindingDelegate
            ?.opt()
            ?: throw IllegalStateException("parentBinding must be alive")

        return StubViewBinding(selector.invoke(parentBinding), bind)
    }
}

class StubViewBinding<T : ViewBinding>(
    @HideApi val viewStub: ViewStub?,
    @HideApi val bind: ((View) -> T)?,
) : ViewBinding {
    companion object {
        inline fun <reified T : ViewBinding> cls(): KClass<out StubViewBinding<T>> =
            StubViewBinding<T>(null, null)::class
    }

    @HideApi
    var binding: T? = null

    override fun getRoot(): View = get().root

    fun get(): T = binding!!

    fun opt() = binding

    inline fun inflate(initializer: T.() -> Unit) {
        if (binding != null) return
        binding = bind!!.invoke(viewStub!!.inflate())
        initializer(get())
    }

    inline fun use(block: T.() -> Unit) = opt()?.let(block)
}