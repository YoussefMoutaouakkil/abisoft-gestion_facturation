package com.skynet.javafx.views.def;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import com.skynet.javafx.views.CustomerView;

@Component
public class CustomerGridDef implements FrameGridDef {

	public static String COLUMN_NAMES[] = 
		{ "Id", "Type", "Name", "Address", "Email", "ICE"};
	public static String COLUMN_DATA_NAMES[] = 
		{ "id", "type", "name",  "address", "email", "ICE" };
	public static Integer COLUMN_SIZES[] = { 40, 100, 150, 150, 200, 200, 100 };
	public static String TITLE_POPUPS = "Customer";

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
		return CustomerView.class;
	}

	@Override
	public String getTitlePopups() {
		return TITLE_POPUPS;
	}

}
