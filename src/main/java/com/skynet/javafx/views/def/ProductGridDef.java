package com.skynet.javafx.views.def;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import com.skynet.javafx.views.ProductView;

@Component
public class ProductGridDef implements FrameGridDef {

    public static String COLUMN_NAMES[] = 
        { "Id", "Name", "Price", "Category", "Quantity" };
    public static String COLUMN_DATA_NAMES[] = 
        { "id", "name", "price", "categoryName", "quantity" };
    public static Integer COLUMN_SIZES[] = { 40, 200, 100, 150, 100 };
    public static String TITLE_POPUPS = "Product";

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList(COLUMN_NAMES);
    }

    @Override
    public List<Integer> getColumnSizes() {
        return Arrays.asList(COLUMN_SIZES);
    }

    @Override
    public List<String> getColumnDataName() {
        return Arrays.asList(COLUMN_DATA_NAMES);
    }

    @Override
    public Class<?> getCreateView() {
        return ProductView.class;
    }

    @Override
    public String getTitlePopups() {
        return TITLE_POPUPS;
    }
}
