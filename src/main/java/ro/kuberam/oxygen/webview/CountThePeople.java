package ro.kuberam.oxygen.webview;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class CountThePeople extends Application {
    private static final String CURRENT_URL =
        "http://www.census.gov/popclock/";
    private static final String PROJECTED_URL =
        "http://www.sciencedaily.com/releases/2015/08/150810110634.htm";

    @Override
    public void start(final Stage stage) throws Exception {
        WebView current = new WebView();
        current.getEngine().load(CURRENT_URL);
        WebView projected = new WebView();
        projected.getEngine().load(PROJECTED_URL);

        StackPane viewHolder = new StackPane();

        ToggleGroup choice = new ToggleGroup();

        RadioButton currentRadio = new RadioButton("Current");
        currentRadio.setToggleGroup(choice);
        RadioButton projectedRadio = new RadioButton("Projected");
        projectedRadio.setToggleGroup(choice);

        choice.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (projectedRadio == newValue) {
                viewHolder.getChildren().setAll(projected);
            } else {
                viewHolder.getChildren().setAll(current);
            }
        });

        choice.selectToggle(currentRadio);
        
//		BorderPane root = new BorderPane();
//		root.setId("background");

		ToolBar toolBar = new ToolBar();
//		root.setTop(toolBar);

		Region spacer = new Region();
		spacer.getStyleClass().setAll("spacer");

		HBox buttonBar = new HBox();
		buttonBar.getStyleClass().setAll("segmented-button-bar");
		Button sampleButton = new Button("Tasks");
		sampleButton.getStyleClass().addAll("first");
		Button sampleButton2 = new Button("Administrator");
		Button sampleButton3 = new Button("Search");
		Button sampleButton4 = new Button("Line");
		Button sampleButton5 = new Button("Process");
		sampleButton5.getStyleClass().addAll("last", "capsule");
		buttonBar.getChildren().addAll(sampleButton, sampleButton2, sampleButton3, sampleButton4, sampleButton5);
		toolBar.getItems().addAll(spacer, buttonBar);

        VBox layout = new VBox(10, currentRadio, projectedRadio, toolBar, viewHolder);
        layout.setPadding(new Insets(10));

        stage.setTitle("World Population");
        stage.setScene(new Scene(layout));
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}