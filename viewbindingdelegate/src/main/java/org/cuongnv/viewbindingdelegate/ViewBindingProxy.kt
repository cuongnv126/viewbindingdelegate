package org.cuongnv.viewbindingdelegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import org.cuongnv.viewbindingdelegate.HideApi
import kotlin.reflect.KProperty0

/**
 * Created by cuongnv on Jun 28, 2021
 *
 * Support declaration ViewBinding for any UI class implements this interface.
 *
 * Current version only support for simple use case:
 * - With each [ViewBinding] implementation, ONLY accept ONE [ViewBindingRootDelegate] in [ViewBindingProxy].
 * - Support child view binding for include tag.
 * - Support stub view binding for [ViewStub]
 *
 *
 * ----------------------------- EXPERIMENTAL VERSION -----------------------------
 *
 * For kotlin reflect version, we can using below function to get who is ViewBindingDelegate for
 * current binding property.
 * ```
 *
 * inline fun <reified R> KProperty0<*>.delegate(): R? {
 *     return try {
 *         isAccessible = true
 *         getDelegate() as? R
 *     } catch (ex: Throwable) {
 *         ex.printStackTrace()
 *         null
 *     }
 * }
 *
 *```
 * Don't forget import:
 *     implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin_version}")
 * to your gradle file.
 *
 * NOTE: If using that, you make sure do not obfuscate binding field, if-not function delegate()
 * will always return null.
 * --------------------------------------------------------------------------------
 *
 */

interface ViewBindingProxy : LifecycleOwner {
    @HideApi
    val bindingDelegates: MutableList<ViewBindingDelegate<*>>

    fun getViewLifecycleOwner(): LifecycleOwner?
}

/**
 * Find first root delegate for main viewBinding, it's will use to set default contentView
 */
fun ViewBindingProxy.rootDelegate(): ViewBindingRootDelegate<*>? {
    return bindingDelegates.firstOrNull { it is ViewBindingRootDelegate } as? ViewBindingRootDelegate<*>
}

/**
 * Add all delegate declared.
 */
fun ViewBindingProxy.addBindingDelegate(bindingDelegate: ViewBindingDelegate<*>) {
    bindingDelegates.add(bindingDelegate)
}

/**
 * Find all delegate by type [T]
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewBinding> ViewBindingProxy.bindingDelegatesOf(): List<ViewBindingDelegate<T>> {
    return bindingDelegates.filter { it.isBindingDelegateOf<T>() } as List<ViewBindingDelegate<T>>
}

/**
 * Check if exist any [ViewBindingDelegate] with [ViewBinding] type [T]
 */
inline fun <reified T : ViewBinding> ViewBindingProxy.anyBindingDelegate(): Boolean {
    return bindingDelegates.any { it.isBindingDelegateOf<T>() }
}

/**
 * Search [ViewBindingDelegate] for [ViewBinding] type [T]
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewBinding> ViewBindingProxy.bindingDelegateOf(): ViewBindingDelegate<T>? {
    return bindingDelegates.firstOrNull { it.isBindingDelegateOf<T>() } as? ViewBindingDelegate<T>
}

/**
 * Search [ViewBindingDelegate] for [ViewBinding] type [T]
 */
inline fun <reified T : ViewBinding> ViewBindingProxy.bindingDelegateOf(binding: KProperty0<T>): ViewBindingDelegate<T>? {
    return bindingDelegateOf()
}

/**
 * Create delegate [ViewBinding] for root layout, it's mean layout to use for setContentView to
 * your screen ([androidx.fragment.app.Fragment], [androidx.appcompat.app.AppCompatActivity]...)
 *
 * It's must be have one instance for type [T]
 */
inline fun <reified T : ViewBinding> ViewBindingProxy.viewBinding(
    noinline inflate: (LayoutInflater, ViewGroup?, Boolean) -> T,
): ViewBindingRootDelegate<T> {
    if (anyBindingDelegate<T>()) {
        throw IllegalStateException("Only accept one view binding root for ${T::class.java}")
    }

    return ViewBindingRootDelegate(this, T::class, inflate).also { addBindingDelegate(it) }
}

