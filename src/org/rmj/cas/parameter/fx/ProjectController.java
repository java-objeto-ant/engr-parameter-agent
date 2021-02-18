package org.rmj.cas.parameter.fx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.callback.IFXML;
import org.rmj.cas.parameter.agent.XMProject;
import java.util.Date;

public class ProjectController implements Initializable, IFXML {
    @FXML
    private VBox VBoxForm;
    @FXML
    private Button btnExit;
    @FXML
    private AnchorPane anchorField;
    @FXML
    private TextField txtField01;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnActivate;
    @FXML
    private TextField txtField02;
    @FXML
    private FontAwesomeIconView glyphExit;
    @FXML
    private CheckBox Check09;
    @FXML
    private DatePicker datePicker03;
    @FXML
    private DatePicker datePicker04;
    @FXML
    private DatePicker datePicker05;
    @FXML
    private DatePicker datePicker06;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*Initialize class*/
        poRecord = new XMProject(poGRider, poGRider.getBranchCode(), false);
                
        /*Set action event handler for the buttons*/
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnExit.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnSearch.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnActivate.setOnAction(this::cmdButton_Click);
                
        /*Add listener to text fields*/
        txtField02.focusedProperty().addListener(txtField_Focus);
                
        pnEditMode = EditMode.UNKNOWN;
        
        clearFields();
        initButton(pnEditMode);
        
