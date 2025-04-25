package com.skynet.javafx.controller;

import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import com.skynet.javafx.jfxsupport.AbstractFxmlView;
import com.skynet.javafx.jfxsupport.FXMLController;
import com.skynet.javafx.jfxsupport.PrototypeController;
import com.skynet.javafx.model.CompanyInfo;
import com.skynet.javafx.model.Facture;
import com.skynet.javafx.model.SimpleEntity;
import com.skynet.javafx.service.CompanyInfoService;
import com.skynet.javafx.service.CustomerService;
import com.skynet.javafx.service.DevisService;
import com.skynet.javafx.service.ExcelExportable;
import com.skynet.javafx.service.FactureService;
import com.skynet.javafx.service.FrameService;
import com.skynet.javafx.views.def.FrameGridDef;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

@FXMLController
@Scope("prototype")
public class FrameGridController implements PrototypeController {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
	private static final Logger logger = LoggerFactory.getLogger(FrameGridController.class);
	private static final String USERNAME = "root";
	private static final String PASSWORD = "";
	@Autowired
	private ApplicationContext context;
	@FXML
	private Button addButton;
	@FXML
	private Button editButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button exportButton; // Changed from printButton
	@FXML
	private Button printButton;// Changed from printButton
	@FXML
	ProgressBar probar;
	@FXML
	private TableView<SimpleEntity> frameGrid;
	private FrameService frameService;
	private FrameGridDef gridDef;
	private Scene scene;
	@Autowired
	private  CompanyInfoService companyInfoService;

	private boolean isPrintablePage() {
		if (frameService instanceof FactureService || frameService instanceof DevisService) {
			return true;
		}
		return false;
	}
	private boolean isExportablePage() {
		if (frameService instanceof ExcelExportable) {
			return true;
		}
		return false;
	}

	@FXML
	private void initialize() {
		editButton.setDisable(true);
		deleteButton.setDisable(true);
		printButton.setDisable(true);
		exportButton.setDisable(false); // Changed from printButton
		printButton.setVisible(false); // Hide by default
		addButton.setOnAction((event) -> {
			addButtonHandleAction();
		});
		editButton.setOnAction((event) -> {
			editButtonHandleAction();
		});
		deleteButton.setOnAction((event) -> {
			deleteButtonHandleAction();
		});
		exportButton.setOnAction((event) -> {
			handleExport(event);
		}); // Changed from printButtonHandleAction
		printButton.setOnAction((event) -> {
			handlePrint(event);
		});
	}

	private void addButtonHandleAction() {
		AbstractFxmlView fxmlView = showDialog();
		CrudController controller = (CrudController) fxmlView.getFxmlLoader().getController();
		controller.add();
	}

	private void editButtonHandleAction() {
		AbstractFxmlView fxmlView = showDialog();
		CrudController controller = (CrudController) fxmlView.getFxmlLoader().getController();
		SimpleEntity entity = frameGrid.getSelectionModel().getSelectedItem();
		controller.render(entity);
	}

	private void deleteButtonHandleAction() {
		SimpleEntity entity = frameGrid.getSelectionModel().getSelectedItem();
		frameService.delete(entity.getId());
		loadData();
	}

	@FXML
	public void handleExport(ActionEvent event) {
		if (frameService instanceof ExcelExportable) {
			ExcelExportable exportable = (ExcelExportable) frameService;
			try {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Export to Excel");
				fileChooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));

				File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());

