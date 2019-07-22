package np.com.nawarajbista.shoppinglist.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM items")
    fun getAll(): LiveData<List<ShoppingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg list: ShoppingList)

    @Query("DELETE FROM items WHERE itemId = :itemId")
    fun deleteItem(itemId: Long)

    @Delete
    fun delete(model: ShoppingList)
}