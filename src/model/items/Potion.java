package model.items;

import model.characters.Character;

/**
 * מחלקה המייצגת שיקוי במשחק.
 * יורשת מ-Item וגם מממשת את Usable.
 */
public class Potion extends Item implements Usable {
    
    private PotionType potionType;
    private int potency;
    private int remainingUses;
    private final int maxUses;
    
    /**
     * סוגי שיקויים במשחק
     */
    public enum PotionType {
        HEALTH("Restores health points"),
        MANA("Restores mana points"),
        STRENGTH("Temporarily increases strength"),
        DEFENSE("Temporarily increases defense");
        
        private final String effect;
        
        PotionType(String effect) {
            this.effect = effect;
        }
        
        public String getEffect() {
            return effect;
        }
    }
    
    public Potion(String name, String description, int basePrice, ItemRarity rarity,
                  PotionType potionType, int potency, int maxUses) {
        super(name, description, 1, basePrice, rarity); // Potions weigh 1
        this.potionType = potionType;
        this.potency = potency;
        this.maxUses = maxUses;
        this.remainingUses = maxUses;
    }
    
    // ============================================================
    // TODO: מימוש ממשק Usable
    // ============================================================
    
    /**
     * משתמש בשיקוי על הדמות.
     * - אם אין שימושים נותרים או שלא ניתן להשתמש, מחזיר false
     * - אם הסוג הוא HEALTH, מרפא את הדמות בכמות potency
     * - אם הסוג הוא MANA, משחזר מאנה בכמות potency
     * - מפחית שימוש אחד מ-remainingUses
     * 
     * @param target הדמות עליה משתמשים בשיקוי
     * @return true אם ההשימוש הצליח
     */
    @Override
    public boolean use(Character target) {
        if(!canUse(target))
            return false;
        switch (potionType)
        {
            case HEALTH -> {
                target.heal(potency);
                break;
            }
            case MANA -> {
                target.restoreMana(potency);
                break;
            }
            default -> remainingUses--;
        }
        return true;
    }
    
    /**
     * בודק אם ניתן להשתמש בשיקוי על הדמות.
     * - שיקוי בריאות: ניתן להשתמש רק אם הדמות לא בבריאות מלאה
     * - שיקוי מאנה: ניתן להשתמש רק אם הדמות לא במאנה מלאה
     * - שאר השיקויים: תמיד ניתן להשתמש
     * - בכל מקרה, צריך שיהיו שימושים נותרים
     * 
     * @param target הדמות לבדיקה
     * @return true אם ניתן להשתמש
     */
    @Override
    public boolean canUse(Character target) {
        if(remainingUses==0)
            return false;
        switch (potionType)
        {
            case HEALTH:
                return target.getCurrentHealth() < target.getMaxHealth();
            case MANA:
                return target.getCurrentMana() < target.getMaxMana();
            default:
                return true;
        }
    }
    
    @Override
    public int getRemainingUses() {
        return remainingUses;
    }
    
    /**
     * שיקוי שנעשה בו שימוש (remainingUses < maxUses) לא ניתן למכירה.
     * 
     * @return true אם ניתן למכור
     */
    @Override
    public boolean isSellable() {
        return remainingUses == maxUses;
    }
    
    // Getters
    public PotionType getPotionType() {
        return potionType;
    }
    
    public int getPotency() {
        return potency;
    }
    
    public int getMaxUses() {
        return maxUses;
    }
    
    @Override
    public String toString() {
        return String.format("%s | Type: %s | Potency: %d | Uses: %d/%d",
            super.toString(), potionType, potency, remainingUses, maxUses);
    }
}