        pbLoaded = true;
    }    
    
    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        
        switch (event.getCode()){
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton){
            case "btnBrowse":
                if (poRecord.browseRecord("%", false)){
                    clearFields();
                    loadRecord();
                    pnEditMode = poRecord.getEditMode();
                }
                break;
            case "btnCancel":
                if(ShowMessageFX.OkayCancel(getStage(), null, pxeModuleName, "Do you want to disregard changes?")== true){
                    clearFields();
                    pnEditMode = EditMode.UNKNOWN;
                    break;
                } else return;
            case "btnClose":
            case "btnExit":
                unloadForm();
                return;
            case "btnNew":
                if (poRecord.newRecord()){
                    clearFields();
                    loadRecord(); 
                    pnEditMode = poRecord.getEditMode();
                } else{
                    ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                    return;
                }
                break;
            case "btnSave":
                if (datePicker03.getValue() != null)
                    poRecord.setMaster(3, java.sql.Date.valueOf(datePicker03.getValue()));
                else 
                    poRecord.setMaster(3, null);
                
                if (datePicker04.getValue() != null)
                    poRecord.setMaster(4, java.sql.Date.valueOf(datePicker04.getValue()));
                else 
                    poRecord.setMaster(4, null);
                
                if (datePicker05.getValue() != null)
                    poRecord.setMaster(5, java.sql.Date.valueOf(datePicker05.getValue()));
                else 
                    poRecord.setMaster(5, null);
                
                if (datePicker06.getValue() != null)
                    poRecord.setMaster(6, java.sql.Date.valueOf(datePicker06.getValue()));
                else 
                    poRecord.setMaster(6, null);
                
                if (poRecord.saveRecord()){
                    clearFields();
                    openRecord(psOldRec);
                    ShowMessageFX.Information(getStage(), null, pxeModuleName, "Record Save Successfully.");
                } else{
                    ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                    return;
                }
                break;
            case "btnSearch":
                break;
            case "btnUpdate":
                if (poRecord.getMaster(1) != null && !poRecord.getMaster(1).toString().equals("")){
                    if (poRecord.updateRecord()){
                        pnEditMode = poRecord.getEditMode();
                    }
                }
                break;
            case "btnActivate":
                if (poRecord.getMaster(1) != null && !poRecord.getMaster(1).toString().equals("")){
                    if (btnActivate.getText().equals("Activate")){
                        if (poRecord.activateRecord(poRecord.getMaster(1).toString())){
                            clearFields();
                            openRecord(psOldRec);
                            ShowMessageFX.Information(getStage(), null, pxeModuleName, "Record Activated Successfully.");
                        }else{
                            ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                            return;
                        }
                    } else{
                        if (poRecord.deactivateRecord(poRecord.getMaster(1).toString())){
                            clearFields();
                            openRecord(psOldRec);
                            ShowMessageFX.Information(getStage(), null, pxeModuleName, "Record Deactivated Successfully.");
                        }else{
                            ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                            return;
                        }
                    }
                }
                break;
            default:
                ShowMessageFX.Warning(getStage(), null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                return;
        }
        
        initButton(pnEditMode);
    } 
    
    private void unloadForm(){
        CommonUtils.closeStage(btnNew);
    }
    
    private Stage getStage(){
        return (Stage) btnNew.getScene().getWindow();
    }
    
    private void openRecord(String fsRecordID){
        if (poRecord.openRecord(fsRecordID)){
            loadRecord();
            pnEditMode = poRecord.getEditMode();
        }
    }
    
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
                
        btnClose.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnActivate.setVisible(!lbShow);
        btnUpdate.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtField01.setDisable(true);
        txtField02.setDisable(!lbShow);
        datePicker03.setDisable(!lbShow);
        datePicker04.setDisable(!lbShow);
        datePicker05.setDisable(!lbShow);
        datePicker06.setDisable(!lbShow);
        
        Check09.setDisable(true);
        
        if (lbShow)
            txtField02.requestFocus();
        else
            btnNew.requestFocus();
    }
    
    private void clearFields(){
        txtField01.setText("");
        txtField02.setText("");
        datePicker03.setValue(null);
        datePicker04.setValue(null);
        datePicker05.setValue(null);
        datePicker06.setValue(null);
        
        Check09.selectedProperty().setValue(false);
        btnActivate.setText("Activate");
        
        psOldRec = "";
        psInvType = "";
    }
    
    private void loadRecord(){
        txtField01.setText((String) poRecord.getMaster(1));
        txtField02.setText((String) poRecord.getMaster(2));
        
        Date ldDate;
        if (poRecord.getMaster(3) != null){
            ldDate = (Date) poRecord.getMaster(3);
            datePicker03.setValue(new java.sql.Date(ldDate.getTime()).toLocalDate());
        }
            
        if (poRecord.getMaster(4) != null){
            ldDate = (Date) poRecord.getMaster(4);
            datePicker04.setValue(new java.sql.Date(ldDate.getTime()).toLocalDate());
        }
        
        if (poRecord.getMaster(5) != null){
            ldDate = (Date) poRecord.getMaster(5);
            datePicker05.setValue(new java.sql.Date(ldDate.getTime()).toLocalDate());
        }
        
        if (poRecord.getMaster(6) != null){
            ldDate = (Date) poRecord.getMaster(6);
            datePicker06.setValue(new java.sql.Date(ldDate.getTime()).toLocalDate());
        }
                
        boolean lbCheck = poRecord.getMaster("cRecdStat").toString().equals("1");
        Check09.selectedProperty().setValue(lbCheck);
        
        if (poRecord.getMaster("cRecdStat").toString().equals("1"))
            btnActivate.setText("Deactivate");
        else
            btnActivate.setText("Activate");
        
        psOldRec = txtField01.getText();
    }
            
    @Override
    public void setGRider(GRider foGRider){
        this.poGRider = foGRider;
    }
    
    private final String pxeModuleName = this.getClass().getSimpleName();
    private GRider poGRider;
    private XMProject poRecord;
    
    private int pnEditMode;
    private boolean pbLoaded = false;
    private String psOldRec;
    private String psInvType = ""; /*search description container*/
   
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 2:
                    if (lsValue.length() > 128) lsValue = lsValue.substring(0, 128);
                    
                    poRecord.setMaster(lnIndex, lsValue);
                    txtField.setText((String)poRecord.getMaster(lnIndex));
                    break;
                default:
                    ShowMessageFX.Warning(getStage(), null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
            }
        } else
            txtField.selectAll();
    };
}
