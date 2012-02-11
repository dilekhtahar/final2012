package sql2sparql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TGroupByItemList;
import gudusoft.gsqlparser.nodes.TJoinList;
import gudusoft.gsqlparser.nodes.TLimitClause;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TWhereClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public abstract class SELECTStatementProcessor {
	public static String VALID = "VALID QUERY";
	public static String INVALID = "INVALID QUERY";

	/**
	 * This method returns an SPARQL query </br> selectStatement should be
	 * validate before processed
	 * 
	 * @param selectStatement
	 * @return
	 */
	public static String processQuery1(String selectStatement) {
		String SPARQLSelect = "";
		String SPARQLWhere = "";
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
		sqlparser.setSqltext(selectStatement);

		int ret = sqlparser.parse();
		if (0 == ret) {
			TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements
					.get(0);
			TResultColumnList listOfFields = select.getResultColumnList();
			TJoinList joinList = select.joins;// tables list of From clause

			TWhereClause where = select.getWhereClause();
			if (select.getGroupByClause() != null) {
				for (int i = 0; i < listOfFields.size(); i++) {

				}
			}
			SPARQLSelect = "SELECT ";
			SPARQLWhere = "\nWHERE{";
			if (joinList.size() == 1) {
				for (int i = 0; i < listOfFields.size(); i++) {

					SPARQLSelect += "?" + listOfFields.getElement(i).toString();
					SPARQLWhere += "\n\t?x "
							+ joinList.getElement(0).toString() + ":"
							+ listOfFields.getElement(i).toString() + " ?"
							+ listOfFields.getElement(i).toString() + " .";
				}
			}
			SPARQLWhere += "\n}";
		}
		String SPARQLQuery = SPARQLSelect + SPARQLWhere;
		return SPARQLQuery;
	}

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
			TResultColumnList listOfFields = select.getResultColumnList();
			TJoinList joinList = select.joins;// tables list of From clause

			TWhereClause where = select.getWhereClause();

			// list of mappings
			if (1 == joinList.size() && (null == where)) {
				return simpleLightSelect(select);
			} else if (1 == joinList.size() && (null != where)) {
				return simpleLightSelectWhere(select, where);
			} else if (1 > joinList.size() && (null == where)) {
				return "TRU";
			} else if (true) {
				return "TRU";
			}
			return "";
		} else {
			return INVALID + ": " + sqlparser.getErrormessage();
		}
	}

	private static String simpleLightSelect(TSelectSqlStatement select) {
		String SPARQLSelect = "SELECT";
		String SPARQLWhere = "\nWHERE{";

		TResultColumnList listOfFields = select.getResultColumnList();
		TJoinList joinList = select.joins;// tables list of From clause
		for (int i = 0; i < listOfFields.size(); i++) {

			TResultColumn col = (TResultColumn) listOfFields.getElement(i);

			SPARQLSelect += " ?" + listOfFields.getElement(i).toString();
			SPARQLWhere += "\n\t?x " + joinList.getElement(0).toString() + ":"
					+ listOfFields.getElement(i).toString() + " ?"
					+ listOfFields.getElement(i).toString() + " .";
		}

		SPARQLWhere += "\n}";
		String SPARQLGroubBy = "";
		if (null != select.getGroupByClause()) {
			SPARQLGroubBy += "\nGROUP BY";
			TGroupByItemList listGrBy = select.getGroupByClause().getItems();
			for (int i = 0; i < listGrBy.size(); i++) {
				SPARQLGroubBy += " ?" + listGrBy.getElement(i).toString();
			}
		}
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
		String SPARQLLimit = "";
		if (null != select.getLimitClause()) {
			SPARQLLimit += "\n"
					+ ((TLimitClause) select.getLimitClause()).toString();
		}
		String SPARQLQuery = SPARQLSelect + SPARQLWhere + SPARQLGroubBy
				+ SPARQLOrderBy + SPARQLLimit;
		return SPARQLQuery;
	}

	private static String simpleLightSelectWhere(TSelectSqlStatement select,
			TWhereClause where) {

		System.out.println("where: " + where.toString());
		String SPARQLSelect = "SELECT";
		String SPARQLWhere = "\nWHERE{";

		TResultColumnList listOfFields = select.getResultColumnList();
		TJoinList joinList = select.joins;// tables list of From clause
		for (int i = 0; i < listOfFields.size(); i++) {

			TResultColumn col = (TResultColumn) listOfFields.getElement(i);

			SPARQLSelect += " ?" + listOfFields.getElement(i).toString();
			SPARQLWhere += "\n\t?x " + joinList.getElement(0).toString() + ":"
					+ listOfFields.getElement(i).toString() + " ?"
					+ listOfFields.getElement(i).toString() + " .";
		}

		String SPARQLFilter = "\n\tFILTER(";
		// System.out.println("where.getColumnNo(): " + where.getColumnNo());
		// System.out.println("where.getDummyTag(): " + where.getDummyTag());
		// System.out.println("where.getLineNo(): " + where.getLineNo());
		// System.out.println("where.getNodeType(): " + where.getNodeType());
		System.out.println("where.getCondition(): " + where.getCondition());

		TExpression condition = where.getCondition();
		String str = precessRecursiveWhere(condition);
		// System.out.println("FILTER ("+str+")\n");
		SPARQLFilter += str;
		// TExpression left = condition.getLeftOperand();
		// TExpression right = condition.getRightOperand();

		// System.out.println("condition.getLeftOperand(): " + left);
		// System.out.println("condition.getOperator(): "
		// + condition.toString().substring(
		// condition.toString().lastIndexOf(left.toString())
		// + left.toString().length(),
		// condition.toString().lastIndexOf(right.toString())));
		// System.out.println("condition.getRightOperand(): " + right);

		// System.out.println("condition.getOperatorToken(): "
		// + condition.getOperatorToken());

		// System.out.println("condition.getLeftOperand().getOperatorToken(): "
		// + condition.getLeftOperand().getOperatorToken());
		// System.out.println("where.getEndToken(): " + where.getEndToken());
		// System.out.println("where.getStartToken(): " +
		// where.getStartToken());

		SPARQLFilter += ")";

		SPARQLWhere += SPARQLFilter + "\n}";
		String SPARQLGroubBy = "";
		if (null != select.getGroupByClause()) {
			SPARQLGroubBy += "\nGROUP BY";
			TGroupByItemList listGrBy = select.getGroupByClause().getItems();
			for (int i = 0; i < listGrBy.size(); i++) {
				SPARQLGroubBy += " ?" + listGrBy.getElement(i).toString();
			}
		}
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
		String SPARQLLimit = "";
		if (null != select.getLimitClause()) {
			SPARQLLimit += "\n"
					+ ((TLimitClause) select.getLimitClause()).toString();
		}
		String SPARQLQuery = SPARQLSelect + SPARQLWhere + SPARQLGroubBy
				+ SPARQLOrderBy + SPARQLLimit;
		return SPARQLQuery;
	}

	public static String precessRecursiveWhere(TExpression condition) {
		String str = "";
		if (condition.getOperatorToken() == null) {
			TExpression left = condition.getLeftOperand();
			TExpression right = condition.getRightOperand();

			//System.out.println("condition.getLeftOperand(): " + left);
			// System.out.println("condition.getOperator(): " + operator);
			//System.out.println("condition.getRightOperand(): " + right);
			String operator = condition.toString().substring(
					condition.toString().lastIndexOf(left.toString())
							+ left.toString().length(),
					condition.toString().lastIndexOf(right.toString()));

			str += "?" + left + operator + right;

			return str;

		} else {
		//	System.out.println("condition: " + condition);
			String operator = condition.getOperatorToken().toString();
		//	System.out.println("condition.getOperatorToken(): " + operator);

			if (operator.equalsIgnoreCase("LIKE")) {

				String[] lr = condition.toString().split(operator);
				String left = lr[0];
				String right = lr[1];
				left=left.substring(0,left.length()-1);
				right=right.substring(1,right.length());
				// regex(?ename, "^S")
				str += " regex(?" + left + ",\"" + right + "\") ";
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
