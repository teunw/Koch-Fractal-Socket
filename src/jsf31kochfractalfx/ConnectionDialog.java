package jsf31kochfractalfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by Teun on 5-1-2016.
 */
public class ConnectionDialog extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        TextField textField = new TextField();
        grid.add(textField, 1, 1);

        Button button = new Button("Connect");
        button.setOnMouseClicked(event -> {
            System.out.println("IP set to " + textField.getText());
            JSF31KochFractalFX j = new JSF31KochFractalFX(textField.getText());
            j.start(primaryStage);
        });
        grid.add(button, 1, 2);

        Group root = new Group();
        Scene scene = new Scene(root, 250, 150);
        root.getChildren().add(grid);

        primaryStage.setTitle("Enter ip to connect to");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
