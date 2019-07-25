package np.com.nawarajbista.shoppinglist.viewHolder

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.shopping_row.view.*
import np.com.nawarajbista.shoppinglist.R
import np.com.nawarajbista.shoppinglist.data.ShoppingList

class ShoppingListGroup(private val sp: ShoppingList): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.textView_quantities.text = sp.quantities.toString()
        viewHolder.itemView.textView_name.text = sp.itemName
        viewHolder.itemView.id = sp.itemId.toInt()

    }

    override fun getLayout(): Int {
        return R.layout.shopping_row
    }
}