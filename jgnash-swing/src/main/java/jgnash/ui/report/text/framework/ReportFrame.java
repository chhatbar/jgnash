package jgnash.ui.report.text.framework;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import jgnash.ui.components.RollOverButton;
import jgnash.util.Resource;

public class ReportFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(ReportFrame.class
			.getName());

	private ReportManager _reportManager = new ReportManager();

	private JPanel contentPane;

	private JTextArea textArea;

	/**
	 * Create the frame.
	 */
	public ReportFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		JButton btnPrint = new RollOverButton(Resource.get().getString(
				"Button.Print"),
				Resource.getIcon("/jgnash/resource/document-print.png"));
		btnPrint.setToolTipText(Resource.get().getString("ToolTip.PrintRegRep"));
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e != null) {
					try {
						textArea.print();
					} catch (PrinterException e1) {
						logger.error("Unable to Print TextArea");
					}
				}
			}
		});
		panel.setLayout(new BorderLayout(0, 0));
		btnPrint.setVerticalAlignment(SwingConstants.TOP);
		btnPrint.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(btnPrint, BorderLayout.WEST);

		textArea = new JTextArea();
		textArea.setFont(_reportManager.getFont());
		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		ApplyApplicationModeSettings();
	}

	protected void ApplyApplicationModeSettings() {
	}

	public void setText(String reportText_) {
		textArea.setText(reportText_);
	}
}
