package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class BuckshotRouletteUI extends JFrame {

    Random rnd = new Random();
    Shotgun gun = new Shotgun();
    Player me;
    Dealer enemy;
    GameState gs;
    ArrayList<Item> myItems;
    ArrayList<Item> enemyItems;

    int myScore = 0;
    int enemyScore = 0;
    int roundNum = 1;

    JLabel lblMyHp;
    JLabel lblEnemyHp;
    JLabel lblGunInfo;
    JLabel lblRound;
    JTextArea txtLog;
    JPanel pnlItems;
    JButton btnShootEnemy;
    JButton btnShootMe;

    public BuckshotRouletteUI() {
        setTitle("Buckshot Roulette");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlTop = new JPanel(new GridLayout(4, 1));
        lblRound    = new JLabel("", SwingConstants.CENTER);
        lblMyHp     = new JLabel("", SwingConstants.CENTER);
        lblEnemyHp  = new JLabel("", SwingConstants.CENTER);
        lblGunInfo  = new JLabel("", SwingConstants.CENTER);
        pnlTop.add(lblRound);
        pnlTop.add(lblMyHp);
        pnlTop.add(lblEnemyHp);
        pnlTop.add(lblGunInfo);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        JScrollPane sp = new JScrollPane(txtLog);

        pnlItems = new JPanel(new FlowLayout());
        JPanel pnlItemBox = new JPanel(new BorderLayout());
        pnlItemBox.add(new JLabel("보유 아이템:"), BorderLayout.NORTH);
        pnlItemBox.add(pnlItems, BorderLayout.CENTER);

        btnShootEnemy = new JButton("딜러에게 발사");
        btnShootMe    = new JButton("나 자신에게 발사");
        btnShootEnemy.addActionListener(e -> shoot(false));
        btnShootMe.addActionListener(e -> shoot(true));

        JPanel pnlBtns = new JPanel(new FlowLayout());
        pnlBtns.add(btnShootMe);
        pnlBtns.add(btnShootEnemy);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.add(pnlItemBox, BorderLayout.CENTER);
        pnlBottom.add(pnlBtns,   BorderLayout.SOUTH);

        add(pnlTop,    BorderLayout.NORTH);
        add(sp,        BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);

        initRound(roundNum);
        setVisible(true);
    }

    void initRound(int r) {
        GameState.currentRound = r;
        int maxHp = 3 + r;

        me    = new Player("플레이어", maxHp);
        enemy = new Dealer(maxHp);
        gs    = new GameState(gun, me, enemy);

        int live  = 2 + rnd.nextInt(r + 1);
        int blank = 2 + rnd.nextInt(r + 1);
        gun.load(live, blank);

        myItems    = makeItems(2 + r);
        enemyItems = makeItems(2 + r);

        refreshLabels();
        refreshItemBtns();

        printLog("=== 라운드 " + r + " 시작 ===");
        printLog("실탄 " + live + "발 + 공포탄 " + blank + "발");
    }

    void shoot(boolean toMe) {
        if (!gs.isPlayerTurn()) return;

        Damageable t = toMe ? gs.getPlayer() : gs.getDealer();
        String res = gs.getShotgun().shoot(t);
        printLog("발사! → " + res);
        refreshLabels();

        if (isRoundOver()) return;
        if (gs.getShotgun().isEmpty()) reload();

        boolean keepTurn = toMe && res.contains("공포탄");
        if (!keepTurn) {
            gs.switchTurn();
            runDealerTurn();
        }
    }

    void runDealerTurn() {
        while (!gs.isPlayerTurn() && me.isAlive() && enemy.isAlive()) {
            printLog("--- 딜러 턴 ---");

            if (!enemyItems.isEmpty() && rnd.nextBoolean()) {
                Item it = enemyItems.remove(0);
                printLog("딜러 [" + it.getName() + "] 사용!");
                it.use(gs);
                refreshLabels();
            }

            if (gs.getShotgun().isEmpty()) reload();

            boolean toSelf = gs.getDealer().decideSelfShoot(gs);
            Damageable t   = toSelf ? gs.getDealer() : gs.getPlayer();
            printLog(toSelf ? "딜러 자신에게 발사!" : "딜러가 나에게 발사!");

            String res = gs.getShotgun().shoot(t);
            printLog("발사! → " + res);
            gs.getDealer().clearKnowledge();
            refreshLabels();

            if (isRoundOver()) return;
            if (gs.getShotgun().isEmpty()) reload();

            boolean keepTurn = toSelf && res.contains("공포탄");
            if (!keepTurn) gs.switchTurn();
        }
    }

    boolean isRoundOver() {
        if (me.isAlive() && enemy.isAlive()) return false;

        String winner = me.isAlive() ? "플레이어" : "딜러";
        if (winner.equals("플레이어")) myScore++;
        else enemyScore++;

        printLog("=== 라운드 " + roundNum + " 종료! 승자: " + winner + " ===");
        btnShootEnemy.setEnabled(false);
        btnShootMe.setEnabled(false);

        if (roundNum < 3) {
            JOptionPane.showMessageDialog(this, winner + " 승리!", "라운드 종료", JOptionPane.INFORMATION_MESSAGE);
            roundNum++;
            btnShootEnemy.setEnabled(true);
            btnShootMe.setEnabled(true);
            initRound(roundNum);
        } else {
            String result;
            if (myScore > enemyScore)      result = "최종 승자: 플레이어!";
            else if (enemyScore > myScore) result = "최종 승자: 딜러!";
            else                           result = "무승부!";
            JOptionPane.showMessageDialog(this, "플레이어 " + myScore + "승  딜러 " + enemyScore + "승\n" + result, "게임 종료", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        return true;
    }

    void reload() {
        int l = 1 + rnd.nextInt(3);
        int b = 1 + rnd.nextInt(3);
        gun.load(l, b);
        myItems.addAll(makeItems(1));
        printLog("재장전! 실탄 " + l + " 공포탄 " + b);
        refreshItemBtns();
        refreshLabels();
    }

    void refreshItemBtns() {
        pnlItems.removeAll();
        for (int i = 0; i < myItems.size(); i++) {
            final int idx = i;
            JButton btn = new JButton(myItems.get(i).getName());
            btn.addActionListener(e -> useItem(idx));
            pnlItems.add(btn);
        }
        pnlItems.revalidate();
        pnlItems.repaint();
    }

    void useItem(int idx) {
        if (!gs.isPlayerTurn() || idx >= myItems.size()) return;
        Item it = myItems.remove(idx);
        printLog("[" + it.getName() + "] 사용! " + it.getDescription());
        it.use(gs);

        if (it instanceof Magnifier) {
            printLog("다음 탄 확인: [" + gun.peekNext() + "]");
        }

        refreshLabels();
        refreshItemBtns();
    }

    void refreshLabels() {
        lblRound.setText("라운드 " + roundNum + " / 3   나 " + myScore + "승  딜러 " + enemyScore + "승");
        lblMyHp.setText("내 HP: "     + me.getHP()    + " / " + me.getMaxHP());
        lblEnemyHp.setText("딜러 HP: " + enemy.getHP() + " / " + enemy.getMaxHP());
        lblGunInfo.setText("총: "      + gun.getStatus());
    }

    void printLog(String msg) {
        txtLog.append(msg + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    ArrayList<Item> makeItems(int n) {
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int r = rnd.nextInt(5);
            if      (r == 0) list.add(new Beer());
            else if (r == 1) list.add(new Magnifier());
            else if (r == 2) list.add(new Saw());
            else if (r == 3) list.add(new Cigarette());
            else             list.add(new Handcuff());
        }
        return list;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BuckshotRouletteUI());
    }
}