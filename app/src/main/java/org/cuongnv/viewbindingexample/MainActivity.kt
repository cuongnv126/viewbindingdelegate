package org.cuongnv.viewbindingexample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.cuongnv.viewbindingdelegate.inflate
import org.cuongnv.viewbindingdelegate.select
import org.cuongnv.viewbindingdelegate.viewBinding
import org.cuongnv.viewbindingdelegate.viewBindingInclude
import org.cuongnv.viewbindingdelegate.viewBindingStub
import org.cuongnv.viewbindingexample.databinding.ActivityMainBinding
import org.cuongnv.viewbindingexample.databinding.LayoutIncludeActionMergeBinding
import org.cuongnv.viewbindingexample.databinding.LayoutStubActionBinding

class MainActivity : BaseActivity() {

    // Binding as root binding, support main view to setContentView in Activity
    private val binding by viewBinding(ActivityMainBinding::inflate)

    // Binding as merge tag and include in main binding
    private val mergeBinding by viewBindingInclude(
        LayoutIncludeActionMergeBinding::bind,
        ::binding
    )

    // Binding as include an other ViewGroup
    private val nonMergeBinding get() = binding.includeNonMerge

    // Lazy binding by using ViewStub
    private val stubBinding by viewBindingStub(
        LayoutStubActionBinding::bind,
        ::binding.select { stubAction }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view and init binding
        setContentView(inflate(::binding, layoutInflater, null))

        binding.txtHelloWorld.text = "It's work!"

        mergeBinding.btnHitMe.setOnClickListener {
            Toast.makeText(this, "Clicked from merge binding", Toast.LENGTH_SHORT).show()
        }

        nonMergeBinding.btnTouchMe.setOnClickListener {
            Toast.makeText(this, "Clicked from non-merge binding", Toast.LENGTH_SHORT).show()
        }

        Log.d(TAG, "Stub binding initialized? = ${stubBinding.opt() != null}")
        stubBinding.inflate {
            btnClickMe.setOnClickListener {
                Toast.makeText(this@MainActivity, "Clicked from stub binding", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        Log.d(TAG, "Stub binding initialized? = ${stubBinding.opt() != null}")
    }

    companion object {
        const val TAG = "MainActivity"
    }
}