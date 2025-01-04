package MyCard;

import static MyCard.CardGame.getValueFromRank;

public class Card {
    String rank;
    String suit;
    int value;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
        this.value = getValueFromRank(rank);
    }

    @Override
    public String toString() {
        return "[" + this.suit + "<" + this.rank + ">]";
    }

    public int getValue() {
        return this.value;
    }

    public String getImagePath() {
        return "images/" + suit.toLowerCase() + "_" + rank + ".png";
    }
}
