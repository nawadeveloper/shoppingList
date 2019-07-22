package np.com.nawarajbista.shoppinglist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import np.com.nawarajbista.shoppinglist.data.AppDatabase
import np.com.nawarajbista.shoppinglist.data.ShoppingList

class MainActivity : AppCompatActivity() {

    var num: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        num = editText_number.text.toString().toInt()


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

        val db = AppDatabase.getAppDataBase(this) ?: return
        val shoppingList = ShoppingList(item_count, item_name)

        Thread {
            db.shoppingDao().insertAll(shoppingList)

            // included this for future reference
            // this shows how we can use handler looper to communicate with main thread
            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                run {
                    Toast.makeText(this, "list added", Toast.LENGTH_SHORT).show()
                    editText_name.setText(db.shoppingDao().getAll().toString())
                    editText_number.setText("1")
                }
            }

            handler.post(runnable)

        }.start()



        AppDatabase.destroyInstance()
    }
}
