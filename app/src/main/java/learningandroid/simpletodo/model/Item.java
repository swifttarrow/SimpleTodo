package learningandroid.simpletodo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swifttarrow on 9/29/2016.
 */
@Table(name = "Items")
public class Item extends Model {
    @Column(name = "description")
    public String description;

    public static List<Item> getItems() {
        /*Item item = new Item();
        item.description = "test";
        item.save();*/
        return new Select().from(Item.class).execute();
    }

    public Item() {
        super();
    }
}
