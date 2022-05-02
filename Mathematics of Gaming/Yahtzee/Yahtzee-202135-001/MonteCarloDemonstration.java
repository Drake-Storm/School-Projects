import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.security.SecureRandom;

/**
 *
 * @author 000359041
 * @web http://java-buddy.blogspot.com/2015/07/apply-animaton-in-javafx-charts-with.html
 */

public class MonteCarloDemonstration extends Application {

    final int N = 1_000_000;

    SecureRandom random = new SecureRandom();
    double[] group = new double[ 1576 ]; //  0-1576 for possible yahtzee scores
    int count = 0;
    int over150=0, over200=0;
    int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
    int scores = 0;

    // initialize the data set - makes more sense if your data is objects
    private void prepareData() {
        for (int i = 0; i < group.length; i++) group[i] = 0.0;
    }

    public int doExperiment() {
        int score = new YahtzeeStrategy().play(); // game takes 1-3ms each using the default code

        scores += score;
        max = score > max ? score : max;
        min = score < min ? score : min;
        if (score>=200) over200++;
        else if (score>=150) over150++;

        return score;
    }
    
    @Override
    public void start(Stage primaryStage) {
        prepareData();
 
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart
                = new BarChart<>(xAxis, yAxis);
        barChart.setCategoryGap(0);
        barChart.setBarGap(0);
        barChart.setAnimated(true);
        barChart.setMinHeight(Screen.getPrimary().getVisualBounds().getMaxY()*.65);

        xAxis.setLabel("Score");
        yAxis.setLabel("# of Games");
 
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Yahtzee Score Distribution");
        
        // initialize the bars, all scores to 0%
        for ( int i = 0; i < group.length; i++ ) {
            series1.getData().add(new XYChart.Data<String, Double>(Integer.toString(i), group[i]));
        }
 
        // Intial bar chart is just the axis with no data
        barChart.getData().addAll(series1);

        /*
        Label labelInfo = new Label();
        labelInfo.setText(
                "java.version: " + System.getProperty("java.version") + "\n"
                        + "javafx.runtime.version: " + System.getProperty("javafx.runtime.version")
        );
        */

        Label labelCnt = new Label();
        labelCnt.setText("Iterations: " + count);

        Label labelAnimated = new Label();
        labelAnimated.setText("Min Score:\t\t"+min+"\nMax Score:\t\t"+max+"\nGames>=150:\t\t"+over150+"\nGames>=200:\t\t"+over200+"\nAverage Score:\t\t0");
 
        VBox vBox = new VBox();
        vBox.setLayoutX(Screen.getPrimary().getVisualBounds().getMaxX()*0.50-50);
        vBox.setLayoutY(Screen.getPrimary().getVisualBounds().getMaxY()*.8+25);
        vBox.getChildren().addAll(barChart, labelCnt, labelAnimated);
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);
 
        StackPane root = new StackPane();
        root.getChildren().add(vBox);
 
        Scene scene = new Scene(root, Screen.getPrimary().getBounds().getMaxX()*.8, Screen.getPrimary().getBounds().getMaxY()*.8);

        primaryStage.setTitle("Yahtzee Score Histogram");
        primaryStage.setScene(scene);
        primaryStage.show();


        //Apply Animating Data in Charts
        //ref: http://docs.oracle.com/javafx/2/charts/bar-chart.htm
        //"Animating Data in Charts" section

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame( Duration.millis(1), (ActionEvent actionEvent) -> {
                    int data = doExperiment();
                    count++;
                    ((XYChart.Data) series1.getData().get(data)).setYValue(++group[data]);

                    if ( count == N )
                        labelCnt.setText("iterations: " + count + " (finished)");
                    else
                        labelCnt.setText("iterations: " + count);

                    labelAnimated.setText(String.format(
                            "Min Score:\t\t%d\nMax Score:\t\t%d\nGames>=150:\t\t%d (%2.3f%%)\nGames>=200:\t\t%d (%2.3f%%)\nAverage Score:\t\t%d"
                            , min, max, over150, 100*((double)over150/count), over200, 100*((double)over200/count), ((int)(scores/count))
                    ));
        }));

        timeline.setCycleCount(N);  // number of times to play the animation - 1 frame per cycle in this setup
        timeline.play();

        barChart.setAnimated(false);
         
    }
 
    public static void main(String[] args) {
        launch(args);
    }    
    
}
