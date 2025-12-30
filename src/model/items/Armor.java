package model.items;

/**
 * מחלקה המייצגת שריון במשחק.
 * יורשת מ-Item ומוסיפה מאפייני הגנה.
 */
public class Armor extends Item {
    
    private int defense;
    private ArmorSlot slot;
    
    /**
     * חלקי הגוף עליהם ניתן ללבוש שריון
     */
    public enum ArmorSlot {
        HEAD("Head", 0.15),
        CHEST("Chest", 0.40),
        LEGS("Legs", 0.25),
        BOOTS("Boots", 0.10),
        GLOVES("Gloves", 0.10);
        
        private final String displayName;
        private final double defenseContribution;
        
        ArmorSlot(String displayName, double defenseContribution) {
            this.displayName = displayName;
            this.defenseContribution = defenseContribution;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public double getDefenseContribution() {
            return defenseContribution;
        }
    }
    
    public Armor(String name, String description, int weight, int basePrice,
                 ItemRarity rarity, int defense, ArmorSlot slot) {
        super(name, description, weight, basePrice, rarity);
        this.defense = defense;
        this.slot = slot;
    }
    
    // ============================================================
    // TODO: מימוש מתודות השריון
    // ============================================================
    
    /**
     * מחשב את אחוז הפחתת הנזק שהשריון מספק.
     * הנוסחה: defense * defenseContribution / 100
     * (אך לא יותר מ-75%)
     * 
     * @return אחוז הפחתת נזק (בין 0.0 ל-0.75)
     */
    public double calculateDamageReduction() {

        return Math.min(defense * getSlot().getDefenseContribution()/100, 0.75);
    }
    
    /**
     * מחשב כמה נזק יקבל השחקן אחרי הפחתת השריון.
     * 
     * @param incomingDamage הנזק המקורי
     * @return הנזק אחרי ההפחתה (מספר שלם, לעגל כלפי מעלה)
     */
    public int reduceDamage(int incomingDamage) {
        return (int) Math.ceil(incomingDamage * (1 - calculateDamageReduction()));
    }
    
    // Getters
    public int getDefense() {
        return defense;
    }
    
    public ArmorSlot getSlot() {
        return slot;
    }
    
    @Override
    public String toString() {
        return String.format("%s | Defense: %d | Slot: %s",
            super.toString(), defense, slot.getDisplayName());
    }
}
