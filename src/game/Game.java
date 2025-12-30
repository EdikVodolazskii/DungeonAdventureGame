package game;

import model.characters.*;
import model.characters.Character;
import model.items.*;
import model.items.Weapon.WeaponType;
import model.items.Armor.ArmorSlot;
import model.items.Item.ItemRarity;
import model.items.Potion.PotionType;
import model.exceptions.*;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * המחלקה הראשית של המשחק.
 * מנהלת את זרימת המשחק והאינטראקציה עם השחקן.
 */
public class Game {

    private Character player;
    private DungeonMap map;
    private Shop shop;
    private Scanner scanner;
    private boolean gameRunning;

    public Game() {
        this.scanner = new Scanner(System.in);
        this.gameRunning = false;
    }

    /**
     * מתחיל את המשחק.
     */
    public void start() {
        System.out.println("=================================");
        System.out.println("  Welcome to Dungeon Adventure!");
        System.out.println("=================================\n");

        createCharacter();
        initializeMap();
        initializeShop();

        System.out.println("\nGame started! You are in: " + map.getCurrentLocationId());

        gameRunning = true;
        gameLoop();
    }

    /**
     * מאפשר לשחקן לבחור סוג דמות וליצור אותה.
     */
    private void createCharacter() {
        // 1. בקש שם מהשחקן
        System.out.print("Enter character name: ");
        String name = scanner.nextLine();
        if (name.isEmpty()) name = "Hero";

        // 2. הצג אפשרויות: 1. Warrior, 2. Mage, 3. Archer
        System.out.println("Choose class:");
        System.out.println("1. Warrior");
        System.out.println("2. Mage");
        System.out.println("3. Archer");

        // 3. צור את הדמות המתאימה
        int choice = getPlayerChoice();
        switch (choice) {
            case 1:
                player = new Warrior(name);
                try {
                    player.addItem(new Weapon("Sword", "Basic Sword", 5, 10, ItemRarity.COMMON, 5, 8, WeaponType.SWORD));
                    player.equipWeapon((Weapon)player.getInventory().get(0));
                } catch (Exception e) {}
                break;
            case 2:
                player = new Mage(name);
                try {
                    player.addItem(new Weapon("Staff", "Wooden Staff", 3, 10, ItemRarity.COMMON, 2, 6, WeaponType.STAFF));
                    player.equipWeapon((Weapon)player.getInventory().get(0));
                } catch (Exception e) {}
                break;
            case 3:
                player = new Archer(name);
                try {
                    player.addItem(new Weapon("Bow", "Short Bow", 4, 10, ItemRarity.COMMON, 4, 7, WeaponType.BOW));
                    player.equipWeapon((Weapon)player.getInventory().get(0));
                } catch (Exception e) {}
                break;
            default:
                player = new Warrior(name);
                break;
        }

        try {
            player.addItem(new Potion("Health Potion", "Heals 20", 10, ItemRarity.COMMON, PotionType.HEALTH, 20, 1));
        } catch (InventoryFullException e) {}
    }

    /**
     * יוצר את מפת המבוך עם כמה מיקומים.
     */
    private void initializeMap() {
        // 1. צור DungeonMap חדש
        map = new DungeonMap();

        // 2. הוסף לפחות 5 מיקומים
        GameLocation town = new GameLocation("town", "Town", "Safe place", 0);
        GameLocation forest = new GameLocation("forest", "Forest", "Spooky trees", 2);
        GameLocation cave = new GameLocation("cave", "Cave", "Dark and damp", 4);
        GameLocation ruins = new GameLocation("ruins", "Ruins", "Old stones", 6);
        GameLocation boss = new GameLocation("boss", "Boss Lair", "Danger!", 10);

        map.addLocation(town);
        map.addLocation(forest);
        map.addLocation(cave);
        map.addLocation(ruins);
        map.addLocation(boss);

        // 3. חבר ביניהם
        town.addConnection("forest");
        forest.addConnection("town");
        forest.addConnection("cave");
        cave.addConnection("forest");
        forest.addConnection("ruins");
        ruins.addConnection("forest");
        cave.addConnection("boss");
        boss.addConnection("cave");

        // 4. הגדר נקודת התחלה ומיקום הבוס
        map.setStartLocation("town");
        map.setBossLocation("boss");
    }

    /**
     * יוצר את החנות עם פריטים התחלתיים.
     */
    private void initializeShop() {
        // 1. צור Shop חדש
        shop = new Shop("General Store");

        // 2. הוסף כמה נשקים, שריונים ושיקויים
        shop.addItemToShop(new Potion("Health Potion", "Heals 50", 20, ItemRarity.COMMON, PotionType.HEALTH, 50, 1), 10);
        shop.addItemToShop(new Weapon("Iron Axe", "Strong Axe", 8, 50, ItemRarity.UNCOMMON, 8, 12, WeaponType.AXE), 3);
        shop.addItemToShop(new Armor("Leather Vest", "Light Armor", 5, 40, ItemRarity.COMMON, 5, ArmorSlot.CHEST), 5);
        shop.addItemToShop(new Potion("Mana Potion", "Restores Mana", 20, ItemRarity.COMMON, PotionType.MANA, 50, 1), 10);
    }

    /**
     * לולאת המשחק הראשית.
     */
    private void gameLoop() {
        while (gameRunning && player.isAlive()) {
            displayMenu();
            int choice = getPlayerChoice();
            handleChoice(choice);
        }

        if (!player.isAlive()) {
            System.out.println("Game Over!");
        }
        System.out.println("\nThanks for playing Dungeon Adventure!");
    }

