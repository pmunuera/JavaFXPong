import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class CtrlRegistre  implements Initializable{
    @FXML
    private TextField pseudonim,codi1,codi2;

    @FXML
    private ChoiceBox<String> color;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        ArrayList<String> colorsArr = new ArrayList<>();
        colorsArr.add("pink");
        colorsArr.add("purple");
        colorsArr.add("yellow");
        colorsArr.add("lime");
        colorsArr.add("green");
        colorsArr.add("cyan");
        colorsArr.add("blue");
        colorsArr.add("navy");
        colorsArr.add("black");
        color.getItems().addAll(colorsArr);
    }
    
    @FXML
    public void registrar(){
        if(codi1.getText()!=""&&codi2.getText()!=""&&pseudonim.getText()!=""&&color.getSelectionModel().getSelectedItem()!=null){
            if(codi1.getText().equals(codi2.getText())){
                JSONObject obj = new JSONObject("{}");
                obj.put("type", "registrar");
                obj.put("pseudonim",pseudonim.getText());
                obj.put("codi", codi1.getText());
                obj.put("color", color.getValue());
                obj.put("id",CtrlSign.id);
                Main.socketClient.safeSend(obj.toString());
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("ERROR");
                alert.setContentText("Has de posar el mateix codi dos vegades");
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("ERROR");
            alert.setContentText("Has d'omplir tots els camps");
            alert.showAndWait();
        }
        
    }
    @FXML
    public void back(){
        UtilsViews.setViewAnimating("ViewLogin");
    }
}
