package game;

import model.characters.Character;
import model.items.Item;
import model.items.Weapon;
import model.items.Armor;
import model.items.Potion;
import model.exceptions.InventoryFullException;
import model.exceptions.ItemNotFoundException;
import model.exceptions.InsufficientGoldException;
import model.exceptions.InvalidActionException; // הוספתי אימפורט חסר

import java.util.ArrayList;
import java.util.HashMap;

/**
 * מחלקה המייצגת חנות במשחק.
 * מאפשרת קנייה ומכירה של פריטים.
 */
public class Shop {

    private String name;
    private ArrayList<Item> inventory;
    private HashMap<String, Integer> stock; // מיפוי שם פריט לכמות במלאי

    public Shop(String name) {
        this.name = name;
        this.inventory = new ArrayList<>();
        this.stock = new HashMap<>();
    }

    // ============================================================
    // ניהול מלאי החנות
    // ============================================================

    /**
     * מוסיף פריט לחנות עם כמות מסוימת.
     * * @param item הפריט להוספה
     * @param quantity הכמות
     */
    public void addItemToShop(Item item, int quantity) {
        // אם הפריט לא קיים ברשימה, נוסיף אותו
        if (!inventory.contains(item)) {
            inventory.add(item);
        }

        // עדכון המלאי צריך לקרות בכל מקרה (גם אם הפריט כבר היה ברשימה)
        int currentStock = stock.getOrDefault(item.getName(), 0);
        stock.put(item.getName(), currentStock + quantity);
    }

    /**
     * מימוש getAvailableItems
     * מחזיר רשימה של כל הפריטים הזמינים (שיש מהם במלאי > 0).
     * * @return רשימת פריטים זמינים
     */
    public ArrayList<Item> getAvailableItems() {
        ArrayList<Item> available = new ArrayList<>();
        for (Item item : inventory) {
            if (getItemStock(item.getName()) > 0) {
                available.add(item);
            }
        }
        return available;
    }

    /**
     * מימוש getItemsByCategory
     * מחזיר פריטים לפי קטגוריה (Weapon, Armor, Potion).
     * השתמש ב-instanceof.
     * * @param category שם הקטגוריה ("weapon", "armor", "potion")
     * @return רשימת פריטים מהקטגוריה
     */
    public ArrayList<Item> getItemsByCategory(String category) {
        ArrayList<Item> result = new ArrayList<>();
        category = category.toLowerCase(); // כדי למנוע בעיות של אותיות גדולות/קטנות

        for (Item item : inventory) {
            // בודקים אם יש במלאי לפני שמציגים? (בד"כ כן, אבל כאן נחזיר את כל הסוגים הקיימים בחנות)
            if (category.equals("weapon") && item instanceof Weapon) {
                result.add(item);
            } else if (category.equals("armor") && item instanceof Armor) {
                result.add(item);
            } else if (category.equals("potion") && item instanceof Potion) {
                result.add(item);
            }
        }
        return result;
    }

    // ============================================================
    // קנייה ומכירה
    // ============================================================

