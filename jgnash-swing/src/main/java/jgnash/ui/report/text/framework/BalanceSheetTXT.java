package jgnash.ui.report.text.framework;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;



import org.apache.log4j.Logger;

import jgnash.engine.AccountGroup;
import jgnash.engine.AccountType;
import jgnash.util.Resource;

public class BalanceSheetTXT extends TextReport
{
  private static final Logger logger = Logger.getLogger(BalanceSheetTXT.class.getName());

  @Override
  protected void populateAccountGroups()
  {
    logger.info("Start populateAccountGroups");
    _accountGroups.add(AccountGroup.ASSET);
    _accountGroups.add(AccountGroup.LIABILITY);
    _accountGroups.add(AccountGroup.EQUITY);

    _accountGroups.add(AccountGroup.INCOME);
    _accountGroups.add(AccountGroup.EXPENSE);
    logger.info("End populateAccountGroups");
  }

  @Override
  protected List<String> getReportLines()
  {
    logger.info("Start getReportLines");

    List<String> reportLines = new ArrayList<String>();

    // title and dates
    addReportTitle(reportLines, Resource.get().getString("Title.BalanceSheet"));

    addReportDates(reportLines);

    EnumSet<AccountGroup> accountGroupsForReporting = EnumSet.of(AccountGroup.ASSET, AccountGroup.LIABILITY, AccountGroup.EQUITY );
    
    for (AccountGroup accountGroup : accountGroupsForReporting)
    {
      for (AccountType accountType : _reportEntriesMap.get(accountGroup).keySet())
      {
        populateReportLines(reportLines, accountType);
        populateTotal(reportLines, accountType);
      }
    }
    
    BigDecimal incomeTotal = getAccountTypeTotal(AccountType.INCOME);
    BigDecimal expenseTotal = getAccountTypeTotal(AccountType.EXPENSE);
    BigDecimal retainedEarnings = incomeTotal.add(expenseTotal);
    reportLines.add(new ReportEntry("Retained Earnings", retainedEarnings, 0).toString());
    addSingleDottedLine(reportLines);

    BigDecimal equityTotal = getAccountGroupTotal(AccountGroup.EQUITY);
    BigDecimal totalEquity = equityTotal.add(retainedEarnings);
    addSingleDottedLine(reportLines);
    reportLines.add(new ReportEntry("Total Equity", totalEquity, 0).toString());
    addSingleDottedLine(reportLines);
   
    BigDecimal totalLiabilities = getAccountGroupTotal(AccountGroup.LIABILITY);
    BigDecimal totalLiabilitiesPlusEquity = totalLiabilities.add(totalEquity);
    
    addDoubleDottedLine(reportLines);
    reportLines.add(new ReportEntry("Total Liabilities & Equity", totalLiabilitiesPlusEquity, 0).toString());
    addDoubleDottedLine(reportLines);

    
    logger.info("End getReportLines");
    
    return reportLines;

  }
}
