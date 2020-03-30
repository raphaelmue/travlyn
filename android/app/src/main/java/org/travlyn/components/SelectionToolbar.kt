package org.travlyn.components

import android.content.Context
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.get
import com.miguelcatalan.materialsearchview.utils.AnimationUtil
import org.travlyn.R

/**
 * This toolbar can be used when it comes to select multiple elements from a RecyclerView, ListView
 * etc. This toolbar stores the selected elements and returns them, when the user finishes the
 * action.
 *
 * This Toolbar can be initialized by the following:
 *
 * ```
 * val selectionToolbar: SelectionToolbar<Object> = findViewById(R.id.selectionToolbar)
 * ```
 *
 * @param K Type from which the selected elements are derived
 */
class SelectionToolbar<K> : Toolbar {

    private var isVisible = false

    private lateinit var onCloseListener: () -> Unit
    private lateinit var onCheckListener: (selectedElements: MutableList<K>) -> Unit

    private val selectedElements: MutableList<K> = mutableListOf()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)


    init {
        visibility = View.GONE
        background = context.getDrawable(R.color.white)
        setTitleTextColor(context.getColor(R.color.black))

        navigationIcon = context.getDrawable(R.drawable.ic_arrow_back_black_24dp)
        setNavigationOnClickListener {
            hideToolbar()
            this.onCloseListener()
        }

        menu.add(Menu.NONE, R.drawable.ic_check, Menu.NONE, context.getString(R.string.ok))
        menu[0].setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        val drawable = DrawableCompat.wrap(context.getDrawable(R.drawable.ic_check));
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.black));
        menu[0].icon = drawable
        menu[0].setOnMenuItemClickListener {
            hideToolbar()
            this.onCheckListener(selectedElements)
            return@setOnMenuItemClickListener true
        }

        updateTitle()
    }

    /**
     * Sets the on close listener. This listener is called when the user closes or dismisses the
     * toolbar and the selection operation.
     *
     * @param onCloseListener listener to listen to close event
     */
    public fun setOnCloseListener(@Nullable onCloseListener: () -> Unit) {
        this.onCloseListener = onCloseListener
    }

    /**
     * Sets the on check listener. This listener is called when the user finishes the operation by
     * clicking the check button.
     *
     * @param onCheckListener listener to list to check event
     */
    public fun setCheckListener(@Nullable onCheckListener: (selectedElements: MutableList<K>) -> Unit) {
        this.onCheckListener = onCheckListener
    }

    /**
     * Add a selected item to the internal storage of this toolbar. This can be called when an
     * element is selected by the user.
     *
     * @param element element to add to internal list of selected elements
     */
    public fun addSelectedElement(element: K) {
        this.selectedElements.add(element)
        updateTitle()
    }

    /**
     * Removes a selected element from the internal storage of this toolbar. This can be called when
     * an element is unselected by the user.
     *
     * @param element element to remove from internal list of selected elements
     */
    public fun removeSelectedElement(element: K) {
        this.selectedElements.remove(element)
        updateTitle()
    }

    private fun updateTitle() {
        this.title =
            context.getString(R.string.number_of_selected_items, this.selectedElements.size)
    }

    /**
     * Shows the toolbar. This can be called when the user wants to select multiple elememts from a
     * RecyclerView, ListView etc.
     */
    public fun showToolbar() {
        if (!this.isVisible) {
            this.isVisible = !this.isVisible
            setVisibleWithAnimation()
        }
    }

    private fun setVisibleWithAnimation() {
        val animationListener: AnimationUtil.AnimationListener =
            object : AnimationUtil.AnimationListener {
                override fun onAnimationStart(view: View): Boolean {
                    return false
                }

                override fun onAnimationEnd(view: View): Boolean {
                    return false
                }

                override fun onAnimationCancel(view: View): Boolean {
                    return false
                }
            }
        this.visibility = View.VISIBLE
        AnimationUtil.reveal(this, animationListener)
    }

    /**
     * Hides the toolbar.
     */
    public fun hideToolbar() {
        if (this.isVisible) {
            this.isVisible = !this.isVisible
            this.visibility = View.GONE
            this.selectedElements.clear()
        }
    }

    /**
     * Toggles the toolbar. This calls [showToolbar]  and [hideToolbar].
     */
    public fun toggleToolbar() {
        if (this.isVisible) {
            hideToolbar()
        } else {
            showToolbar()
        }
    }
}