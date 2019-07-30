package np.com.nawarajbista.shoppinglist

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.widget.Toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import np.com.nawarajbista.shoppinglist.data.AppDatabase
import np.com.nawarajbista.shoppinglist.data.ShoppingList
import np.com.nawarajbista.shoppinglist.viewHolder.ShoppingListGroup
import java.lang.Exception
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var num: Int = 1
    private val adapter = GroupAdapter<ViewHolder>()
    private var db: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        num = editText_number.text.toString().toInt()

        db = AppDatabase.getAppDataBase(this)


        addListToAdapter()

        list_view.adapter = adapter
        initSwipe()


        button_add.setOnClickListener {
            val itemCount = editText_number.text.toString().toInt()
            val itemName = editText_name.text.toString()

            addDataToDatabase(itemCount, itemName)
            num = 1
        }



        button_minus.setOnClickListener {
            reduceNumber()
        }

        button_plus.setOnClickListener {
            addNumber()
        }
    }




    private fun reduceNumber() {
        if(num > 1) {
            num--
            editText_number.setText(num.toString())
        }
    }

    private fun addNumber() {
        if(num < 9999) {
            num++
            editText_number.setText(num.toString())
        }
    }

    private fun addDataToDatabase(item_count:Int, item_name: String) {
//        db = AppDatabase.getAppDataBase(this) ?: return
        if(db == null) return
        val shoppingList = ShoppingList(item_count, item_name)

        Thread {
            db?.shoppingDao()?.insertAll(shoppingList)

            // included this for future reference
            // this shows how we can use handler looper to communicate with main thread
            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                run {
                    Toast.makeText(this, "list added", Toast.LENGTH_SHORT).show()
                    editText_name.setText("")
                    editText_number.setText("1")

                }
            }

            handler.post(runnable)

        }.start()
    }

    private fun addListToAdapter() {
        if(db == null) {
            Toast.
                makeText(
                    this,
                    "could not connect to data base",
                    Toast.LENGTH_SHORT
                ).show()
        } else {

            val allData = db?.shoppingDao()?.getAll()

            allData?.observe(this, android.arch.lifecycle.Observer {
                adapter.clear()
                it?.forEach {list ->
                    adapter.add(ShoppingListGroup(list))
                }
            })
        }
    }


    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
                    //or ItemTouchHelper.RIGHT
        ) {

            val deleteIcon = ContextCompat.getDrawable(this@MainActivity, android.R.drawable.ic_menu_delete)
            val intrinsicWidth = deleteIcon?.intrinsicWidth
            val intrinsicHeight = deleteIcon?.intrinsicHeight
            val background = ColorDrawable()
            val backgroundColor = Color.parseColor("#f44336")
            val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

//                val nameOfItem = viewHolder.itemView.textView_name.text.toString()

                val itemId = viewHolder.itemView.id.toLong()

                if (direction == ItemTouchHelper.LEFT) {
                    if(db == null) {
                        Toast.
                            makeText(
                                this@MainActivity,
                                "could not connect to data base",
                                Toast.LENGTH_SHORT
                            ).show()
                    } else {


                        try {
                            thread {
                                db?.shoppingDao()?.deleteItem(itemId)
                            }.start()
                        }
                        catch (e: Exception) {
                            Log.d("message", "$itemId")
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                // Draw the red delete background
                background.color = backgroundColor
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                // Draw the delete icon
                deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                deleteIcon?.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
                c?.drawRect(left, top, right, bottom, clearPaint)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(list_view)
    }
}
