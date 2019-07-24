package np.com.nawarajbista.shoppinglist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import np.com.nawarajbista.shoppinglist.data.AppDatabase
import np.com.nawarajbista.shoppinglist.data.ShoppingList
import np.com.nawarajbista.shoppinglist.viewHolder.ShoppingListGroup

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
}
