/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf31kochfractalfx;

import calculate.Edge;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nl.teunwillems.client.KochFractalSocketClient;
import nl.teunwillems.server.Request;

/**
 * @author Nico Kuijpers
 */
public class JSF31KochFractalFX extends Application {

    private final int kpWidth = 650;
    private final int kpHeight = 500;
    private String ip;
    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    private double startPressedX = 0.0;
    private double startPressedY = 0.0;
    private double lastDragX = 0.0;
    private double lastDragY = 0.0;
    // Current level of Koch fractal
    private int currentLevel = 1;
    // Labels for level, nr edges, calculation time, and drawing time
    private Label labelLevel;
    // Koch panel and its size
    private Canvas kochPanel;
    private Button buttonIncreaseLevel, buttonDecreaseLevel;
    private ComboBox<Request.REQUEST_TYPE> connectionType;
    private KochFractalSocketClient client;

    public JSF31KochFractalFX(String ip) {
        this.ip = ip;
    }

    public JSF31KochFractalFX() {
        this.ip = null;
    }

    @Override
    public void start(Stage primaryStage) {

        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // For debug purposes
        // Make de grid lines visible
        // grid.setGridLinesVisible(true);

        // Drawing panel for Koch fractal
        kochPanel = new Canvas(kpWidth, kpHeight);
        grid.add(kochPanel, 0, 3, 25, 1);

        // Label to present current level of Koch fractal
        labelLevel = new Label("Level: " + currentLevel);
        grid.add(labelLevel, 0, 6);

        // Button to increase level of Koch fractal
        buttonIncreaseLevel = new Button();
        buttonIncreaseLevel.setText("Increase Level");
        buttonIncreaseLevel.setOnAction(this::increaseLevelButtonActionPerformed);
        grid.add(buttonIncreaseLevel, 3, 6);

        // Button to decrease level of Koch fractal
        buttonDecreaseLevel = new Button();
        buttonDecreaseLevel.setText("Decrease Level");
        buttonDecreaseLevel.setOnAction(this::decreaseLevelButtonActionPerformed);
        grid.add(buttonDecreaseLevel, 5, 6);

        connectionType = new ComboBox<>(FXCollections.observableArrayList(Request.REQUEST_TYPE.DYNAMIC, Request.REQUEST_TYPE.WHOLE));
        connectionType.valueProperty().setValue(Request.REQUEST_TYPE.DYNAMIC);
        grid.add(connectionType, 4, 6);

        // Button to fit Koch fractal in Koch panel
        Button buttonFitFractal = new Button();
        buttonFitFractal.setText("Fit Fractal");
        buttonFitFractal.setOnAction(this::fitFractalButtonActionPerformed);
        grid.add(buttonFitFractal, 14, 6);


        // Add mouse clicked event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_CLICKED,
                this::kochPanelMouseClicked);

        // Add mouse pressed event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_PRESSED,
                this::kochPanelMousePressed);

        // Add mouse dragged event to Koch panel
        kochPanel.setOnMouseDragged(this::kochPanelMouseDragged);


        // Create Koch manager and set initial level
        resetZoom();

        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth + 50, kpHeight + 300);
        root.getChildren().add(grid);

        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void clearKochPanel() {
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        gc.clearRect(0.0, 0.0, kpWidth, kpHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, kpWidth, kpHeight);
    }

    public String getIp() {
        return ip;
    }

    public void drawEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();

        // Adjust edge for zoom and drag
        Edge e1 = edgeAfterZoomAndDrag(e);

        // Set line color
        gc.setStroke(e1.getColor());

        // Set line width depending on level
        if (currentLevel <= 3) {
            gc.setLineWidth(2.0);
        } else if (currentLevel <= 5) {
            gc.setLineWidth(1.5);
        } else {
            gc.setLineWidth(1.0);
        }

        // Draw line
        gc.strokeLine(e1.getX1(), e1.getY1(), e1.getX2(), e1.getY2());
    }

    private void increaseLevelButtonActionPerformed(ActionEvent event) {
        currentLevel++;
        drawSocket();
    }

    private void decreaseLevelButtonActionPerformed(ActionEvent event) {
        currentLevel--;
        drawSocket();
    }

    private void fitFractalButtonActionPerformed(ActionEvent event) {
        drawSocket(Request.REQUEST_TYPE.WHOLE);
    }

    private void kochPanelMouseClicked(MouseEvent event) {
        if (Math.abs(event.getX() - startPressedX) < 1.0 &&
                Math.abs(event.getY() - startPressedY) < 1.0) {
            double originalPointClickedX = (event.getX() - zoomTranslateX) / zoom;
            double originalPointClickedY = (event.getY() - zoomTranslateY) / zoom;
            if (event.getButton() == MouseButton.PRIMARY) {
                zoom *= 2.0;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                zoom /= 2.0;
            }
            zoomTranslateX = (int) (event.getX() - originalPointClickedX * zoom);
            zoomTranslateY = (int) (event.getY() - originalPointClickedY * zoom);
            drawSocket(Request.REQUEST_TYPE.WHOLE);
        }
    }

    private void kochPanelMouseDragged(MouseEvent event) {
        zoomTranslateX = zoomTranslateX + event.getX() - lastDragX;
        zoomTranslateY = zoomTranslateY + event.getY() - lastDragY;
        lastDragX = event.getX();
        lastDragY = event.getY();
        drawSocket(Request.REQUEST_TYPE.WHOLE);
    }

    private void kochPanelMousePressed(MouseEvent event) {
        startPressedX = event.getX();
        startPressedY = event.getY();
        lastDragX = event.getX();
        lastDragY = event.getY();
    }

    private void resetZoom() {
        int kpSize = Math.min(kpWidth, kpHeight);
        zoom = kpSize;
        zoomTranslateX = (kpWidth - kpSize) / 2.0;
        zoomTranslateY = (kpHeight - kpSize) / 2.0;
    }

    private void drawSocket() {
        KochFractalSocketClient k = new KochFractalSocketClient(this, new Request(connectionType.getValue(), currentLevel));
        k.requestEdges();
    }

    private void drawSocket(Request.REQUEST_TYPE request_type) {
        KochFractalSocketClient k = new KochFractalSocketClient(this, new Request(request_type, currentLevel));
        k.requestEdges();
    }

    private Edge edgeAfterZoomAndDrag(Edge e) {
        return new Edge(
                e.getX1() * zoom + zoomTranslateX,
                e.getY1() * zoom + zoomTranslateY,
                e.getX2() * zoom + zoomTranslateX,
                e.getY2() * zoom + zoomTranslateY,
                e.getColor());
    }

}
