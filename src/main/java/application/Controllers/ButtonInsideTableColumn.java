package application.Controllers;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;

public class ButtonInsideTableColumn<T, V> extends TableCell<T, V> {

    protected final Button button;
    protected final String iconName;
    protected EventHandler<MouseEvent> eventHandler;
    protected String cssId;
    protected String cssClassId;
    protected T rowId;

    public ButtonInsideTableColumn(String iconNameWithExtension, String buttonText) {
        this.iconName = iconNameWithExtension;
        this.button = new Button();
        this.button.setText(buttonText);
        button.setOnAction(mouseEvent -> rowId = getTableView().getItems().get(getIndex()));
    }

    public ButtonInsideTableColumn() {
        this.iconName = "";
        this.button = new Button();
        button.setOnAction(mouseEvent -> rowId = getTableView().getItems().get(getIndex()));
    }

    public void setCssId(String cssId) {
        this.cssId = cssId;
    }

    public void setCssClassId(String classId) {
        this.cssClassId = classId;
    }

    public T getRowId() {
        return rowId;
    }

    public void setEventHandler(EventHandler<MouseEvent> eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    protected void updateItem(V item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            button.setOnMouseClicked(eventHandler);
            button.setGraphic(new ImageView(getClass().getResource("/application/Icons/") + iconName));
            button.setBackground(Background.EMPTY);
            button.setId(cssId);
            button.getStyleClass().add(cssClassId);
            setGraphic(button);
        }
    }
}
