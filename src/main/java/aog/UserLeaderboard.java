package aog;

public class UserLeaderboard {

    private final String username;
    private final int num_cards;
    private final int user_id;

    public UserLeaderboard(String username, int num_cards, int user_id) {
        this.username = username;
        this.num_cards = num_cards;
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public int getNumCards() {
        return num_cards;
    }

    public int getUserID() {
        return user_id;
    }
}
