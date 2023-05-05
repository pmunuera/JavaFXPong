import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class CtrlLogin implements Initializable{
    @FXML
    private TextField pseudonim,codi;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        
    }

    @FXML
    public void viewRegistrar(){
        UtilsViews.setViewAnimating("ViewRegistre");
    }

    @FXML
    public void login(){
        if(codi.getText()!=""&&pseudonim.getText()!=""){
            JSONObject obj = new JSONObject("{}");
            obj.put("type", "registrar");
            obj.put("pseudonim",pseudonim.getText());
            obj.put("codi", codi.getText());
            Main.socketClient.safeSend(obj.toString());
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("ERROR");
            alert.setContentText("Has d'omplir tots els camps");
            alert.showAndWait();
        }
    }
    
}
