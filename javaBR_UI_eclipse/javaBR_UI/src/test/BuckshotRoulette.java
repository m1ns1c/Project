package test;

import java.util.*;

public class BuckshotRoulette {

    interface RoundEndEvent {
        void onRoundEnd(String winner, int round);
    }

    static Scanner sc = new Scanner(System.in);
    static Random random = new Random();
    static int playerWin = 0;
    static int dealerWin = 0;

    public static void main(String[] args) {
        System.out.println("===== BUCKSHOT ROULETTE =====");
        System.out.println("실탄과 공포탄이 섞인 산탄총으로 딜러를 쓰러뜨려라!");
        System.out.println("3라운드 중 더 많이 이긴 쪽이 승리!");
        System.out.println("=============================");
        System.out.print("Enter를 눌러 시작...");
        sc.nextLine();

        Shotgun shotgun = new Shotgun();

        RoundEndEvent roundEndHandler = new RoundEndEvent() {
            @Override
            public void onRoundEnd(String winner, int round) {
                System.out.println("라운드 " + round + " 종료! 승자: " + winner);
                System.out.println("현재 스코어 -> 플레이어: " + playerWin + "승 | 딜러: " + dealerWin + "승");
            }
        };

        for (int round = 1; round <= 3; round++) {
            GameState.currentRound = round;
            System.out.println("\n=============================");
            System.out.println("         라운드 " + round + " 시작!");
            System.out.println("=============================");

            int maxHp = 3 + round;
            Player player = new Player("플레이어", maxHp);
            Dealer dealer = new Dealer(maxHp);
            GameState state = new GameState(shotgun, player, dealer);

            String winner = playRound(state);
            if (winner.equals("플레이어")) playerWin++;
            else dealerWin++;

            roundEndHandler.onRoundEnd(winner, round);

            if (round < 3) {
                System.out.print("Enter를 눌러 다음 라운드로...");
                sc.nextLine();
            }
        }

        System.out.println("\n=============================");
        System.out.println("          게임 종료!");
        System.out.println("=============================");
        System.out.println("플레이어 승: " + playerWin + "라운드");
        System.out.println("딜러     승: " + dealerWin + "라운드");
        System.out.println("총 발사 횟수: " + Shotgun.getShotsFired() + "발");
        if (playerWin > dealerWin) System.out.println("최종 승자: 플레이어!");
        else if (dealerWin > playerWin) System.out.println("최종 승자: 딜러!");
        else System.out.println("무승부!");
        sc.close();
    }

    static String playRound(GameState state) {
        int live = 2 + random.nextInt(GameState.currentRound + 1);
        int blank = 2 + random.nextInt(GameState.currentRound + 1);
        state.getShotgun().load(live, blank);

        ArrayList<Item> playerItems = generateItems(2 + GameState.currentRound);
        ArrayList<Item> dealerItems = generateItems(2 + GameState.currentRound);

        System.out.print("지급 아이템: ");
        for (int i = 0; i < playerItems.size(); i++) {
            System.out.print("[" + playerItems.get(i).getName() + "] ");
        }
        System.out.println();

        while (state.getPlayer().isAlive() && state.getDealer().isAlive()) {
            if (state.getShotgun().isEmpty()) {
                int l = 1 + random.nextInt(3);
                int b = 1 + random.nextInt(3);
                state.getShotgun().load(l, b);
                playerItems.addAll(generateItems(1));
                System.out.print("재장전! 아이템 추가 지급: ");
                for (int i = 0; i < playerItems.size(); i++) {
                    System.out.print("[" + playerItems.get(i).getName() + "] ");
                }
                System.out.println();
            }

            printStatus(state);

            boolean selfShot;
            if (state.isPlayerTurn()) {
                if (state.isSkipNextTurn()) {
                    System.out.println("플레이어 턴 스킵! 수갑에 묶였다");
                    state.setSkipNextTurn(false);
                    state.switchTurn();
                    continue;
                }
                selfShot = playerTurn(state, playerItems);
            } else {
                if (state.isSkipNextTurn()) {
                    System.out.println("딜러 턴 스킵! 수갑에 묶였다");
                    state.setSkipNextTurn(false);
                    state.switchTurn();
                    continue;
                }
                selfShot = dealerTurn(state, dealerItems);
            }

            if (!selfShot) state.switchTurn();
        }

        if (state.getPlayer().isAlive()) return "플레이어";
        else return "딜러";
    }

