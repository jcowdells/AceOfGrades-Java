package aog;

public class UserStats {
    private final int num_cards;
    private final int num_packs;
    private final int num_attempts;
    private final int num_correct;
    private final int best_card;
    private final int worst_card;

    public UserStats(int num_cards, int num_packs, int num_attempts, int num_correct, int best_card, int worst_card) {
        this.num_cards = num_cards;
        this.num_packs = num_packs;
        this.num_attempts = num_attempts;
        this.num_correct = num_correct;
        this.best_card = best_card;
        this.worst_card = worst_card;
    }

    public int getNumCards() {
        return num_cards;
    }

    public int getNumPacks() {
        return num_packs;
    }

    public int getNumAttempts() {
        return num_attempts;
    }

    public int getNumCorrect() {
        return num_correct;
    }

    public int getBestCard() {
        return best_card;
    }

    public int getWorstCard() {
        return worst_card;
    }
}
