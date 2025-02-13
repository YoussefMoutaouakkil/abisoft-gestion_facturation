package com.skynet.javafx.views.def;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import com.skynet.javafx.views.CategoryView;

@Component
public class CategoryGridDef implements FrameGridDef {

    public static String COLUMN_NAMES[] = 
        { "Id", "Name", "Description" };
    public static String COLUMN_DATA_NAMES[] = 
        { "id", "name", "description" };
    public static Integer COLUMN_SIZES[] = { 40, 200, 300 };
    public static String TITLE_POPUPS = "Category";

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
        return CategoryView.class;
    }

    @Override
    public String getTitlePopups() {
        return TITLE_POPUPS;
    }
}
