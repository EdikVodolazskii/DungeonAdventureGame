package model.characters;

/**
 * מחלקה המייצגת לוחם במשחק.
 * יורשת מ-Character.
 * הלוחם מתמחה בהתקפות פיזיות חזקות ובהגנה גבוהה.
 */
public class Warrior extends Character {

    private int rage;
    private static final int MAX_RAGE = 100;
    private static final int RAGE_PER_HIT = 10;
    private static final int BERSERK_RAGE_COST = 50;

    public Warrior(String name) {
        // לוחם: הרבה חיים, מעט מאנה, כוח גבוה, הגנה גבוהה
        super(name, 150, 30, 15, 10);
        this.rage = 0;
    }

    // ============================================================
    // TODO: מימוש מתודות אבסטרקטיות
    // ============================================================

    /**
     * כאשר לוחם עולה רמה:
     * - maxHealth עולה ב-20
     * - maxMana עולה ב-5
     * - baseStrength עולה ב-3
     * - baseDefense עולה ב-2
     * - currentHealth ו-currentMana מתמלאים למקסימום
     */
    @Override
    protected void onLevelUp() {
        maxHealth += 20;
        maxMana += 5;
        baseStrength += 3;
        baseDefense += 2;
        currentHealth = maxHealth;
        currentMana = maxMana;
    }

    /**
     * נזק לוחם = baseStrength + נזק נשק (אם יש) + בונוס זעם
     * בונוס זעם = rage / 10 (מספר שלם)
     * אם אין נשק, נזק הנשק הוא 0
     *
     * @return נזק ההתקפה
     */
    @Override
    public int calculateAttackDamage() {
        int armourDamage = (equippedWeapon != null) ? equippedWeapon.calculateDamage():0;
        int totalDamage = baseStrength + armourDamage + rage/10;
        return totalDamage;
    }

    /**
     * יכולת מיוחדת: זעם ברסרק
     * - עולה BERSERK_RAGE_COST זעם
     * - גורם נזק כפול מנזק התקפה רגיל ליריב
     * - מחזיר true אם הצליח, false אם אין מספיק זעם
     *
     * @param target היריב
     * @return true אם היכולת בוצעה
     */
    @Override
    public boolean useSpecialAbility(Character target) {
        if (rage < BERSERK_RAGE_COST) {
            return false;
        }
        rage -= BERSERK_RAGE_COST;
        int damage = calculateAttackDamage() * 2;
        target.takeDamage(damage);
        return true;
    }

    /**
     * כאשר הלוחם מקבל נזק, הוא גם צובר זעם.
     * קודם קרא למימוש של המחלקה הבסיסית (כשתממש אותה)
     * ואז הוסף RAGE_PER_HIT לזעם (מקסימום MAX_RAGE)
     */
    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        rage = Math.min(MAX_RAGE, rage + RAGE_PER_HIT);
    }

    // ============================================================
    // מתודות ייחודיות ללוחם
    // ============================================================

    /**
     * הלוחם חוסם בעזרת מגן ומקבל רק 25% מהנזק.
     * עולה 20 מאנה.
     *
     * @param incomingDamage הנזק הנכנס
     * @return true אם החסימה הצליחה
     */
    public boolean shieldBlock(int incomingDamage) {
        if(!useMana(20))
            return false;

        int lessDamage = (int) Math.ceil(incomingDamage*0.25);
        takeDamage(lessDamage);
        return true;
    }

    // Getters
    public int getRage() {
        return rage;
    }

    public int getMaxRage() {
        return MAX_RAGE;
    }

    @Override
    public String toString() {
        return "Warrior: " + super.toString() +
                String.format(" | Rage: %d/%d", rage, MAX_RAGE);
    }
}
