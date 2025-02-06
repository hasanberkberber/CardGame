package MyCard;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;



public class CardGame {

    List<Card> deck; //all 52 cards
    List<Card> deskCards; //3 cards at the middle
    //player represantations
    Player player1;
    Player player2;
    Player currentPlayer;

    boolean gameOver; //checks if the game is over
    boolean roundOver; // checks if the round is over
    int numRoundsPlayed; //amount of rounds we playewd
    int numPlayersPlayedInRound; // number of moves in a single round

    // gui related components
    private JFrame frame;
    private JLabel[] player1Labels, player2Labels, deskLabels;
    private JLabel player1PointsLabel;
    private JLabel player2PointsLabel;
    private JLabel currentPlayerText;
    private JLabel player1winsText;
    private JLabel player2winsText;
    private JCheckBox[] player1CardCheckboxes;
    private JCheckBox[] player2CardCheckboxes;
    private JButton player1ChangeButton;
    private JButton player2ChangeButton;
    private JButton nextRoundButton;


    //customazible game variables
    private static final int USER_DECK_SIZE = 5;
    private static final int DESK_DECK_SIZE = 3;
    int NUM_ROUNDS_TO_WIN = 3;

    //card attributes
    public static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
    public static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    public static final int[] VALUES = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

    //sorts the card by value using a comparator
    public static void sortCardsByValue(List<Card> cards) {
        cards.sort(Comparator.comparingInt(Card::getValue));
    }

    // Method to print all cards in the list
    public static void printCards(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            System.out.println("No Cards in Deck");
            return;
        }
        for (Card card : cards) {
            System.out.println(card);
        }
    }
    //this is for my debugging purpose
    public static void debugCheckboxes(JCheckBox[] checkBoxes) {
        // Print the list of selected states
        System.out.print("[");
        for (int i = 0; i < checkBoxes.length; i++) {
            System.out.print(checkBoxes[i].isSelected());
            if (i < checkBoxes.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }


    // Method to get a random card from a List<Card>
    public static Card getRandomCard(List<Card> deck) {
        Random random = new Random();
        int cardIndex = random.nextInt(deck.size()); // Use size() for List
        return deck.get(cardIndex); // Use get() method for List
    }
    //picks a card and removes it from the deck
    public static Card pickCard(List<Card> deck) {
        if (deck.isEmpty()) {
            throw new IllegalStateException("The deck is empty, no card to pick.");
        }

        Card card = getRandomCard(deck);
        deck.remove(card);
        return card;
    }

    // prepares a deck and returnes in shuffled form
    public static List<Card> prepareCards() {
        List<Card> cards = new ArrayList<>(); // ArrayList to hold the deck of cards

        // Loop through the suits and ranks to populate the deck
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                // Create a new card for each combination of rank and suit
                cards.add(new Card(rank, suit)); // Add the card to the list
            }
        }
        Collections.shuffle(cards);
        return cards;
    }
    //distributes the card to the desk and players (helper functions)
    public static List<Card> distributeCards(List<Card> cards, int deckSize) {
        ArrayList<Card> distCards = new ArrayList<>();
        for (int i = 0; i < deckSize; i++) {
            distCards.add(pickCard(cards));
        }
        return distCards;
    }

    //calling the distributed cards for the player
    public static List<Card> distributeCardsPlayer(List<Card> cards) {
        return distributeCards(cards, USER_DECK_SIZE);
    }
    //calling the distributed cards for the desk
    public static List<Card> distributeCardsDesk(List<Card> cards) {
        return distributeCards(cards, DESK_DECK_SIZE);
    }

    // Helper method to find the value for a given rank
    public static int getValueFromRank(String rank) {
        int index = Arrays.asList(RANKS).indexOf(rank); // Get the index of the rank
        if (index == -1) {
            throw new IllegalArgumentException("Invalid rank: " + rank);
        }
        return VALUES[index]; // Use the index to get the value
    }

    //here we calculate the points for player as requested in assingment
    public static void calculatePoints(Player player) {
        List<Card> deck = player.hand;//we get the players hand
        int step1 = Calculation.handleKinds(deck);//checking if two kind/three kind/ four kind exists
        int step2 = Calculation.handleStraights(deck);//checks if the there are consecutive card
        int step3 = Calculation.handleSuits(deck);//checks suits

        int totalPoints = step1 + step2 + step3;//returtns total
        player.points = totalPoints;
    }
    //change button action listener
    //whenever we click on change button it triggers this function
    public void handleChange() {

        if (deck.size() < 5) {
            JOptionPane.showMessageDialog(frame, "Not enough cards in the deck!");
            return;
        }
//we check the checkboxes
        for (int i = 0; i < 5; i++) {
            JCheckBox checkBox;
            if (currentPlayer == player1) {
                checkBox = player1CardCheckboxes[i];
            } else {
                checkBox = player2CardCheckboxes[i];
            }
            if (checkBox.isSelected()) {
                // gets the card from deck and inserts it
                currentPlayer.hand.set(i, deck.remove(0));
            }
        }

        calculatePoints(currentPlayer);
        showCardImagesPlayer(currentPlayer);
        switchPlayerTurn();
        calculatePoints(currentPlayer);
        showCardImagesPlayer(currentPlayer);

        // increments our check variable
        //if that variable equals two then round is over
        numPlayersPlayedInRound++;
        if (numPlayersPlayedInRound == 2) {
            roundOver = true;
            showCardImagesDesk();
            updatePlayerDeckWithDeskCards();
            updateWins();
            checkGameOver();
            switchPlayerTurn();
            numRoundsPlayed++;
            nextRoundButton.setEnabled(true);
            player1ChangeButton.setEnabled(false);
            player2ChangeButton.setEnabled(false);

        }
//we update the game for the changes we done
        updateUI();
    }
