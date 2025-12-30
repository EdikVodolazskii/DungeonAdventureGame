package utils;

import model.characters.Character;
import model.items.Item;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * מחלקת עזר עם פונקציות שימושיות למשחק.
 * כאן נתרגל שימוש במחלקות אנונימיות ו-Comparator.
 */
public class GameUtils {

    // ============================================================
    // מיון פריטים (שימוש במחלקות אנונימיות)
    // ============================================================

    /**
     * ממיין רשימת פריטים לפי מחיר (מהזול ליקר).
     * השתמש ב-Comparator כמחלקה אנונימית!
     * * @param items רשימת הפריטים למיון
     */
    public static void sortItemsByPrice(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                // מחזיר מספר שלילי אם i1 זול יותר, חיובי אם i2 זול יותר
                return i1.getBuyPrice() - i2.getBuyPrice();
            }
        });
    }

    /**
     * ממיין רשימת פריטים לפי מחיר (מהיקר לזול).
     * השתמש ב-Comparator כמחלקה אנונימית!
     * * @param items רשימת הפריטים למיון
     */
    public static void sortItemsByPriceDescending(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                // הפוך: מחסרים את המחיר של i1 מ-i2 כדי לקבל סדר יורד
                return i2.getBuyPrice() - i1.getBuyPrice();
            }
        });
    }

    /**
     * ממיין רשימת פריטים לפי נדירות (מהנפוץ לנדיר ביותר).
     * סדר הנדירות: COMMON < UNCOMMON < RARE < EPIC < LEGENDARY
     * השתמש ב-ordinal() של ה-enum!
     * * @param items רשימת הפריטים למיון
     */
    public static void sortItemsByRarity(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                // השוואה לפי האינדקס של ה-Enum (ordinal)
                return i1.getRarity().ordinal() - i2.getRarity().ordinal();
            }
        });
    }

    /**
     * ממיין רשימת פריטים לפי שם (אלפביתי).
     * * @param items רשימת הפריטים למיון
     */
    public static void sortItemsByName(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                return i1.getName().compareTo(i2.getName());
            }
        });
    }

    /**
     * ממיין רשימת פריטים לפי משקל (מהקל לכבד).
     * * @param items רשימת הפריטים למיון
     */
    public static void sortItemsByWeight(ArrayList<Item> items) {
        items.sort(new Comparator<Item>() {
            @Override
            public int compare(Item i1, Item i2) {
                return i1.getWeight() - i2.getWeight();
            }
        });
    }

    // ============================================================
    // מיון דמויות
    // ============================================================

    /**
     * ממיין רשימת דמויות לפי בריאות נוכחית (מהנמוך לגבוה).
     * * @param characters רשימת הדמויות למיון
     */
    public static void sortCharactersByHealth(ArrayList<Character> characters) {
        characters.sort(new Comparator<Character>() {
            @Override
            public int compare(Character c1, Character c2) {
                return c1.getCurrentHealth() - c2.getCurrentHealth();
            }
        });
    }

    /**
     * ממיין רשימת דמויות לפי רמה (מהגבוה לנמוך).
     * * @param characters רשימת הדמויות למיון
     */
    public static void sortCharactersByLevel(ArrayList<Character> characters) {
        characters.sort(new Comparator<Character>() {
            @Override
            public int compare(Character c1, Character c2) {
                // סדר יורד: c2 פחות c1
                return c2.getLevel() - c1.getLevel();
            }
        });
    }

    // ============================================================
    // סינון (Filtering)
    // ============================================================

    /**
     * ממשק פונקציונלי לסינון פריטים.
     */
    public interface ItemFilter {
        boolean accept(Item item);
    }

    /**
     * מסנן רשימת פריטים לפי תנאי מסוים.
     * * @param items רשימת הפריטים
     * @param filter הפילטר
     * @return רשימה חדשה עם הפריטים שעברו את הסינון
     */
    public static ArrayList<Item> filterItems(ArrayList<Item> items, ItemFilter filter) {
        ArrayList<Item> filteredList = new ArrayList<>();
        for (Item item : items) {
            // אם הפריט עובר את הסינון (accept מחזיר true), מוסיפים אותו
            if (filter.accept(item)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    /**
     * מסנן פריטים שהשחקן יכול לקנות.
     * השתמש ב-filterItems עם מחלקה אנונימית!
     * * @param items רשימת הפריטים
     * @param playerGold כמות הזהב של השחקן
     * @return רשימת פריטים שניתן לקנות
     */
    public static ArrayList<Item> filterAffordableItems(ArrayList<Item> items, int playerGold) {
        return filterItems(items, new ItemFilter() {
            @Override
            public boolean accept(Item item) {
                // בודקים אם מחיר הקנייה קטן או שווה לזהב שיש לשחקן
                return item.getBuyPrice() <= playerGold;
            }
        });
    }

    /**
     * מסנן פריטים לפי נדירות מינימלית.
     * * @param items רשימת הפריטים
     * @param minRarity הנדירות המינימלית
     * @return רשימת פריטים בנדירות המבוקשת או יותר
     */
    public static ArrayList<Item> filterByRarity(ArrayList<Item> items,
                                                 Item.ItemRarity minRarity) {
        return filterItems(items, new ItemFilter() {
            @Override
            public boolean accept(Item item) {
                // בודקים אם ה-ordinal של הנדירות גדול או שווה למינימום
                return item.getRarity().ordinal() >= minRarity.ordinal();
            }
        });
    }

    /**
     * מסנן פריטים קלים (עד משקל מסוים).
     * * @param items רשימת הפריטים
     * @param maxWeight המשקל המקסימלי
     * @return רשימת פריטים קלים
     */
    public static ArrayList<Item> filterLightItems(ArrayList<Item> items, int maxWeight) {
        return filterItems(items, new ItemFilter() {
            @Override
            public boolean accept(Item item) {
                return item.getWeight() <= maxWeight;
            }
        });
    }

    // ============================================================
    // פונקציות עזר נוספות
    // ============================================================

    /**
     * מוצא את הפריט ה"טוב ביותר" לפי קריטריון מסוים.
     * * @param items רשימת הפריטים
     * @param comparator הקריטריון להשוואה
     * @return הפריט הטוב ביותר, או null אם הרשימה ריקה
     */
    public static Item findBestItem(ArrayList<Item> items, Comparator<Item> comparator) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        Item bestItem = items.get(0);

        for (int i = 1; i < items.size(); i++) {
            Item currentItem = items.get(i);
            // אם currentItem "גדול יותר" מ-bestItem לפי ה-comparator
            if (comparator.compare(currentItem, bestItem) > 0) {
                bestItem = currentItem;
            }
        }

        return bestItem;
    }

    /**
     * מחשב את המשקל הכולל של כל הפריטים ברשימה.
     * * @param items רשימת הפריטים
     * @return המשקל הכולל
     */
    public static int calculateTotalWeight(ArrayList<Item> items) {
        int totalWeight = 0;
        for (Item item : items) {
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }

    /**
     * מחשב את הערך הכולל של כל הפריטים (לפי מחיר מכירה).
     * * @param items רשימת הפריטים
     * @return הערך הכולל
     */
    public static int calculateTotalValue(ArrayList<Item> items) {
        int totalValue = 0;
        for (Item item : items) {
            totalValue += item.getSellPrice();
        }
        return totalValue;
    }
}