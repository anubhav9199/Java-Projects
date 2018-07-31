package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#ee0000";
	private static final String discColor2 = "#00ee00";
	private String PLAYER_ONE = "Player One";
	private String PLAYER_TWO = "Player Two";
	private boolean isPlayerOneTurn = true;
	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];
	private boolean isAllowedToInsert = true;

	@FXML
	public GridPane gridPane;
	@FXML
	public Pane menuPane,playGroundPane;
	@FXML
	public TextField playerOneText,playerTwoText;
	@FXML
	public Button setNameBtn;
	@FXML
	public Label playerOneLabel,turnLabel;

	public void createPlayGround(){
		Platform.runLater(() -> setNameBtn.requestFocus());
		Shape rectangleWithHoles = gamePlayStructure();
		gridPane.add(rectangleWithHoles,0,1);
		List<Rectangle> rectangleList = clickableColumns();
		for (Rectangle rectangle:rectangleList) {
			gridPane.add(rectangle,0,1);
		}
		setNameBtn.setOnAction(event -> {
			PLAYER_ONE = playerOneText.getText();
			PLAYER_TWO = playerTwoText.getText();
			playerOneLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
		});
	}

	private Shape gamePlayStructure() {
		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER,(ROWS + 1) * CIRCLE_DIAMETER);
		for (int row = 0; row < ROWS ; row++) {
			for (int col = 0; col < COLUMNS ; col++) {
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);
				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> clickableColumns(){
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col = 0; col < COLUMNS; col++) {
			Rectangle clickableRectangle = new Rectangle(CIRCLE_DIAMETER,(ROWS + 1) * CIRCLE_DIAMETER);
			clickableRectangle.setFill(Color.TRANSPARENT);
			clickableRectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
			clickableRectangle.setOnMouseEntered(event -> clickableRectangle.setFill(Color.valueOf("#eeeeee60")));
			clickableRectangle.setOnMouseExited(event -> clickableRectangle.setFill(Color.TRANSPARENT));
			rectangleList.add(clickableRectangle);
			final int column = col;
			clickableRectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert = false;
					insertedDisc(new Disc(isPlayerOneTurn), column);
				}
			});
		}
		return rectangleList;
	}

	private void insertedDisc(Disc disc,int column){
		int row = ROWS - 1;
		while (row > 0){
			if (getDiscIfPresent(row,column) == null){
				break;
			}
			row--;
		}
		if (row < 0){
			return;
		}
		insertedDiscArray[row][column] = disc;
		playGroundPane.getChildren().addAll(disc);
		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), disc);
		transition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		int currentRow = row;
		transition.setOnFinished(event -> {
			isAllowedToInsert = true;
			if (gameEnded(currentRow,column)){
				gameOver();
				return;
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerOneLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
		});
		transition.play();
	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("Winner is : " + winner);
		alert.setContentText("Do you want to Play Again?");
		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No");
		alert.getButtonTypes().setAll(yesBtn,noBtn);
		Platform.runLater(() -> {
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if (btnClicked.isPresent() && btnClicked.get() == yesBtn){
				resetGame();
				return;
			} else {
				Platform.exit();
				System.exit(0);
				return;
			}
		});
		return;
	}

	private boolean gameEnded(int row, int column) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3).mapToObj(r -> new Point2D(r, column)).collect(Collectors.toList());
		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3).mapToObj(col -> new Point2D(row, col)).collect(Collectors.toList());
		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6).mapToObj(i -> startPoint1.add(i, -i)).collect(Collectors.toList());
		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6).mapToObj(i -> startPoint2.add(i, i)).collect(Collectors.toList());
		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints) || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> Points) {
		int chain = 0;
		for (Point2D point:Points) {
			int rowIndexForArray = (int) point.getX();
			int colIndexForArray = (int) point.getY();
			Disc disc = getDiscIfPresent(rowIndexForArray,colIndexForArray);
			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
				chain++;
				if (chain == 4){
					return true;
				}
			}else {
				chain = 0;
			}
		}
		return false;
	}

	private Disc getDiscIfPresent(int row,int column){
		if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0){
			return null;
		}
		return insertedDiscArray[row][column];
	}

	public void resetGame() {
		playGroundPane.getChildren().clear();
		for (int row = 0; row < insertedDiscArray.length ; row++) {
			for (int col = 0; col < insertedDiscArray[row].length; col++) {
				insertedDiscArray[row][col] = null;
			}
		}
		isPlayerOneTurn = true;
		playerOneLabel.setText(PLAYER_ONE);
		createPlayGround();
		return;
	}

	private static class Disc extends Circle{
		private boolean isPlayerOneMove;
		private Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1) : Color.valueOf(discColor2));
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
}
