package MyCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static MyCard.CardGame.getValueFromRank;
import static MyCard.CardGame.sortCardsByValue;

public class Calculation {

    final static int STRAIGHT_SIZE = 5;

    public static int sumValues(List<Card> cards) {
        int sum = 0;
        for (Card card : cards) {
            sum += card.getValue();
        }
        return sum;
    }

    public static int handleSuits(List<Card> deck) {

        Map<String, List<Card>> suitCardsMapping = new HashMap<>();

        for (Card card : deck) {
            // If the suit doesn't exist in the map, add it with an empty list
            suitCardsMapping.putIfAbsent(card.suit, new ArrayList<>());

            // Add the card to the list corresponding to the suit
            suitCardsMapping.get(card.suit).add(card);
        }

        int totalPoints = 0;
//        System.out.println("Printing the dictionary:");
        for (Map.Entry<String, List<Card>> entry : suitCardsMapping.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
            List<Card> suitCards = entry.getValue();
            if (suitCards.size() >= 5) {
                System.out.println("Handle Suits found: " + entry.getKey() + " " + entry.getValue());
                totalPoints += sumValues(suitCards) * 6;
                // We will need to exclude them once, as we alreadu counted them
                totalPoints -= sumValues(suitCards);
            }
        }

        return totalPoints;
    }

    public static int handleStraights(List<Card> deck) {

        if (deck.size() > (STRAIGHT_SIZE * 2)) {
            throw new RuntimeException("Size greater than 10");
        }

        // Get a sorted list.
        List<Card> deckCopy = new ArrayList<Card>(deck);
        sortCardsByValue(deckCopy);

        // Detect a straight and store the straight elements
        List<Card> straight = new ArrayList<>();
        straight.add(deckCopy.getFirst()); // Start with the first card

        for (int i = 1; i < deckCopy.size(); i++) {
            Card current = deckCopy.get(i);
            Card previous = deckCopy.get(i - 1);

            if (current.getValue() == previous.getValue() + 1) {
                straight.add(current); // Add to straight if it's consecutive
            } else if (current.getValue() != previous.getValue()) {
                // Break the straight if there's a gap
                if (straight.size() >= STRAIGHT_SIZE) {
                    break; // Exit the loop if a valid straight is found
                }
                straight.clear(); // Reset the straight if interrupted
                straight.add(current); // Start a new straight
            }
        }

        // Check the last straight in the deck
        int totalPoints = 0;
        if (straight.size() >= STRAIGHT_SIZE) {
            System.out.println("Straight found: " + straight);
            totalPoints += sumValues(straight) * STRAIGHT_SIZE;
            // We will need to exclude them once, as we already count them.
            totalPoints -= sumValues(straight);
        }

        return totalPoints;
    }

    public static int handleKinds(List<Card> deck) {
        /*
        We will always count the value, even if they are occuring only once.
        E.g.,
        rankCounts = { 9: 2, A: 1 }
        9 + 9 + 14 = 32
         */
        Map<String, Integer> rankCounts = new HashMap<>();

        for (Card card : deck) {
            rankCounts.put(card.rank, rankCounts.getOrDefault(card.rank, 0) + 1);
        }

        int totalPoints = 0;
        for (Map.Entry<String, Integer> entry : rankCounts.entrySet()) {
            if (entry.getValue() == 2) {
                System.out.println("Pair of Cards found");
            } else if (entry.getValue() == 3) {
                System.out.println("Three of a Kind found");
            } else if (entry.getValue() == 4) {
                System.out.println("Four of a Kind found");
            }
            // We will add and multiply with occurrance regardless
            totalPoints += getValueFromRank(entry.getKey()) * entry.getValue() * entry.getValue();
        }
        return totalPoints;
    }
}
