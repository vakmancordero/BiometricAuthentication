

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class AutoCompleteTextField extends TextField {
    
    private final SortedSet<String> entries;
    
    private ContextMenu entriesPopup;

    public AutoCompleteTextField() {
        
        super();
        
        entries = new TreeSet<>();
        entriesPopup = new ContextMenu();
    
        this.textProperty().addListener(new ChangeListener<String>() {
            
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                
                if (getText().length() == 0) {
                    
                    entriesPopup.hide();
                    
                } else {
                    
                    LinkedList<String> searchResult = new LinkedList<>();
          
                    entries.add("Hola");
                    entries.add("Arturo");
                    entries.add("asdasdo");
                    entries.add("asd");
          
                    searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
                    
                    if (entries.size() > 0) {
                        
                        populatePopup(searchResult);
                        
                        if (!entriesPopup.isShowing()) {
                            
                            entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                        }
                        
                    } else {
                        entriesPopup.hide();
                    }
                }
            }
        });

        focusedProperty().addListener(new ChangeListener<Boolean>() {
            
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                
                entriesPopup.hide();
                
            }
            
        });
        
    }
    
    public SortedSet<String> getEntries() {
        
        return entries; 
    
    }
    
    private void populatePopup(List<String> searchResult) {

        List<CustomMenuItem> menuItems = new LinkedList<>();

        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);

        for (int i = 0; i < count; i++) {

            String result = searchResult.get(i);

            Label entryLabel = new Label(result);

            CustomMenuItem item = new CustomMenuItem(entryLabel, true);

            item.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent actionEvent) {

                    setText(result);

                    System.out.println(result);

                    entriesPopup.hide();
                }
            });

            menuItems.add(item);

        }

        entriesPopup.getItems().clear();

        entriesPopup.getItems().addAll(menuItems);

    }

}