//at the end of game it adds the cards at the desk to players hand
    public void updatePlayerDeckWithDeskCards() {
        System.out.println("Updating Player Deck to 8");
        for (Card card : deskCards) {
            player1.hand.add(card);
            player2.hand.add(card);
        }
        System.out.println("player 1 hand size" + player1.hand.size());
        System.out.println("player 2 hand size" + player2.hand.size());
        calculatePoints(player1);
        calculatePoints(player2);
    }
//updates the win counts and ui labels
    public void updateWins() {
        System.out.println("updateWins");
        System.out.println("player 1 hand size" + player1.hand.size());
        System.out.println("player 2 hand size" + player2.hand.size());
        if(player1.points > player2.points) {
            player1.wins++;
        } else if (player2.points > player1.points) {
            player2.wins++;
        }
        setPlayerWinsLabelTexts();
    }

    public void setupGUI() {
        frame = new JFrame("Card Game");//creating the new frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setResizable(false);
        frame.setLayout(null); // Disable layout manager

        // Initializing
        player1CardCheckboxes = new JCheckBox[5];
        player2CardCheckboxes = new JCheckBox[5];
        player1Labels = new JLabel[5];
        player2Labels = new JLabel[5];
        deskLabels = new JLabel[3];

        int xOffset = 100; // Starting x-coordinate for the first checkbox
        int player1YOffset = 150; // Fixed y-coordinate for Player 1
        int player2YOffset = 500; // Fixed y-coordinate for Player 2
        int spacing = 100; // Space between checkboxes

        // Load the image icon (facedown card image)
        ImageIcon cardImage = new ImageIcon("images\\back_dark.png");

        // Setup Player 1 GUI components
        for (int i = 0; i < 5; i++) {
            // Create JLabel for Player 1 card image
            player1Labels[i] = new JLabel(cardImage);
            player1Labels[i].setBounds(xOffset + i * spacing, player1YOffset - 100, 80, 100);
            frame.add(player1Labels[i]);

            // Create checkbox for Player 1
            player1CardCheckboxes[i] = new JCheckBox();
            player1CardCheckboxes[i].setBounds(xOffset + i * spacing + 30, player1YOffset, 20, 20);
            frame.add(player1CardCheckboxes[i]);
        }

        // Setup Player 2 GUI components
        for (int i = 0; i < 5; i++) {
            // Create JLabel for Player 2 card image
            player2Labels[i] = new JLabel(cardImage);
            player2Labels[i].setBounds(xOffset + i * spacing, player2YOffset - 100, 80, 100);
            frame.add(player2Labels[i]);

            // Create checkbox for Player 2
            player2CardCheckboxes[i] = new JCheckBox();
            player2CardCheckboxes[i].setBounds(xOffset + i * spacing + 30, player2YOffset, 20, 20);
            frame.add(player2CardCheckboxes[i]);
        }

        // Add three cards in the middle (without checkboxes)
        int middleYOffset = 225; // Y-coordinate for middle cards
        int middleXOffset = 200; // Starting x-coordinate for middle cards
        int middleSpacing = 100; // Space between middle cards
//creating the frame for desk
        for (int i = 0; i < 3; i++) {
            deskLabels[i] = new JLabel(cardImage);
            deskLabels[i].setBounds(middleXOffset + i * middleSpacing, middleYOffset, 80, 100); // Position cards
            frame.add(deskLabels[i]);
        }
        //current players text field
        currentPlayerText = new JLabel("Current Player is Player " + currentPlayer.playerNum);
        currentPlayerText.setBounds(300, 20, 200, 30);
        currentPlayerText.setFont(new Font("Arial", Font.BOLD, 14));
        frame.add(currentPlayerText);

        //player 1's text field num wins
        player1winsText = new JLabel("Player 1 Wins:" + player1.wins);
        player1winsText.setBounds(100, 20, 200, 30);
        player1winsText.setFont(new Font("Arial", Font.BOLD, 14));
        frame.add(player1winsText);

        //player 2's text field num wins
        player2winsText = new JLabel("Player 2 Wins:" + player2.wins);
        player2winsText.setBounds(550, 20, 200, 30);
        player2winsText.setFont(new Font("Arial", Font.BOLD, 14));
        frame.add(player2winsText);
        //player 1's text field
        JLabel player1Text = new JLabel("Player 1");
        player1Text.setBounds(775, 50, 200, 30);
        player1Text.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(player1Text);
            //hand points for p1
        JLabel player1TextHand = new JLabel("Hand's point:");
        player1TextHand.setBounds(775, 75, 200, 30);
        player1TextHand.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(player1TextHand);
        //initializing the label
        player1PointsLabel = new JLabel("X");
        player1PointsLabel.setBounds(900, 75, 200, 30);
        player1PointsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player1PointsLabel.setForeground(new Color(126, 33, 50));
        frame.add(player1PointsLabel);

        player1ChangeButton = new JButton("CHANGE");
        player1ChangeButton.setBounds(775, 110, 100, 30);
        player1ChangeButton.setFont(new Font("Arial", Font.BOLD, 14));
        player1ChangeButton.addActionListener(e -> handleChange());//action listener triggered
        frame.add(player1ChangeButton);

        //player2 text
        JLabel player2Text = new JLabel("Player 2");
        player2Text.setBounds(775, 425, 200, 30);
        player2Text.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(player2Text);
        //hands points for p2
        JLabel player2TextHand = new JLabel("Hand's point:");
        player2TextHand.setBounds(775, 450, 200, 30);
        player2TextHand.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(player2TextHand);
        //player 2 text field
        player2PointsLabel = new JLabel("X");
        player2PointsLabel.setBounds(900, 450, 200, 30);
        player2PointsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player2PointsLabel.setForeground(new Color(126, 33, 50));
        frame.add(player2PointsLabel);

        player2ChangeButton = new JButton("CHANGE");
        player2ChangeButton.setBounds(775, 485, 100, 30);
        player2ChangeButton.setFont(new Font("Arial", Font.BOLD, 14));
        player2ChangeButton.addActionListener(e -> handleChange());//action listener triggered
        frame.add(player2ChangeButton);
        //this is my debug button
        JButton debugButton = new JButton("Debugging");
        debugButton.setBounds(775, 300, 100, 30);
        debugButton.setFont(new Font("Arial", Font.BOLD, 14));
        debugButton.addActionListener(e -> printStateButton());//action listener
        frame.add(debugButton);

        nextRoundButton = new JButton("Next Round");
        nextRoundButton.setBounds(600, 300, 100, 30);
        nextRoundButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextRoundButton.addActionListener(e -> advanceToNextRound());//action listener
        frame.add(nextRoundButton);
        nextRoundButton.setEnabled(false);


        frame.setVisible(true);

    }
