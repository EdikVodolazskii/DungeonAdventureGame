package model.characters;

import model.items.Weapon;
import model.items.Armor;
import model.items.Item;
import model.exceptions.InventoryFullException;
import model.exceptions.ItemNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * מחלקה אבסטרקטית המייצגת דמות במשחק.
 * מממשת את ממשק Attackable.
 * כל סוגי הדמויות (Warrior, Mage, Archer) יורשים ממחלקה זו.
 */
public abstract class Character implements Attackable {
    
    // Basic stats
    protected String name;
    protected int level;
    protected int experience;
    protected int gold;
    
    // Health & Mana
    protected int currentHealth;
    protected int maxHealth;
    protected int currentMana;
    protected int maxMana;
    
    // Combat stats
    protected int baseStrength;
    protected int baseDefense;
    
    // Equipment - HashMap מ-slot לשריון
    protected HashMap<Armor.ArmorSlot, Armor> equippedArmor;
    protected Weapon equippedWeapon;
    
    // Inventory - ArrayList של פריטים + Stack לפריטים אחרונים שהשתמשנו בהם
    protected ArrayList<Item> inventory;
    protected Stack<Item> recentlyUsedItems;
    protected final int maxInventorySize;
    
    // Constants
    protected static final int EXPERIENCE_PER_LEVEL = 100;
    protected static final int DEFAULT_INVENTORY_SIZE = 20;
    
    public Character(String name, int maxHealth, int maxMana, 
                     int baseStrength, int baseDefense) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.gold = 0;
        
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.maxMana = maxMana;
        this.currentMana = maxMana;
        
        this.baseStrength = baseStrength;
        this.baseDefense = baseDefense;
        
        this.equippedArmor = new HashMap<>();
        this.equippedWeapon = null;
        
