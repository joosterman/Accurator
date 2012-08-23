package org.sealinc.accurator.client.component;

import java.beans.Beans;
import java.util.Date;
import java.util.List;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.Annotation;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QualityScreen extends Composite {
	private CellTable<Annotation> cellTable;
	private List<Annotation> annotations;

	public QualityScreen() {
		
		VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		
		cellTable = new CellTable<Annotation>();
		cellTable.setStyleName("wide");
		verticalPanel.add(cellTable);
		
		TextColumn<Annotation> textColumn = new TextColumn<Annotation>() {
			@Override
			public String getValue(Annotation object) {
				return object.hasBody;
			}
		};
		cellTable.addColumn(textColumn, "Annotatie");
		
		TextColumn<Annotation> textColumn_1 = new TextColumn<Annotation>() {
			@Override
			public String getValue(Annotation object) {
				return object.annotationField;
			}
		};
		cellTable.addColumn(textColumn_1, "Veld");
		
		TextColumn<Annotation> textColumn_2 = new TextColumn<Annotation>() {
			@Override
			public String getValue(Annotation object) {
				return object.annotator;
			}
		};
		cellTable.addColumn(textColumn_2, "Annotator");
		
		Column<Annotation, Date> column = new Column<Annotation, Date>(new DateCell(DateTimeFormat.getFormat("dd-MM-yyyy"))) {
			@Override
			public Date getValue(Annotation object) {
				return object.annotated;
			}
		};
		cellTable.addColumn(column, "Datum annotatie");
		
		TextColumn<Annotation> textColumn_3 = new TextColumn<Annotation>() {			
			@Override
			public String getValue(Annotation object) {
				return Double.toString(object.trustworthiness);
			}
		};
		cellTable.addColumn(textColumn_3, "Trust");
		
		Column<Annotation, String> column_1 = new Column<Annotation, String>(new ButtonCell()) {
			@Override
			public String getValue(Annotation object) {
				return "Goed";
			}
		};
		cellTable.addColumn(column_1, "Goed");
		
		Column<Annotation, String> column_2 = new Column<Annotation, String>(new ButtonCell()) {
			@Override
			public String getValue(Annotation object) {
				return "Fout";
			}
		};
		cellTable.addColumn(column_2, "Fout");
		
		
		if(!Beans.isDesignTime()){
			Utility.qualityService.getRecentAnnotations(new AsyncCallback<List<Annotation>>() {
				
				@Override
				public void onSuccess(List<Annotation> result) {
					annotations = result;
					cellTable.setRowData(annotations);
					loadTrustValues();
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	private void loadTrustValues(){
		for(final Annotation a:annotations){
			Utility.qualityService.getTrustworthiness(a.uri, new AsyncCallback<Double>() {
				
				@Override
				public void onSuccess(Double result) {
					a.trustworthiness = result;
					cellTable.redraw();
				}
				
				@Override
				public void onFailure(Throwable caught) {

					
				}
			});
		}
	}
}