/**
 * Create [ViewBinding] with type [T] for <include /> tag in your xml layout.
 */
inline fun <reified R : ViewBinding, reified T : ViewBinding> ViewBindingProxy.viewBindingInclude(
    noinline bind: (View) -> T,
    viewBindingParentDelegate: ViewBindingDelegate<R>,
): ViewBindingIncludeDelegate<R, T> {

    return ViewBindingIncludeDelegate(
        this,
        T::class,
        viewBindingParentDelegate,
        bind
    ).also { addBindingDelegate(it) }
}

/**
 * Same with [viewBindingInclude]
 * Create [ViewBinding] with type [T] for <include /> tag in your xml layout.
 *
 * @param parentBindingProp: please ensure have only one definition with type [R] in your
 * [ViewBindingProxy], if not, parent will auto use by first delegate with type [R]
 *
 * @see [bindingDelegateOf]
 */
inline fun <reified R : ViewBinding, reified T : ViewBinding> ViewBindingProxy.viewBindingInclude(
    noinline bind: (View) -> T,
    parentBindingProp: KProperty0<R>,
): ViewBindingIncludeDelegate<R, T> {
    val parentBindingDelegate = bindingDelegateOf(parentBindingProp)
        ?: throw IllegalStateException("Not exist parent binding!")

    return viewBindingInclude(bind, parentBindingDelegate)
}

class KProperty0WithSelector<T>(
    val property: KProperty0<T>,
    val selector: T.() -> ViewStub,
)

inline fun <reified T> KProperty0<T>.select(
    noinline selector: T.() -> ViewStub,
): KProperty0WithSelector<T> {
    return KProperty0WithSelector(this, selector)
}

/**
 * Lazy binding view to [ViewBinding] by [ViewStub]
 */
inline fun <reified R : ViewBinding, reified T : ViewBinding> ViewBindingProxy.viewBindingStub(
    noinline bind: (View) -> T,
    viewBindingParentDelegate: ViewBindingDelegate<R>,
    noinline selector: R.() -> ViewStub,
): ViewBindingStubDelegate<R, T> {
    return ViewBindingStubDelegate(
        this,
        StubViewBinding.cls(),
        viewBindingParentDelegate,
        bind,
        selector
    ).also { addBindingDelegate(it) }
}

/**
 * Same with [viewBindingStub]
 * Lazy binding view to [ViewBinding] by [ViewStub]
 *
 * @param parentBindingSelectorProp: please ensure [KProperty0WithSelector.property] have only one
 * definition with type [R] in your [ViewBindingProxy], if not, parent will auto use by first delegate
 * with type [R]
 *
 * @see [bindingDelegateOf]
 */
inline fun <reified R : ViewBinding, reified T : ViewBinding> ViewBindingProxy.viewBindingStub(
    noinline bind: (View) -> T,
    parentBindingSelectorProp: KProperty0WithSelector<R>,
): ViewBindingStubDelegate<R, T> {
    val parentBindingDelegate = bindingDelegateOf(parentBindingSelectorProp.property)
        ?: throw IllegalStateException("Not exist parent binding!")

    return viewBindingStub(bind, parentBindingDelegate, parentBindingSelectorProp.selector)
}

/**
 * Only using with [ViewBindingRootDelegate], so we ensure only have one [ViewBindingRootDelegate] for class [T]
 *
 * @param rootBindingProp: [ViewBinding] delegated from [viewBinding] with [ViewBindingRootDelegate] instance.
 */
inline fun <reified T : ViewBinding> ViewBindingProxy.inflate(
    rootBindingProp: KProperty0<T>,
    inflater: LayoutInflater,
    container: ViewGroup?,
): View {
    val delegate = bindingDelegateOf(rootBindingProp) as? ViewBindingRootDelegate<T>
        ?: throw IllegalStateException("Binding must be a root view binding")

    return delegate.onCreateView(inflater, container)
}