package model.items;

/**
 * מחלקה המייצגת נשק במשחק.
 * יורשת מ-Item ומוסיפה מאפייני נזק.
 */
public class Weapon extends Item {
    
    private int minDamage;
    private int maxDamage;
    private WeaponType weaponType;
    
    /**
     * סוגי נשקים במשחק
     */
    public enum WeaponType {
        SWORD("Melee", 1.0),
        AXE("Melee", 1.2),
        BOW("Ranged", 0.9),
        STAFF("Magic", 1.1),
        DAGGER("Melee", 0.7);
        
        private final String category;
        private final double speedModifier;
        
        WeaponType(String category, double speedModifier) {
            this.category = category;
            this.speedModifier = speedModifier;
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getSpeedModifier() {
            return speedModifier;
        }
    }
    
    public Weapon(String name, String description, int weight, int basePrice, 
                  ItemRarity rarity, int minDamage, int maxDamage, WeaponType weaponType) {
        super(name, description, weight, basePrice, rarity);
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.weaponType = weaponType;
    }
    
    // ============================================================
    // TODO: מימוש מתודות הנשק
    // ============================================================
    
    /**
     * TODO: מימוש calculateDamage
     * מחשב נזק אקראי בין minDamage ל-maxDamage (כולל).
     * השתמש ב-Math.random()
     * 
     * @return נזק אקראי
     */
    public int calculateDamage() {
        return minDamage + (int)(Math.random() * (maxDamage - minDamage + 1));
    }
    
    /**
     * TODO: מימוש getAverageDamage
     * מחזיר את הנזק הממוצע של הנשק.
     * 
     * @return נזק ממוצע (מספר עשרוני)
     */
    public double getAverageDamage() {
        return (minDamage+maxDamage)*0.5;
    }
    
    // Getters
    public int getMinDamage() {
        return minDamage;
    }
    
    public int getMaxDamage() {
        return maxDamage;
    }
    
    public WeaponType getWeaponType() {
        return weaponType;
    }
    
    @Override
    public String toString() {
        return String.format("%s | Damage: %d-%d | Type: %s", 
            super.toString(), minDamage, maxDamage, weaponType);
    }
}