//checks if the game is over
    //game over when p1 or p2 gets 3 wins
    public void checkGameOver() {
        String winner;
        if (player1.wins == NUM_ROUNDS_TO_WIN){
            winner = "PLAYER 1";
        } else if (player2.wins == NUM_ROUNDS_TO_WIN){
            winner = "PLAYER 2";
        }
        else {
            winner = null;
        }
        if (winner != null) { //if we have a winner we set the text
            gameOver = true;
            currentPlayerText.setText("GAME OVER " + winner + " WINS");
            System.out.println("---------------------------------------------------");
        }
    }

    public void advanceToNextRound(){

        if (gameOver) {//if game is over we disable some buttons
            System.out.println("Game Over");
            player1ChangeButton.setEnabled(false);
            player2ChangeButton.setEnabled(false);
            nextRoundButton.setEnabled(false);
        } else {//otherwise advance to next round
            System.out.println("Advance to next round");
            startRound(currentPlayer);
        }


    }
        //this is for my debug purpose
    public void printStateButton() {
//        player1.points = calculatePoints(player1.hand);
//        player2.points = calculatePoints(player2.hand);

        System.out.println("======= CURRENT STATE =======");
        System.out.println("numPlayersPlayedInRound: " + numPlayersPlayedInRound);
        System.out.println("numRoundsPlayed: " + numRoundsPlayed);
        System.out.println("gameOver: " + gameOver);
        System.out.println("setOver: " + roundOver);
        System.out.println("Current Player: " + currentPlayer);
        System.out.println("\nPlayer 1");
        System.out.println("------");
        printCards(player1.hand);
        System.out.println("\nPlayer 2");
        System.out.println("------");
        printCards(player2.hand);
        System.out.println("\nDesk");
        printCards(deskCards);

        if (deck == null) {
            System.out.println("Deck is null");
        } else {
            System.out.println("\nNumber of cards on the deck: " + deck.size());
        }

        System.out.println("\nPlayer 1 Checkboxes");
        System.out.println("------");
        debugCheckboxes(player1CardCheckboxes);
        System.out.println("\nPlayer 2 Checkboxes");
        System.out.println("------");
        debugCheckboxes(player2CardCheckboxes);

        System.out.println();
        System.out.println("Player 1 points: " + player1.points);
        System.out.println("Player 2 points: " + player2.points);
    }