				if (file != null) {
					exportable.exportToExcel(file.getAbsolutePath());
					logger.info("Successfully exported data to Excel");
				}
			} catch (IOException e) {
				logger.error("Error exporting to Excel", e);
			}
		}
	}

	SimpleEntity entity;

	@FXML
	public void handlePrint(ActionEvent event) {

		entity = frameGrid.getSelectionModel().getSelectedItem();
		System.out.println("entityId=" + entity.getId());
		if (entity != null) {
			probar.setVisible(true);
			Task<Void> task = createTask1(event);
			Thread t = new Thread(task);
			// t.start();
			probar.progressProperty().bind(task.progressProperty());
			t.start();
		} else {
			System.out.println("null!!!!!!!!!!!!!");
		}
	}

	public void initializeGrid(FrameService frameService, FrameGridDef gridDef) {
		this.frameService = frameService;
		this.gridDef = gridDef;
		printButton.setVisible(isPrintablePage()); // Show only for printable pages
		exportButton.setVisible(isExportablePage()); // Show only for exportable pages
		setupGrid();
		loadData();
	}

	private void setupGrid() {
		List<String> columnNames = gridDef.getColumnNames();
		List<String> columnDataNames = gridDef.getColumnDataName();
		List<Integer> columnSizes = gridDef.getColumnSizes();
		for (int i = 0; i < gridDef.getColumnNames().size(); i++) {
			TableColumn<SimpleEntity, String> column = new TableColumn<>(columnNames.get(i));
			column.setCellValueFactory(
					new PropertyValueFactory<SimpleEntity, String>(columnDataNames.get(i)));
			column.setMinWidth(columnSizes.get(i));
			frameGrid.getColumns().add(column);
		}
		frameGrid.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				editButton.setDisable(false);
				deleteButton.setDisable(false);
				printButton.setDisable(false);
			} else {
				editButton.setDisable(true);
				deleteButton.setDisable(true);
				printButton.setDisable(true);
			}
		});
		frameGrid.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				editButtonHandleAction();
			}
		});
		frameGrid.setOnMousePressed((event) -> {
			if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
				editButtonHandleAction();
			}
		});
	}

	private void loadData() {
		ObservableList<SimpleEntity> data = FXCollections.observableArrayList(frameService.getData());
		if (data != null) {
			logger.debug("loadData, data size: {}", data.size());
			frameGrid.setItems(data);
		}
	}

	private AbstractFxmlView showDialog() {
		AbstractFxmlView fxmlView = (AbstractFxmlView) context.getBean(gridDef.getCreateView());
		Stage stage = new Stage();
		scene = new Scene(fxmlView.getView());
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UTILITY);
		stage.setResizable(false);
		stage.setTitle(gridDef.getTitlePopups());
		stage.setOnHidden((event) -> {
			stage.close();
			SimpleEntity oldSelected = frameGrid.getSelectionModel().getSelectedItem();
			loadData();
			if (oldSelected != null) {
				frameGrid.getSelectionModel().select(oldSelected);
			} else {
				frameGrid.getSelectionModel().select(0);
			}
		});
		stage.show();
		return fxmlView;
	}

	private Task<Void> createTask1(ActionEvent event) {
		return new Task<Void>() {
			@Override
			public Void call() {
				/*
				 * try {
				 * Thread.sleep(50);
				 * } catch (InterruptedException e1) {
				 * // TODO Auto-generated catch block
				 * e1.printStackTrace();
				 * }
				 */

				for (int i = 0; i < 10; i++) {
					updateProgress(i, 10);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				/*
				 * for (double i = 0; i < 3; i = i + 0.2) {
				 * if (isCancelled()) {
				 * break;
				 * }
				 * updateProgress(i, 3);
				 * updateMessage("المرجو الانتظار");
				 * try {
				 * Thread.sleep(50);
				 * } catch (InterruptedException ex) {
				 * return null;
				 * }
				 * }
				 */
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						Connection con = null;
						String unicode = "?useUnicode=yes&characterEncoding=UTF-8";
						String url = "jdbc:mysql://localhost/";
						String db = "gestion_facturation";
						// String driver = "com.mysql.jdbc.Driver";
						try {
							Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							con = DriverManager.getConnection(url + db + unicode, USERNAME, PASSWORD);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						InputStream report = getClass().getClassLoader().getResourceAsStream("jasper/template.jrxml");
						if (report == null)
							System.out.println("raport null ======== ");
						System.out.println("entity.getId()=" + entity.getId());
						CompanyInfo companyInfo = companyInfoService.getCompanyInfo().get(0);
						Facture facture = ((FactureService) frameService).findById(entity.getId());
						String dateFacture = facture.getDateFacture().format(formatter);
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("numFacture", facture.getNumeroFacture());
						params.put("dateFacture", dateFacture);
						params.put("clientName", facture.getClientName());
						params.put("raisonSociale",companyInfo.getRaisonSociale());
						params.put("adresse", companyInfo.getAdresse());
						 // Convert byte[] to InputStream for the logo
						byte[] logoData = companyInfo.getLogo();
						if (logoData != null) {
							params.put("logo", new ByteArrayInputStream(logoData));
						}
						JasperDesign jd;
						try {
							jd = JRXmlLoader.load(report);
							 // Update SQL query to use aliased column names that match the field definitions
                            String sql = "SELECT p.name as name, fp.quantity as quantity, fp.price as price " +
                                       "FROM facture_products fp " +
                                       "JOIN products p ON fp.product_id = p.id " +
                                       "WHERE fp.facture_id = " + entity.getId();
                            
                            JRDesignQuery newQ = new JRDesignQuery();
                            newQ.setText(sql);
                            jd.setQuery(newQ);
                            
                            JasperReport jr = JasperCompileManager.compileReport(jd);
                            JasperPrint jp = JasperFillManager.fillReport(jr, params, con);
							String filePath1 = getpath(facture.getClientName(), facture.getNumeroFacture());

							JasperExportManager.exportReportToPdfFile(jp, filePath1);
							try {
								// Create the process to open the PDF file with the default viewer
								ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", "\"\"",
										filePath1);
								processBuilder.start();
							} catch (IOException e) {
								e.printStackTrace();
								System.out.println("Error opening PDF: " + e.getMessage());
							}
						} catch (JRException e) {

							e.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});
				probar.setVisible(false);
				updateProgress(10, 10);
				return null;
			}
		};
	}

	public String getpath(String name, String numFacture) throws IOException {
		String chemin1 = null;
		String workingDirectory1 = System.getProperty("user.home");
		File Gen1 = new File(workingDirectory1, "AppData/Local/Document_Facturation/pdf_factures/" + name);
		File Gen = new File(Gen1, numFacture.replace('/', '_') + ".pdf");
		if (!Gen1.exists()) {
			Gen1.mkdirs();
		}
		if (Gen.exists()) {
			Gen.delete();
		}
		System.out.println("Gen1=" + Gen1.getAbsolutePath());
		boolean test = Gen.createNewFile();
		System.out.println("test=" + test);
		if (test) {
			chemin1 = Gen.getAbsolutePath().replace('\\', '/');
		}
		return chemin1;
	}
}
