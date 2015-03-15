package jgnash.ui.actions;

import java.awt.event.ActionEvent;

import jgnash.ui.report.text.framework.BalanceSheetTXT;
import jgnash.ui.util.builder.Action;

@Action("report-balancesheettxt-command")
public class BalanceSheetTXTReportAction extends AbstractEnabledAction
{

  private static final long serialVersionUID = 0L;

  @Override
  public void actionPerformed(ActionEvent e)
  {
    new BalanceSheetTXT().run();

  }

}