//decides how many cards needs to be redrewn
    public int howManyCardsToRedrew(JCheckBox[] checkBoxes) {
        int count = 0; // Counter for checked boxes
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) { // Check if the checkbox is selected
                count++;
            }
        }
        return count; // Return the count of selected checkboxes
    }
//update the players hand with new cards
    public void replaceCards(Player player, List<Card> newCards, JCheckBox[] checkBoxes) {
        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isSelected() && !newCards.isEmpty()) {
                // Replace the selected card in the player's hand
                Card newCard = newCards.remove(0); // Remove the first card from newCards
                player.hand.set(i, newCard); // Replace the card at the same index
            }
        }
    }
    public void showCardImagesDesk() {
        // Displays the desk cards
        System.out.println("Displaying Desk Cards.");
        for (int i = 0; i < 3; i++) {
            String imagePath = deskCards.get(i).getImagePath();//we get the image path for the spesific card

            // Load and resize the image
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(scaledImage);

            // Set the resized image to the label
            deskLabels[i].setIcon(resizedIcon);
        }
    }
//we do the same for players to show
    public void showCardImagesPlayer(Player player) {
        JLabel[] imageLabels;
        if (player == player1) {
            imageLabels = player1Labels;
        } else if (player == player2) {
            imageLabels = player2Labels;
        } else {
            throw new RuntimeException();
        }

        for (int i = 0; i < 5; i++) {
            // Get the image path from the player's hand
            String imagePath = player.hand.get(i).getImagePath();

            // Load and resize the image
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(scaledImage);

            // Set the resized image to the label
            imageLabels[i].setIcon(resizedIcon);
        }
    }

//for hiding purpose
    public void hideCardImagesDesk() {
        JLabel[] imageLabels = deskLabels;

        for (int i = 0; i < 3; i++) {
            // gets the facedown card
            String imagePath = "images\\back_dark.png";
            // Load and resize the image
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(scaledImage);

            // Set the resized image to the label
            imageLabels[i].setIcon(resizedIcon);
        }
    }