    /**
     * מציג את התפריט הראשי.
     */
    private void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. View Character");
        System.out.println("2. View Inventory");
        System.out.println("3. Move to Location");
        System.out.println("4. Visit Shop");
        System.out.println("5. Battle");
        System.out.println("6. Save & Quit");
        System.out.print("Choose: ");
    }

    /**
     * קורא בחירה מהשחקן.
     */
    private int getPlayerChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * מטפל בבחירת השחקן.
     */
    private void handleChoice(int choice) {
        // switch על choice וקרא למתודות המתאימות
        switch (choice) {
            case 1:
                viewCharacter();
                break;
            case 2:
                viewInventory();
                break;
            case 3:
                moveToLocation();
                break;
            case 4:
                visitShop();
                break;
            case 5:
                startBattle();
                break;
            case 6:
                gameRunning = false;
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    /**
     * מציג מידע על הדמות.
     */
    private void viewCharacter() {
        System.out.println("\n" + player.toString());
        System.out.println("Equipped Weapon: " + (player.getEquippedWeapon() != null ? player.getEquippedWeapon().getName() : "None"));
        System.out.println("Total Defense: " + player.getTotalDefense());
    }

    /**
     * מציג את המלאי של השחקן.
     */
    private void viewInventory() {
        // הצג את כל הפריטים במלאי
        ArrayList<Item> inv = player.getInventory();
        System.out.println("\n--- Inventory ---");
        for (int i = 0; i < inv.size(); i++) {
            System.out.println((i + 1) + ". " + inv.get(i).toString());
        }

        System.out.println("0. Back");
        System.out.print("Select item to equip/use: ");
        int choice = getPlayerChoice();

        if (choice > 0 && choice <= inv.size()) {
            Item item = inv.get(choice - 1);
            try {
                if (item instanceof Weapon) player.equipWeapon((Weapon) item);
                else if (item instanceof Armor) player.equipArmor((Armor) item);
                else if (item instanceof Potion) ((Potion) item).use(player);
                System.out.println("Action performed on " + item.getName());
            } catch (Exception e) {
                System.out.println("Cannot use item: " + e.getMessage());
            }
        }
    }

    /**
     * מאפשר לשחקן לזוז למיקום אחר.
     */
    private void moveToLocation() {

        // 1. הצג מיקומים נגישים
        ArrayList<GameLocation> locations = map.getAccessibleLocations();
        System.out.println("\n--- Travel ---");
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i+1) + ". " + locations.get(i).getName());
        }
        System.out.println("0. Cancel");

        // 2. בקש בחירה מהשחקן
        int choice = getPlayerChoice();

        // 3. הזז את השחקן
        if (choice > 0 && choice <= locations.size()) {
            try {
                map.moveTo(locations.get(choice-1).getId());
                System.out.println("Moved to " + map.getCurrentLocationId());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * מאפשר לשחקן לקנות ולמכור בחנות.
     */
    private void visitShop() {
        if (!map.getCurrentLocationId().equals("town")) {
            System.out.println("No shop here.");
            return;
        }

        boolean inShop = true;
        while (inShop) {
            System.out.println("\n--- Shop (Gold: " + player.getGold() + ") ---");
            System.out.println("1. Buy");
            System.out.println("2. Sell");
            System.out.println("3. Leave");
            int choice = getPlayerChoice();

            if (choice == 1) {
                ArrayList<Item> items = shop.getAvailableItems();
                for (int i = 0; i < items.size(); i++) {
                    System.out.println((i+1) + ". " + items.get(i).getName() + " - " + items.get(i).getBuyPrice() + "g");
                }
                int buyChoice = getPlayerChoice();
                if (buyChoice > 0 && buyChoice <= items.size()) {
                    try {
                        shop.buyItem(player, items.get(buyChoice-1).getName());
                        System.out.println("Bought item.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            } else if (choice == 2) {
                ArrayList<Item> inv = player.getInventory();
                for (int i = 0; i < inv.size(); i++) {
                    System.out.println((i+1) + ". " + inv.get(i).getName() + " - " + inv.get(i).getSellPrice() + "g");
                }
                int sellChoice = getPlayerChoice();
                if (sellChoice > 0 && sellChoice <= inv.size()) {
                    try {
                        shop.sellItem(player, inv.get(sellChoice-1).getName());
                        System.out.println("Sold item.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            } else {
                inShop = false;
            }
        }
    }

    /**
     * מתחיל קרב עם אויב.
     */
    private void startBattle() {
        // 1. צור אויב (דמות אקראית)
        Character enemy = new Warrior("Enemy Warrior");

        // 2. צור BattleSystem
        BattleSystem battle = new BattleSystem(player, enemy);

        // 3. הרץ את הקרב
        while (!battle.isBattleEnded()) {
            System.out.println("1. Attack 2. Defend 3. Flee");
            int action = getPlayerChoice();
            try {
                if (action == 1) battle.queuePlayerAction(BattleAction.ActionType.ATTACK);
                else if (action == 2) battle.queuePlayerAction(BattleAction.ActionType.DEFEND);
                else if (action == 3) battle.queuePlayerAction(BattleAction.ActionType.FLEE);

                if (!battle.isBattleEnded()) battle.queueAction(battle.generateEnemyAction());

                battle.processAllActions();
            } catch (Exception e) {
                System.out.println("Battle error: " + e.getMessage());
            }
        }
    }

    /**
     * נקודת הכניסה למשחק.
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}