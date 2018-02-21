package redboxman_javafx;

import java.io.InputStream;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 *
 * @author McKillaGorilla
 */
public class RedBoxManRenderer extends Application {
    Canvas canvas;
    GraphicsContext gc;
    ArrayList<Point2D> imagesRedBoxManLocations;
    ArrayList<Point2D> shapesRedBoxManLocations;
    ArrayList<Point2D> shapesRedRoundManLocations;
    Image redBoxManImage;
    
    @Override
    public void start(Stage primaryStage) {
	// INIT THE DATA MANAGERS
	imagesRedBoxManLocations = new ArrayList<>();
	shapesRedBoxManLocations = new ArrayList<>();
	shapesRedRoundManLocations = new ArrayList<>();
	
	// LOAD THE RED BOX MAN IMAGE
        InputStream str = getClass().getResourceAsStream("/RedBoxMan.png");
	redBoxManImage = new Image(str);
	
	// MAKE THE CANVAS
	canvas = new Canvas();
	canvas.setStyle("-fx-background-color: cyan");
	gc = canvas.getGraphicsContext2D();

	// PUT THE CANVAS IN A CONTAINER
	Group root = new Group();
	root.getChildren().add(canvas);
	
	canvas.setOnMouseClicked(e->{
	    if (e.isShiftDown()) {
		shapesRedBoxManLocations.add(new Point2D(e.getX(), e.getY()));
		render();
	    }
	    else if (e.isControlDown()) {
		imagesRedBoxManLocations.add(new Point2D(e.getX(), e.getY()));
		render();
	    }else if(e.isAltDown()){
		shapesRedRoundManLocations.add(new Point2D(e.getX(), e.getY()));
		render();
	    }
	    else {
		clear();
	    }
	});
	
	// PUT THE CONTAINER IN A SCENE
	Scene scene = new Scene(root, 800, 600);
	canvas.setWidth(scene.getWidth());
	canvas.setHeight(scene.getHeight());

	// AND START UP THE WINDOW
	primaryStage.setTitle("Red Box Man Renderer");
	primaryStage.setScene(scene);
	primaryStage.show();
    }
    
    public void clearCanvas() {
	gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public void clear() {
	shapesRedBoxManLocations.clear();
	imagesRedBoxManLocations.clear();
	render();
    }
    
    public void render() {
	clearCanvas();
	for (int i = 0; i < shapesRedBoxManLocations.size(); i++) {
	    renderShapeRedBoxMan(shapesRedBoxManLocations.get(i));
	}
	for (int j = 0; j < imagesRedBoxManLocations.size(); j++) {
	    renderImageRedBoxMan(imagesRedBoxManLocations.get(j));
	}
	for (int k = 0; k < shapesRedRoundManLocations.size(); k++){
		renderShapeRedRoundMan(shapesRedRoundManLocations.get(k));
	}
    }
    
    public void renderShapeRedBoxMan(Point2D location) {
	String headColor = "#DD0000";
	String outlineColor = "#000000";
	int headW = 115;
	int headH = 88;
    
	// DRAW HIS RED HEAD
        gc.setFill(Paint.valueOf(headColor));
	gc.fillRect(location.getX(), location.getY(), headW, headH);
        gc.beginPath();
	gc.setStroke(Paint.valueOf(outlineColor));
	gc.setLineWidth(1);
	gc.rect(location.getX(), location.getY(), headW, headH);
	gc.stroke();
	
	// AND THEN DRAW THE REST OF HIM
    }

    public void renderShapeRedRoundMan(Point2D location){
	    //Draw Body
	   double bodyX = location.getX() + (25);
	   double bodyY = location.getY() + (70);
	   int bodyW = 60;
	   int bodyH = 30;
	   gc.setFill(Paint.valueOf("#000000"));
	   gc.fillRect(bodyX, bodyY, bodyW, bodyH);
	   gc.fillRect(bodyX + 7.5, bodyY + 20, 45, 20);
	   gc.fillRect(bodyX - 2, bodyY + 35, 10, 10);
	   gc.fillRect(bodyX + bodyW - 8, bodyY +35, 10, 10);



	    String headColor = "#40E0D0";
	    String outlineColor = "#000000";
	    int headW = 115;
	    int headH = 88;

	    //Draw his red head
	    gc.setFill(Paint.valueOf(headColor));
	    gc.fillOval(location.getX(), location.getY(), headW, headH);
	    gc.beginPath();
	    gc.setStroke(Paint.valueOf(outlineColor));
	    gc.setLineWidth(1);
	    gc.strokeOval(location.getX(), location.getY(), headW, headH);
	    gc.stroke();

	    //Draw his eyes
	    String eyeColor = "#FFFF00";
	    int eyeW = 30;
	    int eyeH = 20;

	    double leftX = location.getX() + (15);
	    double leftY = location.getY() + (25);
	    double rightX = location.getX() + (65);
	    double rightY= leftY;

	    gc.setFill(Paint.valueOf(eyeColor));
	    gc.fillOval(leftX, leftY, eyeW, eyeH);
	    gc.fillOval(rightX, rightY, eyeW, eyeH);
	    gc.beginPath();
	    gc.strokeOval(leftX, leftY, eyeW, eyeH);
	    gc.strokeOval(rightX, rightY, eyeW, eyeH);
	    gc.stroke();

	    //Draw pupils
	    String black = "#000000";
	    int pupilW = 10;
	    int pupilH = 5;
	    double pupilLeftX = leftX + 9;
	    double pupilLeftY = leftY + 8;
	    double pupilRightX = rightX + 9;
	    double pupilRightY = pupilLeftY;

	    gc.setFill(Paint.valueOf(black));
	    gc.fillOval(pupilLeftX, pupilLeftY, pupilW, pupilH);
	    gc.fillOval(pupilRightX, pupilRightY, pupilW, pupilH);
	    gc.beginPath();


	   //Draw mouth
	   double mouthX = location.getX() + 20;
	   double mouthY = location.getY() + 60;
	   int mouthW = 70;
	   int mouthH = 7;
	   gc.fillRect(mouthX, mouthY, mouthW, mouthH);
	   gc.beginPath();

	   

    }
    
    public void renderImageRedBoxMan(Point2D location) {
	gc.drawImage(redBoxManImage, location.getX(), location.getY());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	launch(args);
    }
    
}
