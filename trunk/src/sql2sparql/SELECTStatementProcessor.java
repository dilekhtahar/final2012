package sql2sparql;

import java.util.ArrayList;
import java.util.HashMap;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TArrayAccess;
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
	public static ArrayList<String> exists = new ArrayList<String>();
	public static ArrayList<String> notexists = new ArrayList<String>();
	public static HashMap<String, String> alias = new HashMap<String, String>();

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
		exists = new ArrayList<String>();
		notexists = new ArrayList<String>();
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
		sqlparser.setSqltext(selectStatement);

		if (selectStatement.contains("SELECT *")) {
			return "SELECT ?x ?y ?x\nWHERE{\n\t?x ?y ?z .\n}";
		}

		int ret = sqlparser.parse();
		if (0 == ret) {
			TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements
					.get(0);
			TJoinList joinList = select.joins;// tables list of From clause
			// list of mappings

			if (1 == joinList.size()) {
				// return simpleLightSelect(select);
				// } else if (1 == joinList.size() && (null != where)) {
				String validation = validateQuery(selectStatement);
				if (validation.equals(VALID)) {
					return simpleLightSelectWhere(select);
				} else {
					return validation;
				}
			} else if (1 > joinList.size()) {
				return "1 > joinList.size()";
			} else if (true) {
				return simpleLightSelectWhereFrom(select);
			}
		} else {
			return INVALID + ": " + sqlparser.getErrormessage();
		}
		return "Nothing";
	}

	private static String simpleLightSelectWhereFrom(TSelectSqlStatement select) {

		// PREFIXES PART
		String SPARQLPrefixes = buildPrefixes(select);

		// SELECT WHERE
		String SPARQLSelect = "SELECT";
		String SPARQLWhere = "\nWHERE{";
		String table = "";
		TResultColumnList listOfFields = select.getResultColumnList();
		TJoinList joinList = select.joins;// tables list of SELECT clause

		for (int i = 0; i < joinList.size(); i++) {

			//System.out.println("joinList.getElement(0).toString(): "
			//		+ joinList.getElement(i).toString());
			String[] str = joinList.getElement(i).toString().split(" ");
			alias.put(str[1], str[0]);
		}

		for (int i = 0; i < listOfFields.size(); i++) {
			SPARQLSelect += " ?"
					+ listOfFields.getElement(i).toString()
							.replaceAll("\\.", "_");

			String ss = listOfFields.getElement(i).toString();

			String sss[] = ss.split("\\.");

			//System.out.println("alias: " + alias);
			//System.out.println("s[0]: " + sss[0]);
			//System.out.println("a[1]: " + sss[1]);

			SPARQLWhere += "\n\t?" + sss[0] + " " + alias.get(sss[0]) + ":"
					+ sss[1] + " ?" + ss.replaceAll("\\.", "_") + " .";
			table = joinList.getElement(0).toString();
			exists.add(listOfFields.getElement(i).toString());
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
		// System.out.println("exists: " + exists);
		//System.out.println("notexists: " + notexists);

		String add = "";
		for (String str : notexists) {

			// System.out.println("table: " +table);
			// System.out.println("tt[0]: " +alias.get(tt[0]));
			// System.out.println("tt[1]: " +alias.get(tt[1]));
			String[] ss = str.split("\\.");
			add += "\n\t?" + ss[0] + " " + alias.get(ss[0]) + ":" + ss[1]
					+ " ?" + str.replaceAll("\\.", "_") + " .";

		}
		SPARQLWhere += add + SPARQLFilter + "\n}";

		// GROUP BY
		String SPARQLGroubBy = buildGroupByFrom(select);

		// ORDER BY
		String SPARQLOrderBy = buildOrderByFrom(select);

		// LIMIT
		String SPARQLLimit = buildLimit(select);

		String SPARQLQuery = SPARQLPrefixes + SPARQLSelect + SPARQLWhere
				+ SPARQLGroubBy + SPARQLOrderBy + SPARQLLimit;
		return SPARQLQuery;
	}

	private static String simpleLightSelectWhere(TSelectSqlStatement select) {

		// PREFIXES PART
		String SPARQLPrefixes = buildPrefixes(select);

		// SELECT WHERE
		String SPARQLSelect = "SELECT";
		String SPARQLWhere = "\nWHERE{";
		String table = "";
		TResultColumnList listOfFields = select.getResultColumnList();
		TJoinList joinList = select.joins;// tables list of From clause
		for (int i = 0; i < listOfFields.size(); i++) {
			SPARQLSelect += " ?" + listOfFields.getElement(i).toString();
			SPARQLWhere += "\n\t?x " + joinList.getElement(0).toString() + ":"
					+ listOfFields.getElement(i).toString() + " ?"
					+ listOfFields.getElement(i).toString() + " .";
			table = joinList.getElement(0).toString();
			exists.add(listOfFields.getElement(i).toString());
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
		// System.out.println("exists: " + exists);
		// System.out.println("notexists: " + notexists);

		String add = "";
		for (String str : notexists) {
			add += "\n\t?x " + table + ":" + str + " ?" + str + " .";

		}
		SPARQLWhere += add + SPARQLFilter + "\n}";

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

	private static String buildOrderByFrom(TSelectSqlStatement select) {
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
									.getSortKey().toString().replaceAll("\\.", "_") + ")";
				} else {// default - ASC
					SPARQLOrderBy += " ?"
							+ ((TOrderByItem) listOrderBy.getElement(i))
									.getSortKey().toString().replaceAll("\\.", "_");
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
	private static String buildGroupByFrom(TSelectSqlStatement select) {
		String SPARQLGroubBy = "";
		if (null != select.getGroupByClause()) {
			SPARQLGroubBy += "\nGROUP BY";
			TGroupByItemList listGrBy = select.getGroupByClause().getItems();
			for (int i = 0; i < listGrBy.size(); i++) {
				SPARQLGroubBy += " ?" + listGrBy.getElement(i).toString().replaceAll("\\.","_");
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
						+ ": <"
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
		boolean parentheses = false;
		String str = "";
		// System.out.println("condition: " + condition);

		if (condition.toString().contains("SELECT")) {
			System.out.println();
		} else {

			// if(condition.getArrayAccess()!=null)
			// System.out.println("condition.getComparisonOperator(): "
			// + condition.getComparisonOperator());
			// remove(condition.toString());
			// System.out.println("condition.getOperatorToken(): "
			// + condition.getOperatorToken());

			if (has(condition.toString())
					&& condition.getOperatorToken() == null) {

				parentheses = true;

				// System.out.println("has(condition.toString())");
				String rem = remove(condition.toString());
				String newSelect = "SELECT A FROM B WHERE " + rem;

				// System.out.println(newSelect);

				TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
				sqlparser.setSqltext(newSelect);

				int ret = sqlparser.parse();
				if (0 == ret) {
					TSelectSqlStatement select = (TSelectSqlStatement) sqlparser.sqlstatements
							.get(0);
					TWhereClause where = select.getWhereClause();
					if (null != where) {

						TExpression newCondition = where.getCondition();
						String sss = precessRecursiveWhere(newCondition);
						// System.out.println("SSSS+ " + sss);

						str += "(" + sss + ")";
					}
				}
			}

			if (parentheses != true) {

				if (condition.getOperatorToken() == null) {
					TExpression left = condition.getLeftOperand();
					TExpression right = condition.getRightOperand();
					// System.out.println("-left: " + left);
					// System.out.println("-right: " + right);
					String operator = condition.getComparisonOperator()
							.toString();

					// condition.toString().substring(
					// condition.toString().lastIndexOf(left.toString())
					// + left.toString().length(),
					// condition.toString().lastIndexOf(right.toString()));

					String strRight = right.toString();
					if (strRight.contains("'")) {
						strRight = strRight.replaceAll("'", "\"");
					} else {
						if (isInt(strRight) == false) {
							if (!exists.contains(strRight)) {
								notexists.add(strRight);
							}
							strRight = "?" + strRight;
						}

					}

					String strLeft = left.toString().replaceAll("\\.", "_");

					strRight = strRight.replaceAll("\\.", "_");
					if (!exists.contains(left.toString())) {
						notexists.add(left.toString());
					}

					str += "?" + strLeft + operator + strRight;

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
						if (!exists.contains(left.toString())) {
							notexists.add(left.toString());
						}
						str += " regex(?" + left + ",\""
								+ right.replaceAll("'", "") + "\") ";
					} else {

						TExpression left = condition.getLeftOperand();
						TExpression right = condition.getRightOperand();

						// System.out.println("left: " + left);
						// System.out.println("right: " + right);

						String recLeft = precessRecursiveWhere(left);
						String recRight = precessRecursiveWhere(right);
						if (operator.equalsIgnoreCase("or"))
							operator = " || ";
						else if (operator.equalsIgnoreCase("and"))
							operator = " && ";
						str += recLeft + operator + recRight;

					}
				}

			}
		}
		return str;
	}

	public static boolean has(String cond) {
		if (cond.charAt(0) == '(')
			return true;
		else
			return false;
	}

	public static String remove(String cond) {
		// System.out.println("cond.substring(1, cond.length() - 1): "+cond.substring(1,
		// cond.length() - 1));
		return cond.substring(1, cond.length() - 1);
	}

	public static boolean isInt(String i) {
		try {
			Integer.parseInt(i);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
}
