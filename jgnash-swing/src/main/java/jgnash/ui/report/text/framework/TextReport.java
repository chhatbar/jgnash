package jgnash.ui.report.text.framework;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jgnash.engine.Account;
import jgnash.engine.AccountGroup;
import jgnash.engine.AccountType;
import jgnash.engine.CurrencyNode;
import jgnash.engine.Engine;
import jgnash.engine.EngineFactory;
import jgnash.ui.components.JDateField;
import jgnash.ui.register.AccountBalanceDisplayManager;
import jgnash.util.DateUtils;
import jgnash.util.Resource;

import org.apache.log4j.Logger;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public abstract class TextReport
{
  private static final Logger logger = Logger.getLogger(TextReport.class.getName());

  private static final boolean SHOW_EMPTY_ACCOUNT = true;

  private Date _startDate;

  private Date _endDate;

  private CurrencyNode baseCommodity;

  private Account _rootAccount;

  private ReportManager _reportManager = new ReportManager();

  protected List<AccountGroup> _accountGroups = new ArrayList<>();

  protected Map<AccountGroup, Map<AccountType, List<ReportEntry>>> _reportEntriesMap = new HashMap<AccountGroup, Map<AccountType, List<ReportEntry>>>();

  protected abstract List<String> getReportLines();

  protected abstract void populateAccountGroups();

  private void initializeMembers()
  {
    logger.info("Start initializeMembers");

    initializeReportEntriesPlaceholderMap();

    Engine engine = EngineFactory.getEngine(EngineFactory.DEFAULT);
    _rootAccount = engine.getRootAccount();
    baseCommodity = engine.getDefaultCurrency();

    logger.info("End initializeMembers");
  }

  private void initializeReportEntriesPlaceholderMap()
  {
    logger.info("Start initializeReportEntriesPlaceholderMap");

    EnumSet<AccountGroup> accountGroupEnumSet = EnumSet.allOf(AccountGroup.class);

    for (AccountGroup accountGroup : accountGroupEnumSet)
    {
      Map<AccountType, List<ReportEntry>> _accountTypeReportEntriesMap = new HashMap<AccountType, List<ReportEntry>>();

      Set<AccountType> accountTypes = AccountType.getAccountTypes(accountGroup);

      for (AccountType accountType : accountTypes)
      {
        _accountTypeReportEntriesMap.put(accountType, new ArrayList<ReportEntry>());
      }

      _reportEntriesMap.put(accountGroup, _accountTypeReportEntriesMap);
    }
    logger.info("End initializeReportEntriesPlaceholderMap");
  }

  private void populateReportEntriesForAccountGroup(AccountGroup accountGroup_)
  {
    logger.info("Start populateReportEntriesForAccountGroup");

    Map<AccountType, List<ReportEntry>> accountTypeReportEntriesMap = _reportEntriesMap.get(accountGroup_);

    for (AccountType accountType : accountTypeReportEntriesMap.keySet())
    {
      PopulateReportEntriesForAccountType(accountGroup_, accountType);
    }
    logger.info("End populateReportEntriesForAccountGroup");
  }

  private void PopulateReportEntriesForAccountType(AccountGroup accountGroup, AccountType accountType)
  {
    logger.info("Start populateReportEntriesForAccountType");

    List<ReportEntry> reportEntries = _reportEntriesMap.get(accountGroup).get(accountType);

    reportEntries.clear();
    reportEntries.addAll(this.getReportEntriesForAccountType(_rootAccount, _startDate, _endDate, accountType, 0));

    logger.info("End populateReportEntriesForAccountType");
  }

  protected void populateReportLines(List<String> reportLines, AccountType accountType)
  {
    logger.info("Start populateReportLines");

    reportLines.add(accountType.toString(true));
    addSingleDottedLine(reportLines);
    List<ReportEntry> reportEntries = _reportEntriesMap.get(accountType.getAccountGroup()).get(accountType);

    for (ReportEntry reportEntry : reportEntries)
    {
      reportLines.add(reportEntry.toString());
    }

    addSingleDottedLine(reportLines);

    logger.info("End populateReportLines");

  }

  protected void populateTotal(List<String> reportLines, AccountType accountType)
  {
    logger.info("Start populateTotal");

    BigDecimal total = getAccountTypeTotal(accountType);
    reportLines.add(new ReportEntry("Gross " + accountType.toString(true), total, 0).toString());
    addSingleDottedLine(reportLines);
    addBlankLine(reportLines);
    addBlankLine(reportLines);

    logger.info("End populateTotal");
  }

  private String getReportText(List<String> reportLines)
  {
    logger.info("Start getReportText");

    String reportText = "";

    if (_startDate == null || _endDate == null)
    {
      return reportText;
    }

    for (String reportLine : reportLines)
    {
      reportText += reportLine + "\n";
    }

    logger.info("End getReportText");

    return reportText;
  }

  public void run()
  {
    logger.info("Start run");

    try
    {
      initializeMembers();

      if (getDatesFromUser())
      {
        populateAccountGroups();
        for (AccountGroup accountGroup : _accountGroups)
        {
          populateReportEntriesForAccountGroup(accountGroup);
        }

        List<String> reportLines = getReportLines();
        String reportText = getReportText(reportLines);

        showReport(reportText);
      }
    }
    catch (Exception ex)
    {
      logger.error("Unable to run Report", ex);
    }
    logger.info("End run");

  }

  public static void showReport(final String reportText)
  {
    EventQueue.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          logger.info("Start showReport run");

          StringSelection stringSelection = new StringSelection(reportText);
          Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
          clpbrd.setContents(stringSelection, null);

          String vbs = ""
            + "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n"
            + "WshShell.Run \"notepad\", 9\n"
            + "WScript.Sleep 1000\n"
            + "WshShell.SendKeys \"^V\"";

            File file = new File(System.getProperty("java.io.tmpdir"), "notepad.vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            p.waitFor();
            cleanupClipboard();

          logger.info("End showReport run");

        }
        catch (Exception e)
        {
          logger.error("Unable to show report", e);
        }
      }
    });

  }

  private boolean getDatesFromUser()
  {
    logger.info("Start getDatesFromUser");

    Date start = new Date();
    start = DateUtils.subtractYear(start);

    JDateField startField = new JDateField();
    JDateField endField = new JDateField();

    startField.setValue(start);

    FormLayout layout = new FormLayout("right:p, 4dlu, p:g", "");
    DefaultFormBuilder builder = new DefaultFormBuilder(layout);
    builder.rowGroupingEnabled(true);

    builder.append(Resource.get().getString("Label.StartDate"), startField);
    builder.append(Resource.get().getString("Label.EndDate"), endField);
    builder.nextLine();
    builder.appendUnrelatedComponentsGapRow();
    builder.nextLine();

    JPanel panel = builder.getPanel();

    int option = JOptionPane.showConfirmDialog(null, new Object[] {panel}, Resource.get().getString("Message.StartEndDate"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (option == JOptionPane.OK_OPTION)
    {
      _startDate = DateUtils.trimDate(startField.dateValue());
      _endDate = DateUtils.trimDate(endField.dateValue());

    }

    logger.info("End getDatesFromUser");

    return _startDate != null && _endDate != null;
  }

  protected void addDoubleDottedLine(List<String> reportLines)
  {
    addHorizontalLine(reportLines, '=');
  }

  protected void addSingleDottedLine(List<String> reportLines)
  {
    addHorizontalLine(reportLines, '-');
  }

  private void addHorizontalLine(List<String> reportLines, char repeatChar)
  {
    logger.info("Start addHorizontalLine");

    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < _reportManager.getHorizontalLineMaxWidth(); i++)
    {
      sb.append(repeatChar);

    }
    reportLines.add(sb.toString());
    logger.info("Start addHorizontalLine");
  }

  private List<ReportEntry> getReportEntriesForAccountType(final Account a, final Date startDate, final Date endDate, final AccountType type, int level)
  {
    logger.info("Start getReportEntriesForAccountType");

    List<ReportEntry> reportEntries = new ArrayList<ReportEntry>();

    for (Account child : a.getChildren())
    {

      int len = child.getTransactionCount();
      if ((SHOW_EMPTY_ACCOUNT || len > 0) && type == child.getAccountType())
      {
        String acctName = child.getName();

        BigDecimal acctBal = AccountBalanceDisplayManager.convertToSelectedBalanceMode(child.getAccountType(), child.getBalance(startDate, endDate, baseCommodity));

        // output account name and balance
        reportEntries.add(new ReportEntry(acctName, acctBal, level));
      }
      if (child.isParent())
      {
        reportEntries.addAll(getReportEntriesForAccountType(child, startDate, endDate, type, level + 1));
      }
    }
    logger.info("End getReportEntriesForAccountType");

    return reportEntries;
  }

  protected BigDecimal getAccountGroupTotal(AccountGroup accountGroup_)
  {
    logger.info("Start getAccountGroupTotal");

    BigDecimal total = BigDecimal.ZERO;
    Map<AccountType, List<ReportEntry>> accountTypeReportEntriesMap = _reportEntriesMap.get(accountGroup_);

    for (AccountType accountType : accountTypeReportEntriesMap.keySet())
    {
      List<ReportEntry> reportEntries = _reportEntriesMap.get(accountGroup_).get(accountType);

      for (ReportEntry reportEntry : reportEntries)
      {
        total = total.add(reportEntry.get_amount());
      }
    }
    logger.info("End getAccountGroupTotal");
    return total;
  }

  protected BigDecimal getNetTotal()
  {
    logger.info("Start getNetTotal");

    BigDecimal total = BigDecimal.ZERO;

    for (AccountGroup accountGroup_ : _accountGroups)
    {
      Map<AccountType, List<ReportEntry>> accountTypeReportEntriesMap = _reportEntriesMap.get(accountGroup_);

      for (AccountType accountType : accountTypeReportEntriesMap.keySet())
      {
        List<ReportEntry> reportEntries = _reportEntriesMap.get(accountGroup_).get(accountType);

        for (ReportEntry reportEntry : reportEntries)
        {
          total = total.add(reportEntry.get_amount());
        }
      }
    }
    logger.info("End getNetTotal");

    return total;
  }

  protected BigDecimal getAccountTypeTotal(AccountType accountType_)
  {
    logger.info("Start getAccountTypeTotal");

    BigDecimal total = BigDecimal.ZERO;

    List<ReportEntry> reportEntries = _reportEntriesMap.get(accountType_.getAccountGroup()).get(accountType_);

    for (ReportEntry reportEntry : reportEntries)
    {
      total = total.add(reportEntry.get_amount());
    }
    logger.info("End getAccountTypeTotal");
    return total;
  }

  protected void addReportTitle(List<String> reportLines, String title_)
  {
    logger.info("Start addReportTitle");

    reportLines.add(title_);
    addBlankLine(reportLines);

    logger.info("End addReportTitle");

  }

  protected void addReportDates(List<String> reportLines)
  {
    logger.info("Start addReportDates");

    SimpleDateFormat df = new SimpleDateFormat("dd-MMMMM-yyyy");
    reportLines.add("From " + df.format(_startDate) + " To " + df.format(_endDate));
    addBlankLine(reportLines);
    addBlankLine(reportLines);

    logger.info("End addReportDates");

  }

  private void addBlankLine(List<String> reportLines)
  {
    logger.info("Start addBlankLine");

    reportLines.add("");

    logger.info("End addBlankLine");
  }

  private static  void cleanupClipboard()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          Thread.sleep(2000);
          StringSelection stringSelection = new StringSelection("Reset Clipboard");
          Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
          clpbrd.setContents(stringSelection, null);
        }
        catch (Exception e)
        {
          logger.error("Unable to clear clipboard", e);
        }
      }
    });
  }

}
