import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class CtrlUsuaris implements Initializable {
    @FXML
    private VBox vBoxList = new VBox();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
    }
    @FXML
    public void goBack() {
        UtilsViews.setViewAnimating("ViewSelect");
    }
    public void loadList (String response) {
        
        JSONObject objResponse = new JSONObject(response);
        System.out.println(objResponse);
         if (objResponse.getString("status").equals("OK")) {

            JSONArray JSONlist = objResponse.getJSONArray("result");
            URL resource = this.getClass().getResource("./assets/listItem.fxml");
            
            // Clear the list of consoles
            vBoxList.getChildren().clear();
            // Add received consoles from the JSON to the yPane (VBox) list
            for (int i = 0; i < JSONlist.length(); i++) {

                // Get console information
                JSONObject user = JSONlist.getJSONObject(i);

                    try {
                    // Load template and set controller
                    FXMLLoader loader = new FXMLLoader(resource);
                    Parent itemTemplate = loader.load();
                    ControllerItem itemController = loader.getController();
                        
                    System.out.println(user);
                    // Fill template with console information
                    itemController.setTitle(user.getString("Pseudonim"));                     
                    // Add template to the list
                    vBoxList.getChildren().add(itemTemplate);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
