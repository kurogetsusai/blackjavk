import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class MainWindow implements Initializable {
	@FXML private AnchorPane anchorPane;
	@FXML private Label fxBalance;
	@FXML private Label fxBet;
	@FXML private Button fxStart;
	@FXML private Button fxBet1;
	@FXML private Button fxBet5;
	@FXML private Button fxBet10;
	@FXML private Button fxBet50;
	@FXML private Button fxBet100;
	@FXML private Button fxBet500;
	@FXML private Button fxBet1000;
	@FXML private Button fxHit;
	@FXML private Button fxStand;
	@FXML private Button fxDoubleDown;
	@FXML private Button fxSplit;
	@FXML private Label fxPointsCasino;
	@FXML private Label fxPointsPlayer;
	@FXML private Label fxPointsPlayerSplit;
	@FXML private Label fxSplitArrow1;
	@FXML private Label fxSplitArrow2;

	@FXML private List<Label> cardsCasino = new ArrayList<Label>();
	@FXML private List<Label> cardsPlayer = new ArrayList<Label>();
	@FXML private List<Label> cardsPlayerSplit = new ArrayList<Label>();

	private String[] cardNames = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
	private int balance = 10000;
	private int bet = 0;
	private int cardsCasinoCount = 0;
	private int cardsPlayerCount = 0;
	private int cardsPlayerSplitCount = 0;
	private AnIntegerThatActuallyWorksProperlyWithPointers pointsCasino;
	private AnIntegerThatActuallyWorksProperlyWithPointers pointsPlayer;
	private AnIntegerThatActuallyWorksProperlyWithPointers pointsPlayerSplit;
	private int splitMode = 0;

	public void initialize(URL url, ResourceBundle rb) {
		pointsCasino = new AnIntegerThatActuallyWorksProperlyWithPointers();
		pointsPlayer = new AnIntegerThatActuallyWorksProperlyWithPointers();
		pointsPlayerSplit = new AnIntegerThatActuallyWorksProperlyWithPointers();

		fxHit.setVisible(false);
		fxStand.setVisible(false);
		fxDoubleDown.setVisible(false);
		fxSplit.setVisible(false);
		fxPointsCasino.setVisible(false);
		fxPointsPlayer.setVisible(false);
		fxPointsPlayerSplit.setVisible(false);
		fxSplitArrow1.setVisible(false);
		fxSplitArrow2.setVisible(false);
		refreshLabels();
	}

	private void refreshLabels() {
		fxBalance.setText("Balance: " + balance);
		fxBet.setText("Bet: " + bet);
		fxPointsCasino.setText("Points: " + pointsCasino.value);
		fxPointsPlayer.setText("Points: " + pointsPlayer.value);
		fxPointsPlayerSplit.setText("Points: " + pointsPlayerSplit.value);
	}

	private void incBet(int n) {
		if (balance >= n) {
			balance -= n;
			bet += n;
		}
		refreshLabels();
	}

	private void addCard(Boolean player) {
		addCard(player, false, "");
	}

	private void addCard(Boolean player, Boolean secondary) {
		addCard(player, secondary, "");
	}

	private void addCard(Boolean player, Boolean secondary, String label) {
		// secondary -> player: split
		//             !player: reversed
		int count = player ? (secondary ? cardsPlayerSplitCount : cardsPlayerCount) : cardsCasinoCount;
		List<Label> cards = player ? (secondary ? cardsPlayerSplit : cardsPlayer) : cardsCasino;
		cards.add(new Label());
		Label card;
		card = cards.get(count);

		card.setLayoutX(200 + (count * 20) + ((player && secondary) ? 100 : 0));
		card.setLayoutY(30 + (count * 20) + (player ? 290 : 0));
		card.setMinWidth(60);
		card.setMinHeight(90);
		if (!player && secondary) {
			card.getStyleClass().add("card-reversed");
			card.setText("");
		} else {
			card.getStyleClass().add("card");

			AnIntegerThatActuallyWorksProperlyWithPointers points = player ? (secondary ? pointsPlayerSplit : pointsPlayer) : pointsCasino;
			if (label.equals("")) {
				Random r = new Random();
				int n = r.nextInt(12);
				points.value += getCardValue(cardNames[n], points);
				card.setText(cardNames[n]);
			} else {
				points.value += getCardValue(label, points);
				card.setText(label);
			}
			refreshLabels();
		}
		card.setAlignment(Pos.TOP_LEFT);
		card.setVisible(true);
		anchorPane.getChildren().add(card);

		if (player) {
			if (secondary)
				++cardsPlayerSplitCount;
			else
				++cardsPlayerCount;
		} else {
			++cardsCasinoCount;
		}
	}

	private int getCardValue(String cardName, AnIntegerThatActuallyWorksProperlyWithPointers points) {
		if (cardName.equals("10") || cardName.equals("J") || cardName.equals("Q") || cardName.equals("K"))
			return 10;
		if (cardName.equals("A")) {
			if (points.value + 11 > 21)
				return 1;
			else
				return 11;
		}

		return Integer.parseInt(cardName);
	}

	private void revealReversedCard() {
		Label card = cardsCasino.get(0);
		card.getStyleClass().remove("card-reversed");
		card.getStyleClass().add("card");
		Random r = new Random();
		int n = r.nextInt(12);

		pointsCasino.value += getCardValue(cardNames[n], pointsCasino);
		refreshLabels();

		card.setText(cardNames[n]);

		while (pointsCasino.value < 17)
			addCard(false);
	}

	private void checkPlayerPoints(Boolean end) {
		AnIntegerThatActuallyWorksProperlyWithPointers pointsPlayer = (splitMode == 2 || splitMode == 3) ? this.pointsPlayerSplit : this.pointsPlayer;
		if (pointsPlayer.value > 21 || (end && (pointsCasino.value <= 21) && (pointsPlayer.value < pointsCasino.value))) {
			if ((splitMode == 3) || (splitMode == 4)) {
				splitMode = 1;
				revealReversedCard();
				checkPlayerPoints(true);
				splitMode = 2;
				checkPlayerPoints(true);
				return;
			}

			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("You lost!");
			a.setHeaderText(null);
			a.setContentText("You lost!");
			a.showAndWait();

			if (splitMode != 1 && splitMode != 4)
				reset();
		} else if (end && ((pointsPlayer.value > pointsCasino.value) || (pointsCasino.value > 21))) {
			if ((splitMode == 3) || (splitMode == 4)) {
				splitMode = 1;
				revealReversedCard();
				checkPlayerPoints(true);
				splitMode = 2;
				checkPlayerPoints(true);
				return;
			}

			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("You won!");
			a.setHeaderText(null);
			a.setContentText("You won!");
			a.showAndWait();
			balance += bet + bet;

			if (splitMode != 1 && splitMode != 4)
				reset();
		} else if (end) {
			if ((splitMode == 3) || (splitMode == 4)) {
				splitMode = 1;
				revealReversedCard();
				checkPlayerPoints(true);
				splitMode = 2;
				checkPlayerPoints(true);
				return;
			}

			Alert a = new Alert(Alert.AlertType.INFORMATION);
			a.setTitle("Draw!");
			a.setHeaderText(null);
			a.setContentText("Draw!");
			a.showAndWait();
			balance += bet;

			if (splitMode != 1 && splitMode != 4)
				reset();
		}
	}

	private void reset() {
		bet = 0;
		cardsCasinoCount = 0;
		cardsPlayerCount = 0;
		cardsPlayerSplitCount = 0;
		pointsCasino = new AnIntegerThatActuallyWorksProperlyWithPointers();
		pointsPlayer = new AnIntegerThatActuallyWorksProperlyWithPointers();
		pointsPlayerSplit = new AnIntegerThatActuallyWorksProperlyWithPointers();
		splitMode = 0;

		int i = cardsCasino.size();
		while (i --> 0)
			anchorPane.getChildren().remove(cardsCasino.get(i));
		i = cardsPlayer.size();
		while (i --> 0)
			anchorPane.getChildren().remove(cardsPlayer.get(i));
		i = cardsPlayerSplit.size();
		while (i --> 0)
			anchorPane.getChildren().remove(cardsPlayerSplit.get(i));

		cardsCasino.clear();
		cardsPlayer.clear();
		cardsPlayerSplit.clear();

		fxStart.setVisible(true);
		fxBet1.setVisible(true);
		fxBet5.setVisible(true);
		fxBet10.setVisible(true);
		fxBet50.setVisible(true);
		fxBet100.setVisible(true);
		fxBet500.setVisible(true);
		fxBet1000.setVisible(true);

		fxHit.setVisible(false);
		fxStand.setVisible(false);
		fxDoubleDown.setVisible(false);
		fxSplit.setVisible(false);
		fxPointsCasino.setVisible(false);
		fxPointsPlayer.setVisible(false);
		fxPointsPlayerSplit.setVisible(false);
		fxSplitArrow1.setVisible(false);
		fxSplitArrow2.setVisible(false);
		refreshLabels();
	}

	@FXML
	private void fxStartClick(ActionEvent event) {
		if (bet < 1) {
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setTitle("Nope!");
			a.setHeaderText(null);
			a.setContentText("Pick your bet first!");
			a.showAndWait();
			return;
		}

		fxStart.setVisible(false);
		fxBet1.setVisible(false);
		fxBet5.setVisible(false);
		fxBet10.setVisible(false);
		fxBet50.setVisible(false);
		fxBet100.setVisible(false);
		fxBet500.setVisible(false);
		fxBet1000.setVisible(false);
		fxHit.setVisible(true);
		fxStand.setVisible(true);
		fxDoubleDown.setVisible(true);
		fxSplit.setVisible(false);
		fxPointsCasino.setVisible(true);
		fxPointsPlayer.setVisible(true);
		fxPointsPlayerSplit.setVisible(false);

		addCard(false, true);
		addCard(false);
		addCard(true);
		addCard(true);

		if (getCardValue(cardsPlayer.get(0).getText(), pointsPlayer) == getCardValue(cardsPlayer.get(1).getText(), pointsPlayer))
			fxSplit.setVisible(true);
	}

	@FXML
	private void fxBet1Click(ActionEvent event) {
		incBet(1);
	}

	@FXML
	private void fxBet5Click(ActionEvent event) {
		incBet(5);
	}

	@FXML
	private void fxBet10Click(ActionEvent event) {
		incBet(10);
	}

	@FXML
	private void fxBet50Click(ActionEvent event) {
		incBet(50);
	}

	@FXML
	private void fxBet100Click(ActionEvent event) {
		incBet(100);
	}

	@FXML
	private void fxBet500Click(ActionEvent event) {
		incBet(500);
	}

	@FXML
	private void fxBet1000Click(ActionEvent event) {
		incBet(1000);
	}

	@FXML
	private void fxHitClick(ActionEvent event) {
		fxDoubleDown.setVisible(false);
		fxSplit.setVisible(false);

		addCard(true, (splitMode == 2 || splitMode == 3));
		checkPlayerPoints(false);

		if (splitMode == 1) {
			splitMode = 2;
			fxSplitArrow1.setVisible(false);
			fxSplitArrow2.setVisible(true);
		} else if (splitMode == 2) {
			if (cardsPlayerCount < 22)
				splitMode = 1;
			fxSplitArrow1.setVisible(true);
			fxSplitArrow2.setVisible(false);
		}
	}

	@FXML
	private void fxStandClick(ActionEvent event) {
		if (splitMode == 1) {
			splitMode = 3;
			fxSplitArrow1.setVisible(false);
			fxSplitArrow2.setVisible(true);
		} else if (splitMode == 3) {
			splitMode = 1;
			revealReversedCard();
			checkPlayerPoints(true);
			splitMode = 2;
			checkPlayerPoints(true);
		} else if (splitMode == 2 && cardsPlayerCount < 22) {
			fxSplitArrow1.setVisible(true);
			fxSplitArrow2.setVisible(false);
			splitMode = 4;
		} else if (splitMode == 4) {
			revealReversedCard();
			splitMode = 1;
			checkPlayerPoints(true);
			splitMode = 2;
			checkPlayerPoints(true);
		} else {
			revealReversedCard();
			checkPlayerPoints(true);
		}
	}

	@FXML
	private void fxDoubleDownClick(ActionEvent event) {
		balance -= bet;
		bet *= 2;
		addCard(true);
		revealReversedCard();
		checkPlayerPoints(true);
	}

	@FXML
	private void fxSplitClick(ActionEvent event) {
		fxDoubleDown.setVisible(false);
		fxSplit.setVisible(false);
		fxPointsPlayerSplit.setVisible(true);

		String label = cardsPlayer.get(1).getText();
		anchorPane.getChildren().remove(cardsPlayer.get(1));
		cardsPlayer.remove(1);
		--cardsPlayerCount;
		pointsPlayer.value -= getCardValue(label, pointsPlayer);
		addCard(true, true, label);
		splitMode = 1;
		fxSplitArrow1.setVisible(true);
		fxSplitArrow2.setVisible(false);
	}
}
