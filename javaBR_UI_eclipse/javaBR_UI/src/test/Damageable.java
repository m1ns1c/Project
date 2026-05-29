package test;

public interface Damageable {
    void takeDamage(int amount);
    void heal(int amount);
    int getHP();
    int getMaxHP();
    boolean isAlive();
    String getName();
}

class Player implements Damageable {
    private String name;
    private int hp;
    private int maxHp;

    public Player(String name, int maxHp) {
        this.name = name;
        this.hp = maxHp;
        this.maxHp = maxHp;
    }

    @Override
    public void takeDamage(int amount) {
        hp = hp - amount;
        if (hp < 0) hp = 0;
    }

    @Override
    public void heal(int amount) {
        hp = hp + amount;
        if (hp > maxHp) hp = maxHp;
    }

    @Override 
    public int getHP() { return hp; }
    @Override 
    public int getMaxHP() { return maxHp; }
    @Override 
    public boolean isAlive() { return hp > 0; }
    @Override 
    public String getName() { return name; }
}

class Dealer implements Damageable {
    private String name;
    private int hp;
    private int maxHp;
    private boolean knowsNextShell;
    private String knownNextShell;

    public Dealer(int maxHp) {
        this.name = "딜러";
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.knowsNextShell = false;
        this.knownNextShell = "";
    }

    @Override
    public void takeDamage(int amount) {
        hp = hp - amount;
        if (hp < 0) hp = 0;
    }

    @Override
    public void heal(int amount) {
        hp = hp + amount;
        if (hp > maxHp) hp = maxHp;
    }

    @Override 
    public int getHP() { return hp; }
    @Override 
    public int getMaxHP() { return maxHp; }
    @Override 
    public boolean isAlive() { return hp > 0; }
    @Override 
    public String getName() { return name; }

    public boolean decideSelfShoot(GameState state) {
        int live = state.getShotgun().getLiveCount();
        int blank = state.getShotgun().getBlankCount();
        int total = live + blank;
        
        if (total == 0) return false;
        
        if (knowsNextShell && knownNextShell.equals("공포탄")) return true;
        
        if ((double) blank / total >= 0.7) return true;
        
        return false;
    }

    public void setKnownNextShell(String shell) {
        this.knowsNextShell = true;
        this.knownNextShell = shell;
    }

    public void clearKnowledge() {
        this.knowsNextShell = false;
        this.knownNextShell = "";
    }
}
