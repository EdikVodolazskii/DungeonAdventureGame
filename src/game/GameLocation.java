package game;

import model.items.Item;
import java.util.ArrayList;

/**
 * מחלקה המייצגת מיקום במפת המשחק.
 */
public class GameLocation {
    
    private String id;
    private String name;
    private String description;
    private ArrayList<String> connectedLocationIds;
    private ArrayList<Item> loot;
    private boolean visited;
    private boolean hasMaster;
    private int dangerLevel;
    
    public GameLocation(String id, String name, String description, int dangerLevel) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dangerLevel = dangerLevel;
        this.connectedLocationIds = new ArrayList<>();
        this.loot = new ArrayList<>();
        this.visited = false;
        this.hasMaster = false;
    }
    
    // ============================================================
    // TODO: ניהול מיקום
    // ============================================================
    
    /**
     * מוסיף חיבור למיקום אחר.
     * לא להוסיף כפילויות!
     * 
     * @param locationId המזהה של המיקום המחובר
     */
    public void addConnection(String locationId) {
        if(!connectedLocationIds.contains(locationId))
        {
            connectedLocationIds.add(locationId);
        }
    }
    
    /**
     * בודק אם המיקום הזה מחובר למיקום אחר.
     * 
     * @param locationId המזהה של המיקום לבדיקה
     * @return true אם מחובר
     */
    public boolean isConnectedTo(String locationId) {
        return connectedLocationIds.contains(locationId);
    }
    
    /**
     * מוסיף פריט לשלל במיקום.
     * 
     * @param item הפריט להוספה
     */
    public void addLoot(Item item) {
        loot.add(item);
    }
    
    /**
     * אוסף את כל השלל מהמיקום ומרוקן אותו.
     * 
     * @return רשימה של כל הפריטים שנאספו
     */
    public ArrayList<Item> collectAllLoot() {
        ArrayList<Item> collected = new ArrayList<>(loot);
        loot.clear();
        return collected;
    }
    
    /**
     * מסמן את המיקום כמבוקר.
     */
    public void markAsVisited() {
        this.visited = true;
    }
    
    // Getters & Setters
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ArrayList<String> getConnectedLocationIds() {
        return new ArrayList<>(connectedLocationIds);
    }
    
    public boolean isVisited() {
        return visited;
    }
    
    public boolean hasMaster() {
        return hasMaster;
    }
    
    public void setHasMaster(boolean hasMaster) {
        this.hasMaster = hasMaster;
    }
    
    public int getDangerLevel() {
        return dangerLevel;
    }
    
    public boolean hasLoot() {
        return !loot.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s (Danger: %d) - %s%s",
            id, name, dangerLevel, description,
            visited ? " [VISITED]" : "");
    }
}
