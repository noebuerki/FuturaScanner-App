package ch.noebuerki.futurascanner.database.server;

import java.util.List;

import ch.noebuerki.futurascanner.database.local.dal.BlockDao;
import ch.noebuerki.futurascanner.database.local.dal.ItemDao;
import ch.noebuerki.futurascanner.database.local.objects.Block;
import ch.noebuerki.futurascanner.database.local.objects.Item;
import ch.noebuerki.futurascanner.database.local.objects.Settings;

public class StringConverter {

    public String generateString(BlockDao blockDao, ItemDao itemDao, Settings settings) {
        int branch = settings.getBranch();
        String date = settings.getDate();
        String output = "";

        List<Integer> blockIds = blockDao.getIds();

        for (Integer id : blockIds) {
            Block block = blockDao.getById(id);
            List<Item> itemList = itemDao.getByBlockId(id);
            if (itemList != null && !itemList.isEmpty()) {
                int pos = 1;
                for (Item item : itemList) {
                    item.setPosition(pos);
                    output += item.getBarcode() + ";" + branch + ";" + date + ";" + block.getNumber() + ";" + item.getPosition() + "\n";
                    pos++;
                }
            }
        }
        return output;
    }
}
