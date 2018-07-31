package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {
	private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
	    GridPane root = loader.load();
	    Pane menuPane = (Pane) root.getChildren().get(0);
	    MenuBar menuBar = createMenu();
	    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
	    menuPane.getChildren().addAll(menuBar);
	    controller = loader.getController();
	    controller.createPlayGround();
	    Scene scene = new Scene(root);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Connect Four");
	    primaryStage.setResizable(false);
		primaryStage.show();
    }
    private MenuBar createMenu(){
    	//File Menu
	    Menu fileMenu = new Menu("File");
	    MenuItem newFile = new MenuItem("New");
	    newFile.setOnAction(event -> resetGame());
	    MenuItem resetMenu = new MenuItem("Reset");
	    resetMenu.setOnAction(event -> resetGame());
	    SeparatorMenuItem sep1 = new SeparatorMenuItem();
	    MenuItem quitMenu = new MenuItem("Quit");
	    quitMenu.setOnAction(event -> quit());
	    fileMenu.getItems().addAll(newFile,resetMenu,sep1,quitMenu);
	    //Help Menu
	    Menu helpMenu = new Menu("Help");
	    MenuItem aboutGame = new MenuItem("About Game");
	    aboutGame.setOnAction(event -> about());
	    SeparatorMenuItem sep2 = new SeparatorMenuItem();
	    MenuItem aboutMe = new MenuItem("About Developer");
	    aboutMe.setOnAction(event -> aboutMe());
	    helpMenu.getItems().addAll(aboutGame,sep2,aboutMe);
	    //MenuBar
	    MenuBar menuBar = new MenuBar();
	    menuBar.getMenus().addAll(fileMenu,helpMenu);
	    return menuBar;
    }

	private void resetGame() {
    	controller.resetGame();
	}

	private void aboutMe() {
		Alert about = new Alert(Alert.AlertType.INFORMATION);
		about.setTitle("About Developer");
		about.setHeaderText("Anubhav Sharma");
		about.setContentText("I love to play around with code and create games. " +
				"Connect 4 is one of them. In free time " +
				"I like to spend time with nears and dears.");
		ButtonType okay = new ButtonType("I Got It");
		about.getButtonTypes().setAll(okay);
		Optional<ButtonType> meType = about.showAndWait();
		if (meType.isPresent() && meType.get() == okay){
			System.out.println("I Got It button is Clicked");
		}
	}

	private void about() {
		Alert about = new Alert(Alert.AlertType.INFORMATION);
		about.setTitle("About Connect Four");
		about.setHeaderText("About Game");
		about.setContentText("Connect Four is a two-player connection game in which the players first choose a color and"
				+ "then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended" +
				"grid. The pieces fall straight down, occupying the next available space within the column. The objective" +
				"of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs." +
				"Connect Four is a solved game. The first player can always win by playing the right moves.");
		ButtonType okay = new ButtonType("Okay");
		about.getButtonTypes().setAll(okay);
		Optional<ButtonType> type = about.showAndWait();
		if(type.isPresent() && type.get() == okay){
			System.out.println("Okay button is Clicked");
		}
	}

	private void quit() {
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
        launch(args);
    }
}
