package jgnash.ui.report.text.framework;

import java.awt.Font;

public class ReportManager
{
  private static int INDENT_SIZE = 4;
  private static char INDENT_CHAR = ' ';
  
  private static int HORIZONTAL_LINE_MAX_WIDTH = 80;
  private static Font DEFAULT_FONT = new Font("Courier New", Font.PLAIN, 16);
  
  private int _horizontalLineMaxWidth = HORIZONTAL_LINE_MAX_WIDTH;
  private int _numberMaxWidth =  (int) (HORIZONTAL_LINE_MAX_WIDTH * 0.25);
  private int _textMaxWidth =  (int) (HORIZONTAL_LINE_MAX_WIDTH * 0.75);
  private Font _font = DEFAULT_FONT;
  private int _indentSize = INDENT_SIZE;
  private char _indentChar = INDENT_CHAR;
  
  ReportManager()
  {
  }
  
  public int getHorizontalLineMaxWidth()
  {
    return _horizontalLineMaxWidth;
  }
  
  public Font getFont()
  {
    return _font;
  }
  
  public int getNumberMaxWidth()
  {
    return _numberMaxWidth;
  }
  
  public int getTextMaxWidth()
  {
    return _textMaxWidth;
  }
  
  public String getIndentString()
  {
    StringBuilder indentString = new StringBuilder();
    
    for(int i = 0; i < _indentSize; i++)
    {
      indentString.append(_indentChar);
    }
    return indentString.toString();
  }
  
  
}
