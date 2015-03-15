package jgnash.ui.report.text.framework;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import jgnash.engine.EngineFactory;
import jgnash.text.CommodityFormat;

public class ReportEntry
{

  private static DecimalFormat _numberFormat = new DecimalFormat();
  
  private static ReportManager _reportManager = new ReportManager();


//  static
//  {
//    _numberFormat = CommodityFormat.getFullNumberFormat(EngineFactory.getEngine(EngineFactory.DEFAULT).getDefaultCurrency());
//
//  }
  
  public String get_entryName()
  {
    return _entryName;
  }

  public BigDecimal get_amount()
  {
    return _amount;
  }

  public int get_entryLevel()
  {
    return _entryLevel;
  }

  private String _entryName = "";

  private BigDecimal _amount = BigDecimal.ZERO;

  private int _entryLevel = 0;

  public ReportEntry(String entryName_, BigDecimal amount_, int entryLevel_)
  {
    _entryName = entryName_;
    _amount = amount_;
    _entryLevel = entryLevel_;
  }
  
  @Override
  public String toString()
  {
    String reportEntry = formatAcctNameOut(_entryName, _entryLevel) + formatDecimalOut(_amount);
    return reportEntry;
  }
  
  /**
   * format output decimal amount
   * 
   * @param amt the BigDecimal value to format
   * @return formated string
   */
  private static String formatDecimalOut(final BigDecimal amt)
  {

    int maxLen = _reportManager.getNumberMaxWidth(); // (-000,000,000.00)
    StringBuilder sb = new StringBuilder();

    String formattedAmt = _numberFormat.format(amt);

    // right align amount to pre-defined maximum length (maxLen)
    int amtLen = formattedAmt.length();
    if (amtLen < maxLen)
    {
      for (int ix = amtLen; ix < maxLen; ix++)
      {
        sb.append(' ');
      }
    }

    sb.append(formattedAmt);

    return sb.toString();
  }

  /**
   * format output account name
   * 
   * @param acctName the account name to format
   * @return the formatted account name
   */
  private static String formatAcctNameOut(final String acctName, final int accountLevel_)
  {

    int maxLen = _reportManager.getTextMaxWidth(); // max 30 characters
    StringBuilder sb = new StringBuilder(maxLen);

    for (int indent = 0; indent < accountLevel_; indent++)
    {
      sb.append(_reportManager.getIndentString());
    }

    sb.append(acctName);

    // set name to pre-defined maximum length (maxLen)
    int nameLen = sb.length();
    for (int ix = nameLen; ix < maxLen; ix++)
    {
      sb.append(' ');
    }
    sb.setLength(maxLen);

    return sb.toString();
  }
}
