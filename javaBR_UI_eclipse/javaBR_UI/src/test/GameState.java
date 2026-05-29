package test;

public class GameState {
    private Shotgun shotgun;
    private Player player;
    private Dealer dealer;
    private boolean playerTurn;
    private boolean skipNextTurn;

    public static int currentRound = 0;

    public GameState(Shotgun shotgun, Player player, Dealer dealer) {
        this.shotgun = shotgun;
        this.player = player;
        this.dealer = dealer;
        this.playerTurn = true;
        this.skipNextTurn = false;
    }

    public Shotgun getShotgun() { return shotgun; }
    
    public Player getPlayer() { return player; }
    
    public Dealer getDealer() { return dealer; }
    
    public boolean isPlayerTurn() { return playerTurn; }
    
    public boolean isSkipNextTurn() { return skipNextTurn; }
    
    public void setSkipNextTurn(boolean b) { skipNextTurn = b; }

    public void switchTurn() {
        playerTurn = !playerTurn;
    }
}
