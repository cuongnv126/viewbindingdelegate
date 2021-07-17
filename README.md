# ViewBinding Delegate Extension
Support extension to use Android ViewBinding quickly and smooth transform from kotlin-synthetic-extension.
Binding instance auto wipe belong to Android UI view life cycle.

# Usage

## Initial binding instance
```kotlin
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
```

## Interacting
```kotlin
// Set content view and init binding
setContentView(inflate(::binding, layoutInflater, null))

// Instead of this, can using rootDelegate() to direct create view.
val rootView = rootDelegate()?.onCreateView(inflater, container)

// Same with using of normal ViewBinding
binding.txtHelloWorld.text = "It's work!"

mergeBinding.btnHitMe.setOnClickListener {
    Toast.makeText(this, "Clicked from merge binding", Toast.LENGTH_SHORT).show()
}

nonMergeBinding.btnTouchMe.setOnClickListener {
    Toast.makeText(this, "Clicked from non-merge binding", Toast.LENGTH_SHORT).show()
}
```
## ViewStub
```kotlin
// ViewStubBinding will lazy bind view.
Log.d(TAG, "Stub binding initialized? = ${stubBinding.opt() != null}") // null binding instance.
stubBinding.inflate {
    // Run once on view inflated.
    btnClickMe.setOnClickListener {
        Toast.makeText(this@MainActivity, "Clicked from stub binding", Toast.LENGTH_SHORT)
            .show()
    }
}
Log.d(TAG, "Stub binding initialized? = ${stubBinding.opt() != null}") // binding initialized.
```


# Dependency

## Maven
```xml
<dependency>
  <groupId>org.cuongnv</groupId>
  <artifactId>viewbindingdelegate</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

## Gradle Kotlin DSL
```kotlin
implementation("org.cuongnv:viewbindingdelegate:1.0.0")

```
## Gradle Groovy
```groovy
implementation 'org.cuongnv:viewbindingdelegate:1.0.0'
```
