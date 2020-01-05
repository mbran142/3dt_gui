import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel{

    private JButton quitButton, acceptButton;
    private JLabel topText, coinCount, crystalCount;
    private Graveyard graveyard;
    private EnemyView enemyView;
    private YourCards yourCards;
    private CountdownTimer timer;
    private ActionPanel actionPanel;

    public GameScreen() {

        JPanel leftPanel, midPanel, rightPanel;

        //left panel
        quitButton = new JButton("quit");
        quitButton.setPreferredSize(new Dimension(20,30));
        graveyard = new Graveyard();
        coinCount = new JLabel("2");
        crystalCount = new JLabel("0");
        JPanel itemPanel = new JPanel(new GridLayout(2,2,4,12));
        itemPanel.setPreferredSize(new Dimension(80,80));
        itemPanel.add(new JLabel("Coins: ")); //ADD GRAPHIC - coin
        itemPanel.add(coinCount);
        itemPanel.add(new JLabel("Crystals: ")); //ADD GRAPHIC - crystal
        itemPanel.add(crystalCount);

        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(quitButton);
        leftPanel.add(graveyard);
        leftPanel.add(itemPanel);

        //middle panel
        topText = new JLabel("Game Start");
        topText.setFont(new Font(topText.getFont().getName(), Font.PLAIN, 24));
        topText.setForeground(Color.BLACK);
        enemyView = new EnemyView();
        yourCards = new YourCards();
        timer = new CountdownTimer();

        midPanel = new JPanel();
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
        midPanel.add(topText);
        midPanel.add(enemyView);
        midPanel.add(yourCards);
        midPanel.add(timer);

        //right panel
        actionPanel = new ActionPanel();
        acceptButton = new JButton("confirm action");

        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(actionPanel);

        //put all panels together
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(leftPanel);
        this.add(midPanel);
        this.add(rightPanel);
    }
}

//shows which cards have been discarded
class Graveyard extends JPanel {

    public Graveyard() {

        this.setPreferredSize(new Dimension(200,300));
        this.setLayout(new GridLayout(3,3,10,10));
        this.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        //ADD GRAPHIC - card graphic (probably the no card graphic at the start)
        for (int i = 0; i < 9; i++)
            this.add(new JButton(Integer.toString(i + 1)));
    }
}

//shows how many cards your opponent has
class EnemyView extends JPanel {

    int cardsLeft;

    public EnemyView() {

        this.setPreferredSize(new Dimension(260,60));
        this.setLayout(new GridLayout(1, 4, 16, 0));

        for (int i = 0; i < 4; i++)
            this.add(new JButton(Integer.toString(i + 1)));

        cardsLeft = 4;
    }
}

//shows your cards
class YourCards extends JPanel {

    int cardsLeft;

    public YourCards() {

        this.setPreferredSize(new Dimension(260, 400));
        this.setLayout(new GridLayout(2,2,20,20));

        for (int i = 0; i < 4; i++)
            this.add(new JButton(Integer.toString(i + 1)));

        cardsLeft = 4;
    }

}

class CountdownTimer extends JPanel {

    public CountdownTimer() {

        this.setPreferredSize(new Dimension(260, 30));
        this.setBackground(new Color(0,200,0));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
}

class ActionPanel extends JPanel {

    public ActionPanel() {

        this.setPreferredSize(new Dimension(180, 600));
        this.setLayout(new GridLayout(12,2,4,10));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //ADD GRAPHIC - symbols for each action

        //income
        this.add(new JLabel("N/A"));
        this.add(new JLabel("Income"));

        //foreign aid
        this.add(new JLabel("N/A"));
        this.add(new JLabel("Foreign Aid"));

        //coup
        this.add(new JLabel("N/A"));
        this.add(new JLabel("Coup"));

        //duke
        this.add(new JLabel("Duke"));
        this.add(new JLabel("Tax"));

        //assassin
        this.add(new JLabel("Assassin"));
        this.add(new JLabel("Assassinate"));

        //ambassador
        this.add(new JLabel("Ambassador"));
        this.add(new JLabel("Exchange"));

        //captain
        this.add(new JLabel("Captain"));
        this.add(new JLabel("Steal"));

        //contessa
        this.add(new JLabel("Contessa"));
        this.add(new JLabel("Elude(?)"));

        //inquisitor
        this.add(new JLabel("Inquisitor"));
        this.add(new JLabel("Insight"));

        //speculator
        this.add(new JLabel("Speculator"));
        this.add(new JLabel("Gamble"));

        //jester
        this.add(new JLabel("Jester"));
        this.add(new JLabel("Condemn"));

        //bureaucrat
        this.add(new JLabel("Bureaucrat"));
        this.add(new JLabel("Cooperation"));
    }
}