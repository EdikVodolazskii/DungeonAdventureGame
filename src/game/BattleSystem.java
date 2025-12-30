package game;

import model.characters.Character;
import model.items.Item;
import model.items.Potion;
import model.exceptions.InvalidActionException;
import model.exceptions.ItemNotFoundException;

import java.util.*;

/**
 * מערכת הקרב של המשחק.
 * משתמשת ב-Queue לניהול תור הפעולות.
 */
public class BattleSystem {
    
    private Character player;
    private Character enemy;
    private Queue<BattleAction> actionQueue;
    private ArrayList<String> battleLog;
    private boolean battleEnded;
    private Character winner;
    
    public BattleSystem(Character player, Character enemy) {
        this.player = player;
        this.enemy = enemy;
        this.actionQueue = new LinkedList<>();
        this.battleLog = new ArrayList<>();
        this.battleEnded = false;
        this.winner = null;
        
        logMessage("Battle started: " + player.getName() + " vs " + enemy.getName());
    }
    
    // ============================================================
    // TODO: ניהול תור פעולות (Queue Management)
    // ============================================================
    
    /**
     * מוסיף פעולה לתור הפעולות.
     * 
     * @param action הפעולה להוספה
     * @throws InvalidActionException אם הקרב כבר הסתיים
     */
    public void queueAction(BattleAction action) throws InvalidActionException {
        if(battleEnded)
        {
            throw new InvalidActionException("queue", "the game is ended");
        }

        actionQueue.add(action);
    }
    
    /**
     * יוצר ומוסיף פעולה של השחקן.
     * 
     * @param actionType סוג הפעולה
     * @throws InvalidActionException אם הקרב הסתיים
     */
    public void queuePlayerAction(BattleAction.ActionType actionType) 
            throws InvalidActionException {
        if(battleEnded)
        {
            throw new InvalidActionException("queue player", "the game is ended");
        }
        BattleAction action= new BattleAction(player, enemy, actionType);
        queueAction(action);
    }
    
    /**
     * יוצר ומוסיף פעולה של שימוש בפריט.
     * 
     * @param itemName שם הפריט
     * @throws InvalidActionException אם הקרב הסתיים
     */
    public void queuePlayerItemAction(String itemName) throws InvalidActionException {
        if(battleEnded)
        {
            throw new InvalidActionException("queue item", "the game is ended");
        }

        BattleAction action = new BattleAction(player, enemy, BattleAction.ActionType.USE_ITEM, itemName);
        queueAction(action);

    }
    
    /**
     * יוצר פעולה אקראית לאויב (AI פשוט).
     * - 60% התקפה רגילה
     * - 25% יכולת מיוחדת
     * - 15% הגנה
     * 
     * @return פעולת האויב
     */
    public BattleAction generateEnemyAction() {
        Random rnd = new Random();
        int number = rnd.nextInt(0,101);
        BattleAction act = null;

        if(number<25)
        {
            act = new BattleAction(player, enemy, BattleAction.ActionType.DEFEND);
        } else if (number >= 25 && number<60) {
            act = new BattleAction(player, enemy, BattleAction.ActionType.SPECIAL);
        }
        else {
            act = new BattleAction(player, enemy, BattleAction.ActionType.ATTACK);
        }
        return act;
    }
    
    /**
     * מבצע את הפעולה הבאה בתור.
     * 
     * @return תיאור מה קרה, או null אם התור ריק
     */
    public String processNextAction() throws InvalidActionException {
        if (actionQueue.isEmpty()) {
            return null;
        }

        BattleAction action = actionQueue.poll();
        Character actor = action.getActor();
        Character target = action.getTarget();
        BattleAction.ActionType type = action.getActionType();

        if (!actor.isAlive()) {
            return processNextAction();
        }

        String resultMessage = "";

        try {
            switch (type) {
                case ATTACK:
                    int damage = executeAttack(actor, target);
                    resultMessage = String.format("%s attacked %s for %d damage.",
                            actor.getName(), target.getName(), damage);
                    break;

                case SPECIAL:
                    boolean specialSuccess = executeSpecialAbility(actor, target);
                    if (specialSuccess) {
                        resultMessage = String.format("%s used a Special Ability on %s!",
                                actor.getName(), target.getName());
                    } else {
                        resultMessage = String.format("%s tried to use Special Ability but failed (no mana/rage).",
                                actor.getName());
                    }
                    break;

                case DEFEND:
                    executeDefend(actor);
                    resultMessage = String.format("%s entered defensive stance.", actor.getName());
                    break;

                case USE_ITEM:
                    String itemName = action.getItemName();
                    boolean itemSuccess = executeUseItem(actor, itemName);
                    if (itemSuccess) {
                        resultMessage = String.format("%s used item: %s.", actor.getName(), itemName);
                    } else {
                        resultMessage = String.format("%s failed to use item.", actor.getName());
                    }
                    break;

                case FLEE:
                    boolean fleeSuccess = executeFlee(actor);
                    if (fleeSuccess) {
                        resultMessage = String.format("%s fled from battle!", actor.getName());
                        battleEnded = true;
                    } else {
                        resultMessage = String.format("%s tried to flee but failed!", actor.getName());
                    }
                    break;
            }
        } catch (ItemNotFoundException e) {
            resultMessage = String.format("%s tried to use an item but couldn't find it.", actor.getName());
        }

        checkBattleEnd();
        logMessage(resultMessage);
        return resultMessage;
    }
    
