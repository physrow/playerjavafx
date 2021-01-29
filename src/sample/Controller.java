package sample;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.media.AudioEqualizer;

import javafx.scene.media.EqualizerBand;



import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Formatter;


public class Controller {

    private String path;
    private MediaPlayer media_player;

    public static Controller controller;
    public Slider equalize;

    public static Controller getController() {
        return controller;
    }

    public static void setController(Controller controller) {
        Controller.controller = controller;
    }

    public void initialize() {
        Controller.setController(this);
        controller.set_playlist();
    }

    private MediaPlayer mediaPlayer;
    private MediaPlayer _mediaPlayer;


    @FXML
    private MediaView mediaView;

    @FXML
    private Slider progressBar;

    @FXML
    private VBox vBox;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Label song_name;

    @FXML
    private Label l_name;
    @FXML
    private Label l_artist;
    @FXML
    private Label l_listeners;
    @FXML
    private Label l_duration;

    @FXML
    private final Slider[] sliders = new Slider[10];



    String playlist_file = "C:\\Users\\Alexander\\Desktop\\playlist.txt";
    FileWriter playlist_file_writer;
    FileReader playlist_file_reader = new FileReader("C:\\Users\\Alexander\\Desktop\\playlist.txt");

    @FXML
    private TableColumn<String, String> name;

    @FXML
    private TableColumn<String, Integer> duration;

    static public ObservableList<File> selected_files;

    @FXML
    private ListView<File> list_of_songs;

    @FXML
    public final AudioEqualizer equalizer() {
        AudioEqualizer eq = media_player.getAudioEqualizer();

        ObservableList<EqualizerBand> band_list = eq.getBands();
        for (char n = 0; n < 10; n++) {
            EqualizerBand eq_band = band_list.get(n);
            eq_band.setGain(EqualizerBand.MAX_GAIN);
            band_list.set(n, eq_band);

        }
        return eq;
    }

    public Controller() throws IOException {
    }

    public void http(String song_full_name, String artist, String album, String query) {
        Formatter f = new Formatter();
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(query).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            StringBuilder sb = new StringBuilder();

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader in = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                if (!sb.toString().split(",")[0].equals("{\"error\":6")) {
                    JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
                    JsonElement duration_ = jsonObject.getAsJsonObject("album").getAsJsonObject("tracks").getAsJsonArray("track").get(0).getAsJsonObject().get("duration");
                    JsonElement listeners_ = jsonObject.getAsJsonObject("album").get("listeners");
                    JsonElement name_ = jsonObject.getAsJsonObject("album").get("name");
                    JsonElement artist_ = jsonObject.getAsJsonObject("album").get("artist");
                    Integer duration_min = duration_.getAsInt() / 60;
                    Integer duration_sec = duration_.getAsInt() - duration_min * 60;
                    String tererere = f.format("%d:%d", duration_min, duration_sec).toString();
                    l_name.setText(name_.getAsString());
                    l_artist.setText(artist_.getAsString());
                    l_listeners.setText(listeners_.getAsString());
                    l_duration.setText(tererere);

                }


            } else {
                System.out.println("Error " + connection.getResponseCode() + "," + connection.getResponseMessage());
            }
        } catch (Throwable cause) {
            cause.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public Label get_song_name(){
        return song_name;
    }

    public void set_playlist() {
        try {
            ObservableList<File> list = FXCollections.observableArrayList();
            BufferedReader reader = new BufferedReader(playlist_file_reader);
            String line = reader.readLine();
            StringBuilder sb = new StringBuilder();
            sb.append(line);
            int line_ = sb.toString().split(",").length;
            for (int i = 0; i < line_; ++i) {
                File a = new File(sb.toString().split(",")[i].replaceAll("\\[", "").replaceAll("]", ""));
                list.add(a);
            }
            list_of_songs.setItems(list);
            path = list.get(0).toURI().toString();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            song_name.setText(path.split("/")[path.split("/").length - 1].replaceAll("%20", " "));

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    progressBar.setValue(newValue.toSeconds());

                }
            });



            progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
                }
            });

            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
                }
            });

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    Duration time = media.getDuration();
                    progressBar.setMax(time.toSeconds());
                }
            });
            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                }
            });
        } catch (Throwable cause) {
            cause.printStackTrace();
            System.out.println("tyt gg");
        }


    }


    public void choose_method(javafx.event.ActionEvent event) throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        ObservableList<File> list = FXCollections.observableArrayList();
        FileChooser fileChooser = new FileChooser();
        list.addAll(fileChooser.showOpenMultipleDialog(null));
        list_of_songs.setItems(list);
        path = list.get(0).toURI().toString();
        playlist_file_writer = new FileWriter("C:\\Users\\Alexander\\Desktop\\playlist.txt", false);
        playlist_file_writer.write(list.toString());
        playlist_file_writer.close();


        if (list_of_songs != null) {
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            song_name.setText(path.split("/")[path.split("/").length - 1].replaceAll("%20", " "));


            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    progressBar.setValue(newValue.toSeconds());

                }
            });

            progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
                }
            });

            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
                }
            });

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    Duration time = media.getDuration();
                    progressBar.setMax(time.toSeconds());
                }
            });
            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                }
            });


        }
    }

    public void set_song() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        path = list_of_songs.getSelectionModel().getSelectedItem().toURI().toString().replaceAll("C:/Users/Alexander/IdeaProjects/PLAYERXXXXXX/%20", "");
        System.out.println(path);
        Media media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        song_name.setText(path.split("/")[path.split("/").length - 1].replaceAll("%20", " "));
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                progressBar.setValue(newValue.toSeconds());

            }
        });

        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration time = media.getDuration();
                progressBar.setMax(time.toSeconds());
            }
        });
        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });

        String song_full_name = path.split("/")[path.split("/").length - 1].replaceAll("%20", " ");
        String artist = song_full_name.split(" -")[0].replaceAll(" ", "%20");
        String album = song_full_name.split("-")[1].split("\\.")[0].replaceFirst(" ", "").replaceAll(" ", "%20");
        String query = "https://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=3b5b2c649ba6affaea95dc5e7d91963b&artist=" + artist + "&album=" + album + "&format=json";
        http(song_full_name, artist, album, query);

    }


    public void play(javafx.event.ActionEvent event) {
        if (_mediaPlayer != null) {
            mediaPlayer.stop();
            _mediaPlayer = mediaPlayer;
        }
        mediaPlayer.play();


        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                progressBar.setValue(mediaPlayer.getCurrentTime().toSeconds());
            }
        });
    }


    public void pause(javafx.event.ActionEvent event) {
        mediaPlayer.pause();
    }

    public void stop(javafx.event.ActionEvent event) {
        mediaPlayer.stop();
    }

    public void open(javafx.event.ActionEvent event) throws IOException {
        Runtime.getRuntime().exec("c:\\Program Files\\Dolby\\Dolby DAX2\\DAX2_APP\\DolbyDAX2DesktopUI.exe", null, new File("c:\\program files\\test\\"));
    }

    private int clicked_slow = 0;

    public void slow(javafx.event.ActionEvent event) {
        ++clicked_slow;
        if (clicked_slow % 2 == 0) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(0.5);
        }
    }

    private int clicked_fast = 0;

    public void fast(javafx.event.ActionEvent event) {
        ++clicked_fast;
        if (clicked_fast % 2 == 0) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(2);
        }
    }

}