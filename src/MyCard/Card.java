package MyCard;

import static MyCard.CardGame.getValueFromRank;
//card representation
public class Card {
    //each card has a rank suit and value
    String rank;
    String suit;
    int value;
// we initialize with rank and suit
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
        this.value = getValueFromRank(rank); // we retrieve the value automatically
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
