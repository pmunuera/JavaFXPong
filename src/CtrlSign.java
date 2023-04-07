import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class CtrlSign implements Initializable{
    @FXML
    private TextField localhost;

    @FXML
    private Label txtError;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        
    }
    @FXML
    public void enter(){
        String[] serverText1=localhost.getText().split("://");
        String[] serverText2=serverText1[1].split(":");
        Main.host=serverText2[0];
        Main.protocolWS=serverText1[0];
        Main.port=Integer.parseInt(serverText2[1]);
        Main.socketClient = UtilsWS.getSharedInstance(Main.protocolWS + "://" + Main.host + ":" + Main.port);
        Main.socketClient.onMessage((response) -> {
            // JavaFX necessita que els canvis es facin des de el thread principal
            Platform.runLater(()->{
                if(response!=null){
                JSONObject msgObj = new JSONObject(response);
                String type = msgObj.getString("type");

                // Update clients choiceBox list
                if (type.equals("clients")) {

                    JSONArray JSONlist = msgObj.getJSONArray("list");
                    ArrayList<String> list = new ArrayList<>();
                    System.out.println(JSONlist);
                    if(JSONlist.length()==1){
                        Main.playerId=(String) JSONlist.get(0);
                    }
                    else if (JSONlist.length()==2){
                        Main.playerId=(String) JSONlist.get(1);
                    }
                    UtilsViews.setViewAnimating("ViewGame");
                    CtrlGameCanvas.start=true;
                    System.out.println(Main.playerId);
                }
            }else{
                    showError();
                }

        });
    });
    }

    public void enterCallback(String response){

        JSONObject objResponse = new JSONObject(response);
        if (objResponse.getString("status").equals("OK")) {
            UtilsViews.setViewAnimating("ViewGame");
        }
        else{
            showError();
        }
    }

    private void showError () {
        // Show the error
        txtError.setVisible(true);
        txtError.setText("Error amb el servidor o port introduit");
        // Hide the error after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), ae -> txtError.setVisible(false)));
        timeline.play();
    }

}
