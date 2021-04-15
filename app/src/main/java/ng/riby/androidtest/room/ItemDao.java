package ng.riby.androidtest.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ItemDao {
    @Insert
    public void insert(Item... items);

    @Query("SELECT * FROM items ORDER BY id DESC LIMIT 1")
    public Item getItems();
}
