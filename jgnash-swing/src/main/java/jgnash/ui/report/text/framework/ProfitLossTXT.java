package jgnash.ui.report.text.framework;

import java.util.ArrayList;
import java.util.List;



import org.apache.log4j.Logger;

import jgnash.engine.AccountGroup;
import jgnash.engine.AccountType;
import jgnash.util.Resource;

public class ProfitLossTXT extends TextReport
{
  private static final Logger logger = Logger.getLogger(ProfitLossTXT.class.getName());

  @Override
  protected void populateAccountGroups()
  {
    logger.info("Start populateAccountGroups");
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
    addReportTitle(reportLines, Resource.get().getString("Title.ProfitLoss"));

    addReportDates(reportLines);

    for (AccountGroup accountGroup : _accountGroups)
    {
      for (AccountType accountType : _reportEntriesMap.get(accountGroup).keySet())
      {
        populateReportLines(reportLines, accountType);
        populateTotal(reportLines, accountType);
      }
    }

    addDoubleDottedLine(reportLines);
    reportLines.add(new ReportEntry(Resource.get().getString("Word.NetIncome"), getNetTotal(), 0).toString());
    addDoubleDottedLine(reportLines);

    logger.info("End getReportLines");

    return reportLines;

  }
}
