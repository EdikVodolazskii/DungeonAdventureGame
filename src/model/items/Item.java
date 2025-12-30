package model.items;

/**
 * מחלקה בסיסית המייצגת פריט במשחק.
 * כל סוגי הפריטים (נשק, שריון, שיקוי) יורשים ממחלקה זו.
 */
public abstract class Item implements Tradeable {
    
    protected String name;
    protected String description;
    protected int weight;
    protected int basePrice;
    protected ItemRarity rarity;
    
    /**
     * enum המייצג את רמת הנדירות של הפריט
     */
    public enum ItemRarity {
        COMMON(1.0),
        UNCOMMON(1.5),
        RARE(2.5),
        EPIC(4.0),
        LEGENDARY(10.0);
        
        private final double priceMultiplier;
        
        ItemRarity(double priceMultiplier) {
            this.priceMultiplier = priceMultiplier;
        }
        
        public double getPriceMultiplier() {
            return priceMultiplier;
        }
    }
    
    public Item(String name, String description, int weight, int basePrice, ItemRarity rarity) {
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.basePrice = basePrice;
        this.rarity = rarity;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public ItemRarity getRarity() {
        return rarity;
    }
    
    // ============================================================
    // TODO: מימוש ממשק Tradeable
    // ============================================================
    
    /**
     * TODO: מימוש getBuyPrice
     * מחיר הקנייה = basePrice * priceMultiplier של הנדירות
     * יש להחזיר מספר שלם (לעגל כלפי מעלה)
     */
    @Override
    public int getBuyPrice() {
        double fullPrice = basePrice * rarity.getPriceMultiplier();
        return (int) Math.ceil(fullPrice);
    }
    
    /**
     * TODO: מימוש getSellPrice
     * מחיר המכירה = 50% ממחיר הקנייה
     * יש להחזיר מספר שלם (לעגל כלפי מעלה)
     */
    @Override
    public int getSellPrice() {
        return (int) Math.ceil(getBuyPrice()*0.5);
    }
    
    /**
     * TODO: מימוש isSellable
     * כל הפריטים הרגילים ניתנים למכירה.
     * מחלקות יורשות יכולות לדרוס התנהגות זו.
     */
    @Override
    public boolean isSellable() {
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s (Weight: %d)", 
            rarity, name, description, weight);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return name.equals(item.name) && rarity == item.rarity;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() * 31 + rarity.hashCode();
    }
}
