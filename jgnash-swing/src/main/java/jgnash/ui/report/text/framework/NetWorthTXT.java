package jgnash.ui.report.text.framework;

import java.util.ArrayList;
import java.util.List;



import org.apache.log4j.Logger;

import jgnash.engine.AccountGroup;
import jgnash.engine.AccountType;
import jgnash.util.Resource;

public class NetWorthTXT extends TextReport
{
  private static final Logger logger = Logger.getLogger(NetWorthTXT.class.getName());

  @Override
  protected void populateAccountGroups()
  {
    logger.info("Start populateAccountGroups");
    _accountGroups.add(AccountGroup.ASSET);
    _accountGroups.add(AccountGroup.INVEST);
    _accountGroups.add(AccountGroup.LIABILITY);
    logger.info("End populateAccountGroups");
   
  }

  @Override
  protected List<String> getReportLines()
  {
    logger.info("Start getReportLines");

    List<String> reportLines = new ArrayList<String>();

    // title and dates
    addReportTitle(reportLines, Resource.get().getString("Word.NetWorth"));

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
    reportLines.add(new ReportEntry(Resource.get().getString("Word.NetWorth"), getNetTotal(), 0).toString());
    addDoubleDottedLine(reportLines);

    logger.info("End getReportLines");

    return reportLines;

  }
}