//hiding players images
    public void hideCardImagesPlayer(Player player) {
        JLabel[] imageLabels;
        if (player == player1) {
            imageLabels = player1Labels;
        } else if (player == player2) {
            imageLabels = player2Labels;
        } else {
            throw new RuntimeException();
        }

        for (int i = 0; i < 5; i++) {
            // Get the image path from the player's hand
            String imagePath = "images\\back_dark.png";
            // Load and resize the image
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(scaledImage);

            // Set the resized image to the label
            imageLabels[i].setIcon(resizedIcon);
        }
    }
//it is called for every change clicks
    public void updateUI() {

        int numCards;
        if (currentPlayer == player1) {
            // how many true values in this list: player1CardCheckboxes
            numCards = howManyCardsToRedrew(player1CardCheckboxes);
            //gets additional cards and replaces the user hand
            List<Card> newCards = distributeCards(deck, numCards);
            replaceCards(player1, newCards, player1CardCheckboxes);
        } else {
            // how many true values for this array: player2CardCheckboxes
            numCards = howManyCardsToRedrew(player2CardCheckboxes);
            List<Card> newCards = distributeCards(deck, numCards);
            replaceCards(player2, newCards, player2CardCheckboxes);
        }
//setting checkboxes to false
        for (int i = 0; i < 5; i++) {
            player1CardCheckboxes[i].setSelected(false);
            player2CardCheckboxes[i].setSelected(false);
        }
//if round is over but game continues
        if (roundOver && !gameOver) {
            currentPlayerText.setText("ROUND OVER");
            player1ChangeButton.setEnabled(false);
            player2ChangeButton.setEnabled(false);
        } else {     // updating the ui
            if (!gameOver) {
                currentPlayerText.setText("Current Player is Player " + currentPlayer.playerNum);
            }
        }

        if (gameOver) { //making all the buttons invisible
            nextRoundButton.setVisible(false);
            player1ChangeButton.setVisible(false);
            player2ChangeButton.setVisible(false);
        }
//updates the textfields for players' points
        player1PointsLabel.setText(String.valueOf(player1.points));
        player2PointsLabel.setText(String.valueOf(player2.points));
//disablinng the change buttons for the other user
        if (!nextRoundButton.isEnabled()) {
            if (currentPlayer == player1) {
                player2ChangeButton.setEnabled(false);
                player1ChangeButton.setEnabled(true);
            }
            else if (currentPlayer == player2) {
                player1ChangeButton.setEnabled(false);
                player2ChangeButton.setEnabled(true);
            }
        }
    }
    // switching to next player when needed
    public void switchPlayerTurn() {
        if (gameOver) {
            return;
        }

        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
        System.out.println("switched to next player");
    }
//sets the players point in the ui
    public void setPlayerPointsLabelText(Player player) {
        if (player == player1) {
            player1PointsLabel.setText(String.valueOf(player1.points));
        } else if (player == player2) {
            player2PointsLabel.setText(String.valueOf(player2.points));
        }
    }
//sets the number of wins in ui
    public void setPlayerWinsLabelTexts() {
        player1winsText.setText("Player 1 Wins:" + player1.wins);
        player2winsText.setText("Player 2 Wins:" + player2.wins);

    }
//starts round
    public void startRound(Player player) {

        this.roundOver = false;
        this.numPlayersPlayedInRound = 0;
        this.player1.points = 0;
        this.player2.points = 0;

        // Initialize decks.
        this.deck = prepareCards();
        this.player1.hand = distributeCardsPlayer(this.deck);
        this.player2.hand = distributeCardsPlayer(this.deck);
        this.deskCards = distributeCardsDesk(this.deck);
    //hides cards
        hideCardImagesPlayer(player1);
        hideCardImagesPlayer(player2);
        hideCardImagesDesk();
        nextRoundButton.setEnabled(false);

        calculatePoints(player);
        showCardImagesPlayer(player);
        setPlayerPointsLabelText(player);

        updateUI();



    }

//consturctor to start the game
    public CardGame() {
        this.numPlayersPlayedInRound = 0;
        this.roundOver = false;
        this.gameOver = false;
        this.player1 = new Player(1);
        this.player2 = new Player(2);

        this.currentPlayer = player1;
        System.out.println("Current Player is Player " + currentPlayer);
        setupGUI();

        startRound(currentPlayer);
        updateUI();
    }
}
