package MyCard;

import java.util.List;

class Player {
    int playerNum;
    List<Card> hand;
    int wins;
    int points;

    public Player(int playerNum) {
        this.playerNum = playerNum;
        this.hand = null;
        this.wins = 0;
        this.points = 0;
    }

    public void setHand(List<Card> deck) {
        this.hand = deck;
    }

    @Override
    public String toString() {
        return "Player " + playerNum + ": (hand: " + hand + ")";
    }
}