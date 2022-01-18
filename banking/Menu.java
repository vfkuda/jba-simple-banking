package banking;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    List<Menu> items = new ArrayList<>();

    public Menu addItem(int itemId, String brief, Callable action) {
//        MenuItem mi = new MenuItem(itemId, brief, action, this, null)
//        items.add(mi)
//        return this.
        return null;
    }

    interface Callable {
        public void call();
    }

    class MenuItem {
        int itemId;
        String brief;
        Callable action;
        Menu parentMenu;
        Menu subMenu;

        public MenuItem(int itemId, String brief, Callable action, Menu parentMenu, Menu subMenu) {
            this.itemId = itemId;
            this.brief = brief;
            this.action = action;
            this.parentMenu = parentMenu;
            this.subMenu = subMenu;
        }
    }
}