    /**
     * מימוש buyItem
     * השחקן קונה פריט מהחנות.
     * * @param customer השחקן הקונה
     * @param itemName שם הפריט לקנייה
     * @return הפריט שנקנה
     * @throws ItemNotFoundException אם הפריט לא קיים בחנות
     * @throws InsufficientGoldException אם אין מספיק זהב
     * @throws InventoryFullException אם המלאי של השחקן מלא
     */
    public Item buyItem(Character customer, String itemName)
            throws ItemNotFoundException, InsufficientGoldException,
            InventoryFullException {

        // 1. חפש את הפריט ב-inventory לפי שם
        Item itemToBuy = null;
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                itemToBuy = item;
                break;
            }
        }

        if (itemToBuy == null) {
            throw new ItemNotFoundException(itemName);
        }

        // 2. בדוק שיש מלאי (stock > 0)
        int currentStock = getItemStock(itemName);
        if (currentStock <= 0) {
            throw new ItemNotFoundException(itemName + " (Out of stock)");
        }

        // 3. בדוק שיש לשחקן מספיק זהב
        if (customer.getGold() < itemToBuy.getBuyPrice()) {
            throw new InsufficientGoldException(itemToBuy.getBuyPrice(), customer.getGold());
        }

        // 4. בדוק שיש מקום במלאי של השחקן (לפני שלוקחים כסף)
        if (customer.getInventorySize() >= customer.getMaxInventorySize()) {
            throw new InventoryFullException(itemName, customer.getMaxInventorySize());
        }

        // 5. בצע את העסקה
        customer.spendGold(itemToBuy.getBuyPrice());
        customer.addItem(itemToBuy); // הוספת הפריט לשחקן
        stock.put(itemName, currentStock - 1); // הפחתת המלאי בחנות

        return itemToBuy;
    }

    /**
     * מימוש sellItem
     * השחקן מוכר פריט לחנות.
     * * @param seller השחקן המוכר
     * @param itemName שם הפריט למכירה
     * @return כמות הזהב שהתקבלה
     * @throws ItemNotFoundException אם הפריט לא נמצא במלאי השחקן
     * @throws InvalidActionException אם הפריט לא ניתן למכירה
     */
    public int sellItem(Character seller, String itemName)
            throws ItemNotFoundException, InvalidActionException {

        // 1. חפש את הפריט במלאי השחקן (אנחנו לא מסירים עדיין כדי לבדוק אותו)
        Item itemToSell = null;
        for (Item item : seller.getInventory()) {
            if (item.getName().equals(itemName)) {
                itemToSell = item;
                break;
            }
        }

        if (itemToSell == null) {
            throw new ItemNotFoundException(itemName);
        }

        // 2. בדוק שהפריט ניתן למכירה
        if (!itemToSell.isSellable()) {
            throw new InvalidActionException("Sell Item", "Item '" + itemName + "' cannot be sold.");
        }

        // 3. הסר מהשחקן והוסף לחנות
        seller.removeItem(itemName); // זורק ItemNotFoundException שכבר טיפלנו בו תיאורטית
        addItemToShop(itemToSell, 1);

        // 4. תן לשחקן את הזהב
        int sellPrice = itemToSell.getSellPrice();
        seller.addGold(sellPrice);

        return sellPrice;
    }

    /**
     * מימוש getItemStock
     * מחזיר את כמות המלאי של פריט מסוים.
     * * @param itemName שם הפריט
     * @return הכמות במלאי, או 0 אם לא קיים
     */
    public int getItemStock(String itemName) {
        return stock.getOrDefault(itemName, 0);
    }

    /**
     * מימוש getTotalValue
     * מחשב את הערך הכולל של כל הפריטים בחנות.
     * * @return הערך הכולל
     */
    public int getTotalValue() {
        int totalValue = 0;
        for (Item item : inventory) {
            int quantity = getItemStock(item.getName());
            totalValue += item.getBuyPrice() * quantity;
        }
        return totalValue;
    }

    // ============================================================
    // דוחות (שימוש ב-HashMap)
    // ============================================================

    /**
     * מימוש getInventoryReport
     * מחזיר HashMap עם דוח מלאי: שם פריט -> מידע (מחיר וכמות).
     * * @return HashMap של (String -> String) בפורמט "Price: X, Stock: Y"
     */
    public HashMap<String, String> getInventoryReport() {
        HashMap<String, String> report = new HashMap<>();

        for (Item item : inventory) {
            String info = String.format("Price: %d, Stock: %d",
                    item.getBuyPrice(), getItemStock(item.getName()));
            report.put(item.getName(), info);
        }

        return report;
    }

    /**
     * מימוש getPriceComparison
     * משווה מחירים בין קנייה למכירה.
     * * @return HashMap של (String -> int[]) כאשר [0]=buyPrice, [1]=sellPrice
     */
    public HashMap<String, int[]> getPriceComparison() {
        HashMap<String, int[]> comparison = new HashMap<>();

        for (Item item : inventory) {
            int[] prices = new int[2];
            prices[0] = item.getBuyPrice();
            prices[1] = item.getSellPrice();
            comparison.put(item.getName(), prices);
        }

        return comparison;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getUniqueItemCount() {
        return inventory.size();
    }

    public int getTotalItemCount() {
        int total = 0;
        for (int count : stock.values()) {
            total += count;
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("Shop: %s | Items: %d unique, %d total",
                name, getUniqueItemCount(), getTotalItemCount());
    }
}