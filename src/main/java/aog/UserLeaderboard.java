package aog;

public class UserLeaderboard {

    private final String username;
    private final int num_cards;

    public UserLeaderboard(String username, int num_cards) {
        this.username = username;
        this.num_cards = num_cards;
    }

    public String getUsername() {
        return username;
    }

    public int getNumCards() {
        return num_cards;
    }
}
