package MyCard;

import java.util.List;
//player representation
class Player {
    int playerNum; // player id
    List<Card> hand; // player deck of cards
    int wins; //number of wins
    int points; //points for players

    public Player(int playerNum) {
        this.playerNum = playerNum;
        this.hand = null;
        this.wins = 0;
        this.points = 0;
    }
    @Override
    public String toString() {
        return "Player " + playerNum + ": (hand: " + hand + ")";
    }
}