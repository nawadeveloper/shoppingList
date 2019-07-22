package np.com.nawarajbista.shoppinglist.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "items")
data class ShoppingList(
    @ColumnInfo(name = "quantities") val quantities: Int,
    @ColumnInfo(name = "item_name") val itemName: String
) {
    @PrimaryKey(autoGenerate = true) var itemId: Long = 0
}