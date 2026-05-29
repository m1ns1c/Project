package test;

import java.util.*;

public class Shotgun {

    public static class Chamber {
        private ArrayList<String> shells;
        private int liveCount;
        private int blankCount;

        public Chamber(int live, int blank) {
            this.liveCount = live;
            this.blankCount = blank;
            shells = new ArrayList<>();
            for (int i = 0; i < live; i++) {
                shells.add("실탄");
            }
            for (int i = 0; i < blank; i++) {
                shells.add("공포탄");
            }
            Collections.shuffle(shells, new Random());
        }

        public String ejectNext() {
            if (shells.size() == 0) return "없음";
            String s = shells.remove(0);
            if (s.equals("실탄")) liveCount--;
            else blankCount--;
            return s;
        }

        public String peekNext() {
            if (shells.size() == 0) return "없음";
            return shells.get(0);
        }

        public boolean isEmpty() { return shells.size() == 0; }
        public int getLiveCount() { return liveCount; }
        public int getBlankCount() { return blankCount; }
    }

    private Chamber chamber;
    private boolean sawed;
    private static int shotsFired = 0;

    public Shotgun() {
        this.sawed = false;
    }

    public void load(int live, int blank) {
        chamber = new Chamber(live, blank);
        sawed = false;
        System.out.println("약실 장전: 실탄 " + live + "발 + 공포탄 " + blank + "발");
    }

    public String shoot(Damageable target) {
        return shoot(target, false);
    }

    public String shoot(Damageable target, boolean forceSawed) {
        if (chamber == null || chamber.isEmpty()) return "불발 (탄 없음)";
        String shell = chamber.ejectNext();
        shotsFired++;
        boolean isSawed = sawed || forceSawed;
        if (shell.equals("실탄")) {
            int dmg = 1;
            if (isSawed) dmg = 2;
            target.takeDamage(dmg);
            sawed = false;
            if (isSawed) return "실탄! (톱질 x2) -> " + target.getName() + " -" + dmg + "HP";
            else return "실탄! -> " + target.getName() + " -" + dmg + "HP";
        } else {
            sawed = false;
            return "공포탄! -> " + target.getName() + " 무사";
        }
    }

    public String ejectNext() {
        if (chamber == null) return "없음";
        return chamber.ejectNext();
    }

    public String peekNext() {
        if (chamber == null) return "없음";
        return chamber.peekNext();
    }

    public boolean isEmpty() {
        if (chamber == null) return true;
        return chamber.isEmpty();
    }

    public int getLiveCount() {
        if (chamber == null) return 0;
        return chamber.getLiveCount();
    }

    public int getBlankCount() {
        if (chamber == null) return 0;
        return chamber.getBlankCount();
    }

    public void setSawed(boolean b) { this.sawed = b; }
    public boolean isSawed() { return sawed; }
    public static int getShotsFired() { return shotsFired; }

    public String getStatus() {
        if (chamber == null) return "장전 안 됨";
        String status = "실탄 " + chamber.getLiveCount() + "발 / 공포탄 " + chamber.getBlankCount() + "발 남음";
        if (sawed) status = status + " [톱질됨]";
        return status;
    }
}