    /**
     * מבצע את כל הפעולות בתור עד שהוא מתרוקן או שהקרב נגמר.
     * 
     * @return רשימה של כל התיאורים של מה שקרה
     */
    public ArrayList<String> processAllActions() throws InvalidActionException {
        ArrayList<String> actions = new ArrayList<>();
        while (!actionQueue.isEmpty()&&!battleEnded)
            actions.add(processNextAction());
        return actions;
    }
    
    // ============================================================
    // TODO: ביצוע פעולות (Action Execution)
    // ============================================================
    
    /**
     * מבצע התקפה רגילה.
     * 
     * @param attacker התוקף
     * @param defender המותקף
     * @return הנזק שנגרם
     */
    private int executeAttack(Character attacker, Character defender) {
        int damage = attacker.calculateAttackDamage();
        defender.takeDamage(damage);
        return damage;
    }
    
    /**
     * מבצע יכולת מיוחדת.
     * 
     * @param actor המבצע
     * @param target היעד
     * @return true אם הצליח
     */
    private boolean executeSpecialAbility(Character actor, Character target) {
        return actor.useSpecialAbility(target);
    }
    
    /**
     * משתמש בפריט.
     * 
     * @param actor המשתמש
     * @param itemName שם הפריט
     * @return true אם הצליח
     * @throws ItemNotFoundException אם הפריט לא נמצא
     */
    private boolean executeUseItem(Character actor, String itemName)
            throws ItemNotFoundException {
        ArrayList<Item> inventory = actor.getInventory();
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                if (item instanceof Potion) {
                    Potion potion = (Potion) item;
                    if (potion.use(actor)) {
                        actor.pushRecentlyUsed(item);
                        return true;
                    }
                }
                return false;
            }
        }
        throw new ItemNotFoundException(itemName);
    }
    
    /**
     * מבצע פעולת הגנה - מפחית נזק בתור הבא ב-50%.
     * 
     * @param defender המגן
     */
    private void executeDefend(Character defender) {
        logMessage(defender.getName() + " is defending!");
    }
    
    /**
     * מנסה לברוח מהקרב.
     * סיכוי הצלחה = 30% + (רמת שחקן - רמת אויב) * 5%
     * 
     * @param fleeing הבורח
     * @return true אם ההבריחה הצליחה
     */
    private boolean executeFlee(Character fleeing) {
        Character opponent = (fleeing == player) ? enemy : player;

        double chance = 0.30 + (fleeing.getLevel() - opponent.getLevel()) * 0.05;

        if (chance < 0.0) chance = 0.0;
        if (chance > 1.0) chance = 1.0;

        return Math.random() < chance;
    }
    
    // ============================================================
    // TODO: בדיקת סיום קרב
    // ============================================================
    
    /**
     * בודק אם הקרב הסתיים וקובע מנצח.
     */
    private void checkBattleEnd() {
        if(!player.isAlive())
        {
            battleEnded = true;
            winner = enemy;
            logMessage(enemy.getName()+ " win");
        }
        else if(!enemy.isAlive())
        {
            battleEnded = true;
            winner = player;
            logMessage(player.getName()+ " win");
        }
    }
    
    // ============================================================
    // TODO: מיון פעולות לפי עדיפות (שימוש במחלקה אנונימית)
    // ============================================================
    
    /**
     * ממיין רשימת פעולות לפי עדיפות (גבוה לנמוך).
     * השתמש ב-Comparator כמחלקה אנונימית!
     * 
     * @param actions רשימת הפעולות למיון
     */
    public void sortActionsByPriority(ArrayList<BattleAction> actions) {
        actions.sort(new Comparator<BattleAction>() {
            @Override
            public int compare(BattleAction a1, BattleAction a2) {
                return Integer.compare(a2.getPriority(), a1.getPriority());
            }
        });
    }
    
    /**
     * מסנן פעולות לפי תנאי מסוים.
     * השתמש בממשק פונקציונלי!
     * 
     * @param actions רשימת הפעולות
     * @param filter הפילטר (ממשק עם מתודת test)
     * @return רשימה מסוננת
     */
    public ArrayList<BattleAction> getActionsFilteredBy(
            ArrayList<BattleAction> actions, ActionFilter filter) {

        ArrayList<BattleAction> filteredActions = new ArrayList<>();
        for(BattleAction action : actions)
        {
            if(filter.test(action))
            {
                filteredActions.add(action);
            }
        }
        return filteredActions;
    }
    
    /**
     * ממשק פונקציונלי לסינון פעולות.
     */
    public interface ActionFilter {
        boolean test(BattleAction action);
    }
    
    // ============================================================
    // Utility Methods
    // ============================================================
    
    private void logMessage(String message) {
        battleLog.add(message);
        System.out.println(message);
    }
    
    // Getters
    public Character getPlayer() {
        return player;
    }
    
    public Character getEnemy() {
        return enemy;
    }
    
    public boolean isBattleEnded() {
        return battleEnded;
    }
    
    public Character getWinner() {
        return winner;
    }
    
    public ArrayList<String> getBattleLog() {
        return new ArrayList<>(battleLog);
    }
    
    public int getQueueSize() {
        return actionQueue.size();
    }
    
    public boolean isQueueEmpty() {
        return actionQueue.isEmpty();
    }
}
