// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.media.Media;
// import javafx.scene.media.MediaPlayer;
// import javafx.scene.media.MediaView;
// import javafx.stage.Stage;
//
// import java.io.File;
//
// public class VideoPlayer extends Application {
//     @Override
//     public void start(Stage primaryStage) {
//         // Replace with your video file path
//         String videoPath = "C:\\Users\\Siddharth P\\OneDrive\\Desktop\\Game\\bomb_blast.mp4";
//         Media media = new Media(new File(videoPath).toURI().toString());
//         MediaPlayer mediaPlayer = new MediaPlayer(media);
//         MediaView mediaView = new MediaView(mediaPlayer);
//
//         // Play the video
//         mediaPlayer.play();
//
//         Scene scene = new Scene(mediaView, 800, 600);
//         primaryStage.setTitle("Bomb Blast Video Player");
//         primaryStage.setScene(scene);
//         primaryStage.setResizable(false);
//         primaryStage.show();
//     }
//
//     public static void main(String[] args) {
//         launch(args);
//     }
// }
