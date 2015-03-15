package jgnash.ui.report.text.framework;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import jgnash.util.Resource;

public class ReportHelper
{

  public static String getFileName()
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setMultiSelectionEnabled(false);
    chooser.addChoosableFileFilter(new FileNameExtensionFilter(Resource.get().getString("Message.TXTFile"), "txt"));

    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
    {
      String fileName = chooser.getSelectedFile().getAbsolutePath();
      if (!fileName.endsWith(".txt"))
      {
        fileName = fileName + ".txt";
      }
      return fileName;
    }
    return null;
  }
}