    static boolean playerTurn(GameState state, ArrayList<Item> items) {
        System.out.println("\n-- 당신의 턴 --");
        System.out.println("총 상태: " + state.getShotgun().getStatus());

        if (items.size() > 0) {
            System.out.println("아이템 목록:");
            for (int i = 0; i < items.size(); i++) {
                System.out.println((i + 1) + ". [" + items.get(i).getName() + "] " + items.get(i).getDescription());
            }
            System.out.println("0. 아이템 사용 안 함");
            System.out.print("아이템 선택 (번호 입력): ");
            int choice = inputInt(0, items.size());
            if (choice > 0) {
                Item chosen = items.remove(choice - 1);
                System.out.println(chosen.getName() + " 사용!");
                chosen.use(state);
            }
        }

        System.out.println("누구에게 발사?");
        System.out.println("1. 딜러에게");
        System.out.println("2. 나 자신에게");
        System.out.print("선택: ");
        int target = inputInt(1, 2);

        boolean selfShot = false;
        Damageable targetObj;
        if (target == 1) {
            targetObj = state.getDealer();
        } else {
            targetObj = state.getPlayer();
            selfShot = true;
        }

        String result = state.getShotgun().shoot(targetObj);
        System.out.println("발사! -> " + result);

        if (selfShot && result.contains("공포탄")) return true;
        return false;
    }

    static boolean dealerTurn(GameState state, ArrayList<Item> items) {
        System.out.println("\n-- 딜러의 턴 --");

        if (items.size() > 0 && random.nextBoolean()) {
            Item item = items.remove(0);
            System.out.println("딜러가 [" + item.getName() + "] 사용!");
            item.use(state);
        }

        boolean selfShot = state.getDealer().decideSelfShoot(state);
        Damageable targetObj;
        if (selfShot) {
            targetObj = state.getDealer();
            System.out.println("딜러가 자신에게 발사!");
        } else {
            targetObj = state.getPlayer();
            System.out.println("딜러가 플레이어에게 발사!");
        }

        String result = state.getShotgun().shoot(targetObj);
        System.out.println("발사! -> " + result);
        state.getDealer().clearKnowledge();

        if (selfShot && result.contains("공포탄")) return true;
        return false;
    }

    static ArrayList<Item> generateItems(int count) {
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int r = random.nextInt(5);
            if (r == 0) list.add(new Beer());
            else if (r == 1) list.add(new Magnifier());
            else if (r == 2) list.add(new Saw());
            else if (r == 3) list.add(new Cigarette());
            else list.add(new Handcuff());
        }
        return list;
    }

    static void printStatus(GameState state) {
        System.out.println("\n-----------------------------");
        System.out.println("플레이어 HP: " + state.getPlayer().getHP() + "/" + state.getPlayer().getMaxHP());
        System.out.println("딜러     HP: " + state.getDealer().getHP() + "/" + state.getDealer().getMaxHP());
        System.out.println("총 상태: " + state.getShotgun().getStatus());
        System.out.println("-----------------------------");
    }

    static int inputInt(int min, int max) {
        while (true) {
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val >= min && val <= max) return val;
                System.out.print(min + "~" + max + " 사이 숫자를 입력하세요: ");
            } catch (NumberFormatException e) {
                System.out.print("숫자를 입력하세요: ");
            }
        }
    }
}
