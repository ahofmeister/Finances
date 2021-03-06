package de.finances.view.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import de.finances.application.model.MonthOverView;
import de.finances.application.model.Transaction;
import de.finances.application.model.TransactionType;
import de.finances.application.service.TransactionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class StatisticsOverviewController implements Initializable {

	private TransactionService transactionService = new TransactionService();

	@FXML
	private Pane entityPane;

	@FXML
	public TableView<MonthOverView> monthOverviewTable;

	@FXML
	public TableColumn<MonthOverView, Integer> yearColumn;

	@FXML
	public TableColumn<MonthOverView, Month> monthColumn;

	@FXML
	public TableColumn<MonthOverView, BigDecimal> incomeColumn;

	@FXML
	public TableColumn<MonthOverView, BigDecimal> expenseColumn;

	@FXML
	public TableColumn<MonthOverView, BigDecimal> profitColumn;

	private void buildTransactionEdit(final Transaction item) {

	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

		this.monthOverviewTable.setRowFactory(tv -> {
			final TableRow<MonthOverView> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					final FXMLLoader loader = new FXMLLoader();
					loader.setLocation(this.getClass().getClassLoader().getResource("fxml/monthOverview.fxml"));
					Pane editPane = null;
					try {
						editPane = (Pane) loader.load();
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					final MonthOverviewController controller = loader.getController();
					controller.setMonthOverview(row.getItem());

					this.entityPane.getChildren().clear();
					this.entityPane.getChildren().add(editPane);

				}
			});
			return row;
		});

		this.yearColumn.setCellValueFactory(new PropertyValueFactory<MonthOverView, Integer>("year"));
		this.monthColumn.setCellValueFactory(new PropertyValueFactory<MonthOverView, Month>("month"));
		this.incomeColumn.setCellValueFactory(new PropertyValueFactory<MonthOverView, BigDecimal>("income"));
		this.expenseColumn.setCellValueFactory(new PropertyValueFactory<MonthOverView, BigDecimal>("expense"));
		this.profitColumn.setCellValueFactory(new PropertyValueFactory<MonthOverView, BigDecimal>("profit"));

		final List<Transaction> allTransactions = this.transactionService.listAll();

		final Map<YearMonth, List<Transaction>> expenses = allTransactions.stream()
				.collect(Collectors.groupingBy(t -> YearMonth.from(t.getCreated())));

		expenses.forEach((date, transactions) -> {

			final BigDecimal sumExpense = transactions.stream().filter(t -> t.getType() == TransactionType.EXPENSE)
					.map(Transaction::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
			final BigDecimal sumIncome = transactions.stream().filter(t -> t.getType() == TransactionType.INCOME)
					.map(Transaction::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);

			final MonthOverView monthOverView = new MonthOverView(date.getYear(), date.getMonth(), sumIncome,
					sumExpense);
			this.monthOverviewTable.getItems().add(monthOverView);

		});

	}

}