        this.inventory = new ArrayList<>();
        this.recentlyUsedItems = new Stack<>();
        this.maxInventorySize = DEFAULT_INVENTORY_SIZE;
    }
    
    // ============================================================
    // TODO: מימוש ממשק Attackable
    // ============================================================
    
    /**
     * מקבל נזק והופך אותו לנזק בפועל אחרי הפחתת שריון.
     * - חשב את סך הפחתת הנזק מכל חלקי השריון המצוידים
     * - הפחת את הנזק המופחת מ-currentHealth
     * - currentHealth לא יכול לרדת מתחת ל-0
     */
    @Override
    public void takeDamage(int damage) {
        double armorReduction = 0;
        for(Armor armor : equippedArmor.values())
        {
            armorReduction+=armor.calculateDamageReduction();
        }

        armorReduction = Math.min(armorReduction, 0.75);

        int actualDamage = (int) Math.ceil(damage * (1 - armorReduction));

        currentHealth = Math.max(0, currentHealth - actualDamage);
    }
    
    /**
     * @return true אם currentHealth > 0
     */
    @Override
    public boolean isAlive() {
        return currentHealth>0;
    }
    
    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    @Override
    public int getMaxHealth() {
        return maxHealth;
    }
    
    // ============================================================
    // TODO: ניהול מלאי (Inventory Management)
    // ============================================================
    
    /**
     * מוסיף פריט למלאי.
     * 
     * @param item הפריט להוספה
     * @throws InventoryFullException אם המלאי מלא
     */
    public void addItem(Item item) throws InventoryFullException {
        if (inventory.size() >= maxInventorySize) {
            throw new InventoryFullException(item.getName(), maxInventorySize);
        }
        inventory.add(item);
    }
    
    /**
     * מסיר פריט מהמלאי לפי שם.
     * 
     * @param itemName שם הפריט להסרה
     * @return הפריט שהוסר
     * @throws ItemNotFoundException אם הפריט לא נמצא
     */
    public Item removeItem(String itemName) throws ItemNotFoundException {
        int index = -1;
        for(int i = 0; i < inventory.size(); i++)
        {
            if(inventory.get(i).getName().equals(itemName))
            {
                index = i;
                break;
            }
        }
        if(index!=-1)
        {
            return inventory.remove(index);
        }
        else
        {
            throw new ItemNotFoundException(itemName);
        }
    }
    
    /**
     * מחזיר רשימה של כל הפריטים מסוג מסוים במלאי.
     * השתמש ב-instanceof לבדיקת הסוג.
     * 
     * @param itemClass המחלקה לחיפוש (לדוגמה: Weapon.class)
     * @return רשימה של פריטים מהסוג המבוקש
     */
    public <T extends Item> ArrayList<T> findItemsByType(Class<T> itemClass) {
        ArrayList<T> result = new ArrayList<>();
        for (Item item : inventory) {
            if (itemClass.isInstance(item)) {
                result.add((T) item);
            }
        }
        return result;
    }
    
    /**
     * מחזיר HashMap שממפה רמת נדירות לרשימת פריטים.
     * 
     * @return HashMap של (ItemRarity -> ArrayList של Items)
     */
    public HashMap<Item.ItemRarity, ArrayList<Item>> getItemsByRarity() {
        HashMap<Item.ItemRarity, ArrayList<Item>> rarityMap = new HashMap<>();

        for (Item.ItemRarity rarity : Item.ItemRarity.values()) {
            rarityMap.put(rarity, new ArrayList<>());
        }

        for(Item item : inventory)
        {
           rarityMap.get(item.getRarity()).add(item);
        }

        return rarityMap;
    }
    
    // ============================================================
    // TODO: ציוד (Equipment)
    // ============================================================
    
    /**
     * מציית נשק. אם כבר יש נשק מצויד, מחזיר אותו למלאי.
     * 
     * @param weapon הנשק לציוד
     * @throws ItemNotFoundException אם הנשק לא נמצא במלאי
     * @throws InventoryFullException אם אי אפשר להחזיר את הנשק הישן למלאי
     */
    public void equipWeapon(Weapon weapon) throws ItemNotFoundException, InventoryFullException {
        if(!inventory.contains(weapon))
        {
            throw new ItemNotFoundException(weapon.getName());
        }

        if(equippedWeapon!=null)
            addItem(equippedWeapon);
        equippedWeapon = (Weapon) removeItem(weapon.getName());
    }
    
    /**
     * מציית שריון. אם כבר יש שריון באותו slot, מחזיר אותו למלאי.
     * 
     * @param armor השריון לציוד
     * @throws ItemNotFoundException אם השריון לא נמצא במלאי
     * @throws InventoryFullException אם אי אפשר להחזיר את השריון הישן למלאי
     */
    public void equipArmor(Armor armor) throws ItemNotFoundException, InventoryFullException {

        if(!inventory.contains(armor))
        {
            throw new ItemNotFoundException(armor.getName());
        }

        Armor.ArmorSlot slot = armor.getSlot();
        if (equippedArmor.containsKey(slot)) {
            addItem(equippedArmor.get(slot));
        }

        inventory.remove(armor);
        equippedArmor.put(slot, armor);
    }
    
    /**
     * מחשב את סך ההגנה מכל חלקי השריון.
     * 
     * @return סך ההגנה
     */
    public int getTotalDefense() {
        int totalDef = baseDefense;
        for(Armor armor : equippedArmor.values())
        {
            totalDef+=armor.getDefense();
        }
        return totalDef;
    }
    
    // ============================================================
    // TODO: ריפוי ומאנה
    // ============================================================
    
    /**
     * מרפא את הדמות בכמות מסוימת.
     * currentHealth לא יכול לעלות מעל maxHealth.
     * 
     * @param amount כמות הריפוי
     */
    public void heal(int amount) {
        currentHealth = Math.min(currentHealth+amount, maxHealth);
    }
    
    /**
     * משחזר מאנה לדמות.
     * currentMana לא יכול לעלות מעל maxMana.
     * 
     * @param amount כמות המאנה לשחזור
     */
    public void restoreMana(int amount) {
        currentMana = Math.min(maxMana, currentMana+amount);
    }
    
    /**
     * משתמש במאנה לכישוף.
     * 
     * @param amount כמות המאנה לשימוש
     * @return true אם היה מספיק מאנה והשימוש הצליח
     */
    public boolean useMana(int amount) {
        if(currentMana>amount)
        {
            currentMana-=amount;
            return true;
        }
        return false;
    }
    
    // ============================================================
    // TODO: ניסיון ורמות (Experience & Leveling)
    // ============================================================
    
    /**
     * מוסיף ניסיון לדמות ומעלה רמה אם צריך.
     * כל EXPERIENCE_PER_LEVEL נקודות ניסיון מעלות רמה אחת.
     * ניתן לעלות כמה רמות בבת אחת.
     * 
     * @param amount כמות הניסיון
     */
    public void gainExperience(int amount) {
        experience+=amount;

        while (experience >= level*EXPERIENCE_PER_LEVEL)
        {
            experience-=level*EXPERIENCE_PER_LEVEL;
            level++;
            onLevelUp();
        }
    }
    
    /**
     * מתודה אבסטרקטית שנקראת כאשר הדמות עולה רמה.
     * כל סוג דמות מגדיר מה קורה כשעולים רמה.
     */
    protected abstract void onLevelUp();
    
    /**
     * מתודה אבסטרקטית לחישוב נזק התקפה.
     * כל סוג דמות מחשב נזק בצורה שונה.
     * 
     * @return נזק ההתקפה
     */
    public abstract int calculateAttackDamage();
    
    /**
     * מתודה אבסטרקטית לביצוע יכולת מיוחדת.
     * כל סוג דמות יש לו יכולת מיוחדת.
     * 
     * @param target היעד של היכולת
     * @return true אם היכולת בוצעה בהצלחה
     */
    public abstract boolean useSpecialAbility(Character target);
    
    // ============================================================
    // Recently Used Items Stack
    // ============================================================
    
    /**
     * מוסיף פריט לסטאק הפריטים האחרונים.
     * @param item הפריט שנעשה בו שימוש
     */
    public void pushRecentlyUsed(Item item) {
        recentlyUsedItems.push(item);
    }
    
    /**
     * מחזיר את הפריט האחרון שנעשה בו שימוש.
     * @return הפריט האחרון, או null אם הסטאק ריק
     */
    public Item popRecentlyUsed() {
        if (recentlyUsedItems.isEmpty()) {
            return null;
        }
        return recentlyUsedItems.pop();
    }
    
    /**
     * מציץ לפריט האחרון בלי להסיר אותו.
     * @return הפריט האחרון, או null אם הסטאק ריק
     */
    public Item peekRecentlyUsed() {
        if (recentlyUsedItems.isEmpty()) {
            return null;
        }
        return recentlyUsedItems.peek();
    }
    
    // ============================================================
    // Getters & Setters
    // ============================================================
    
    public String getName() {
        return name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public int getGold() {
        return gold;
    }
    
    public void addGold(int amount) {
        this.gold += amount;
    }
    
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }
    
    public int getCurrentMana() {
        return currentMana;
    }
    
    public int getMaxMana() {
        return maxMana;
    }
    
    public int getBaseStrength() {
        return baseStrength;
    }
    
    public int getBaseDefense() {
        return baseDefense;
    }
    
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }
    
    public HashMap<Armor.ArmorSlot, Armor> getEquippedArmor() {
        return new HashMap<>(equippedArmor);
    }
    
    public ArrayList<Item> getInventory() {
        return new ArrayList<>(inventory);
    }
    
    public int getInventorySize() {
        return inventory.size();
    }
    
    public int getMaxInventorySize() {
        return maxInventorySize;
    }
    
    @Override
    public String toString() {
        return String.format("%s (Level %d) - HP: %d/%d, Mana: %d/%d, Gold: %d",
            name, level, currentHealth, maxHealth, currentMana, maxMana, gold);
    }
}
