package org.sealinc.accurator.client.component;

import java.beans.Beans;
import java.util.Date;
import java.util.List;
import org.sealinc.accurator.client.Utility;
import org.sealinc.accurator.shared.Annotation;
import org.sealinc.accurator.shared.Config;
import org.sealinc.accurator.shared.Review;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
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
	private DateTimeFormat dtFormat = DateTimeFormat.getFormat("dd-MM-yyyy");

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

		Column<Annotation, Date> column = new Column<Annotation, Date>(new DateCell(dtFormat)) {
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
		column_1.setFieldUpdater(new FieldUpdater<Annotation, String>() {
			@Override
			public void update(int index, Annotation object, String value) {
				Review rev = new Review();
				rev.reviewer = Utility.getQualifiedUsername();
				rev.approved = true;
				rev.date = new Date();
				Utility.userService.setReview(object.uri, rev, new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						System.out.println("Review result: " + result);
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});
			}
		});
		cellTable.addColumn(column_1, "Goed");

		Column<Annotation, String> column_2 = new Column<Annotation, String>(new ButtonCell()) {
			@Override
			public String getValue(Annotation object) {
				return "Fout";
			}
		};
		column_2.setFieldUpdater(new FieldUpdater<Annotation, String>() {
			@Override
			public void update(int index, Annotation object, String value) {
				Review rev = new Review();
				rev.reviewer = Config.userComponentUserURI + Utility.getStoredUsername();
				rev.approved = false;
				rev.date = new Date();
				Utility.userService.setReview(object.uri, rev, new AsyncCallback<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						System.out.println("Review result: " + result);
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});
			}
		});
		cellTable.addColumn(column_2, "Fout");

		if (!Beans.isDesignTime()) {
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

	private void loadTrustValues() {
		for (final Annotation a : annotations) {
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
