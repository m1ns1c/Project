package test;

public abstract class Item {
    protected String name;
    protected String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void use(GameState state);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

class Beer extends Item {
    public Beer() {
        super("맥주", "약실의 다음 탄을 버린다");
    }

    @Override
    public void use(GameState state) {
        String result = state.getShotgun().ejectNext();
        System.out.println("맥주를 마시고 탄을 배출! -> " + result);
    }
}

class Magnifier extends Item {
    public Magnifier() {
        super("돋보기", "다음 탄이 실탄인지 공포탄인지 확인한다");
    }

    @Override
    public void use(GameState state) {
        String next = state.getShotgun().peekNext();
        System.out.println("돋보기로 확인! 다음 탄: [" + next + "]");
    }
}

class Saw extends Item {
    public Saw() {
        super("톱", "총신을 잘라 이번 발사 데미지를 2배로");
    }

    @Override
    public void use(GameState state) {
        state.getShotgun().setSawed(true);
        System.out.println("총신을 톱으로 잘랐다! 다음 발사 데미지 2배!");
    }
}

class Cigarette extends Item {
    public Cigarette() {
        super("담배", "한 대 피우고 HP를 1 회복한다");
    }

    @Override
    public void use(GameState state) {
        if (state.isPlayerTurn()) {
            state.getPlayer().heal(1);
            System.out.println("담배를 피우고 HP 1 회복! (플레이어 HP: " + state.getPlayer().getHP() + ")");
        } else {
            state.getDealer().heal(1);
            System.out.println("딜러가 담배를 피우고 HP 1 회복! (딜러 HP: " + state.getDealer().getHP() + ")");
        }
    }
}

class Handcuff extends Item {
    public Handcuff() {
        super("수갑", "상대방의 다음 턴을 스킵시킨다");
    }

    @Override
    public void use(GameState state) {
        state.setSkipNextTurn(true);
        System.out.println("수갑을 채웠다! 상대방의 다음 턴이 스킵된다!");
    }
}
