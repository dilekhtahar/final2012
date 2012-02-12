package sql2sparql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TGroupByItemList;
import gudusoft.gsqlparser.nodes.TJoinList;
import gudusoft.gsqlparser.nodes.TLimitClause;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TWhereClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public abstract class SELECTStatementProcessor {
	// Constants
	public static String VALID = "VALID QUERY";
	public static String INVALID = "INVALID QUERY";

	/**
	 * Validates a query
	 * 
	 * @param selectStatement
	 * @return
	 */
	public static String validateQuery(String selectStatement) {
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
		sqlparser.sqltext = selectStatement;
		int ret = sqlparser.parse();
		if (ret == 0) {
			return VALID;
		} else {
			return INVALID + ": " + sqlparser.getErrormessage();
		}
	}

	/**
	 * Parses query calling associated translator for mapping
	 * 
	 * @param selectStatement
	 * @return
	 */
	public static String processQuery(String selectStatement) {
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
		sqlparser.setSqltext(selectStatement);

		int ret = sqlparser.parse();
		if (0 == ret) {
			TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements
					.get(0);
			TJoinList joinList = select.joins;// tables list of From clause
			// list of mappings
			if (1 == joinList.size()) {
				// return simpleLightSelect(select);
				// } else if (1 == joinList.size() && (null != where)) {
				return simpleLightSelectWhere(select);
			} else if (1 > joinList.size()) {
				return "TRU";
			} else if (true) {
				return "TRU";
			}
		} else {
			return INVALID + ": " + sqlparser.getErrormessage();
		}
		return "Nothing";
	}

	private static String simpleLightSelectWhere(TSelectSqlStatement select) {

		// PREFIXES PART
		String SPARQLPrefixes = buildPrefixes(select);

		// SELECT WHERE
		String SPARQLSelect = "SELECT";
		String SPARQLWhere = "\nWHERE{";

		TResultColumnList listOfFields = select.getResultColumnList();
		TJoinList joinList = select.joins;// tables list of From clause
		for (int i = 0; i < listOfFields.size(); i++) {
			SPARQLSelect += " ?" + listOfFields.getElement(i).toString();
			SPARQLWhere += "\n\t?x " + joinList.getElement(0).toString() + ":"
					+ listOfFields.getElement(i).toString() + " ?"
					+ listOfFields.getElement(i).toString() + " .";
		}
		// FILTER
		String SPARQLFilter = "";
		TWhereClause where = select.getWhereClause();
		if (null != where) {
			SPARQLFilter = "\n\tFILTER(";
			TExpression condition = where.getCondition();
			String str = precessRecursiveWhere(condition);
			SPARQLFilter += str;
			SPARQLFilter += ")";
		}

		SPARQLWhere += SPARQLFilter + "\n}";

		// GROUP BY
		String SPARQLGroubBy = buildGroupBy(select);

		// ORDER BY
		String SPARQLOrderBy = buildOrderBy(select);

		// LIMIT
		String SPARQLLimit = buildLimit(select);

		String SPARQLQuery = SPARQLPrefixes + SPARQLSelect + SPARQLWhere
				+ SPARQLGroubBy + SPARQLOrderBy + SPARQLLimit;
		return SPARQLQuery;
	}

	private static String buildLimit(TSelectSqlStatement select) {
		String SPARQLLimit = "";
		if (null != select.getLimitClause()) {
			SPARQLLimit += "\n"
					+ ((TLimitClause) select.getLimitClause()).toString();
		}
		return SPARQLLimit;
	}

	private static String buildOrderBy(TSelectSqlStatement select) {
		String SPARQLOrderBy = "";
		if (null != select.getOrderbyClause()) {
			SPARQLOrderBy += "\nORDER BY";
			TOrderByItemList listOrderBy = select.getOrderbyClause().getItems();
			for (int i = 0; i < listOrderBy.size(); i++) {
				int sortType = ((TOrderByItem) listOrderBy.getElement(i))
						.getSortType();
				if (sortType == 2)// DESC
				{
					SPARQLOrderBy += " DESC(?"
							+ ((TOrderByItem) listOrderBy.getElement(i))
									.getSortKey().toString() + ")";
				} else {// default - ASC
					SPARQLOrderBy += " ?"
							+ ((TOrderByItem) listOrderBy.getElement(i))
									.getSortKey().toString();
				}
			}
		}
		return SPARQLOrderBy;
	}

	private static String buildGroupBy(TSelectSqlStatement select) {
		String SPARQLGroubBy = "";
		if (null != select.getGroupByClause()) {
			SPARQLGroubBy += "\nGROUP BY";
			TGroupByItemList listGrBy = select.getGroupByClause().getItems();
			for (int i = 0; i < listGrBy.size(); i++) {
				SPARQLGroubBy += " ?" + listGrBy.getElement(i).toString();
			}
		}
		return SPARQLGroubBy;
	}

	private static String buildPrefixes(TSelectSqlStatement select) {
		String SPARQLPrefixes = "";

		for (int i = 0; i < select.tables.size(); i++) {
			if (SQL2SPARQLConsts.NAMESPACES.containsKey(select.tables
					.getElement(i).toString())) {
				SPARQLPrefixes += "PREFIX "
						+ select.tables.getElement(i).toString()
						+ " : <"
						+ SQL2SPARQLConsts.NAMESPACES.get(select.tables
								.getElement(i).toString()) + ">\n";
			} else {
				SPARQLPrefixes += "PREFIX "
						+ select.tables.getElement(i).toString()
						+ ": <"
						+ SQL2SPARQLConsts.NAMESPACES
								.get(SQL2SPARQLConsts.DEFAULT_NS) + "/"
						+ select.tables.getElement(i).toString() + ">\n";
			}
		}
		return SPARQLPrefixes;
	}

	public static String precessRecursiveWhere(TExpression condition) {
		String str = "";
		if (condition.getOperatorToken() == null) {
			TExpression left = condition.getLeftOperand();
			TExpression right = condition.getRightOperand();
			String operator = condition.toString().substring(
					condition.toString().lastIndexOf(left.toString())
							+ left.toString().length(),
					condition.toString().lastIndexOf(right.toString()));

			str += "?" + left + operator + right.toString().replaceAll("'", "");

			return str;
		} else {
			String operator = condition.getOperatorToken().toString();

			if (operator.equalsIgnoreCase("LIKE")) {
				String[] lr = condition.toString().split(operator);
				String left = lr[0];
				String right = lr[1];
				left = left.substring(0, left.length() - 1);
				right = right.substring(1, right.length());
				// regex(?ename, "^S")
				str += " regex(?" + left + ",\"" + right.replaceAll("'", "")
						+ "\") ";
			} else {

				TExpression left = condition.getLeftOperand();
				TExpression right = condition.getRightOperand();
				String recLeft = precessRecursiveWhere(left);
				String recRight = precessRecursiveWhere(right);
				if (operator.equalsIgnoreCase("or"))
					operator = " || ";
				else if (operator.equalsIgnoreCase("and"))
					operator = " && ";
				str += recLeft + operator + recRight;

			}
			return str;
		}
	}
}
