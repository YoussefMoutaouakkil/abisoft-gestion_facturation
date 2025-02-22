package com.skynet.javafx.views;

import org.springframework.context.annotation.Scope;
import com.skynet.javafx.jfxsupport.AbstractFxmlView;
import com.skynet.javafx.jfxsupport.FXMLView;

@FXMLView("/fxml/product.fxml")  // Changed to match standard location
@Scope("prototype")
public class ProductView extends AbstractFxmlView {

}
