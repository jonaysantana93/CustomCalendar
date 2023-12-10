package com.jonay.customcalendar.common.utils.viewBinding

import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val viewBinder: (View) -> T,
) : ReadOnlyProperty<Fragment, T> {
    private var fragmentBinding: T? = null

    init {
        initializeFragmentLifecycleObserver()
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {

        ensureMainThread()

        val binding = fragmentBinding
        if (binding != null && thisRef.view === binding.root) {
            return binding
        }

        ensureInitializedState()

        return viewBinder(thisRef.requireView()).also { fragmentBinding = it }
    }

    private fun ensureInitializedState() {
        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Fragment views are destroyed.")
        }
    }

    private fun ViewBinding?.disposeRecyclers() {
        val viewGroup = this?.root ?: return
        if (viewGroup is ViewGroup) {
            viewGroup.children.filterIsInstance<RecyclerView>().forEach {
                it.adapter = null
            }
        }
    }

    private fun ensureMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("Views can only be accessed on the main thread.")
        }
    }

    private fun initializeFragmentLifecycleObserver() {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            fragmentBinding.disposeRecyclers()
                            fragmentBinding = null
                        }
                    })
                }
            }
        })
    }
}