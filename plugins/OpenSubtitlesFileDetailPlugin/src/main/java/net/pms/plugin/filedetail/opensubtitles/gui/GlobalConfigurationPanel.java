package net.pms.plugin.filedetail.opensubtitles.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import net.pms.plugin.filedetail.opensubtitles.OpenSubtitlesPlugin;
import net.pms.plugin.filedetail.opensubtitles.configuration.GlobalConfiguration;

public class GlobalConfigurationPanel extends JPanel {
	private static final long serialVersionUID = -2458232316940897901L;

	private JTextField tfSubtitlesPath;
	
	public GlobalConfigurationPanel() {
		setLayout(new GridLayout());
		
		init();
		build();
	}
	
	public GlobalConfigurationPanel(GlobalConfiguration globalConfig) {
		this();		
		setConfiguration(globalConfig);
	}

	private void init() {
		tfSubtitlesPath = new JTextField();
		tfSubtitlesPath.setToolTipText(OpenSubtitlesPlugin.messages.getString("GlobalConfigurationPanel.SelectSubtitlePathTextField.ToolTip"));
	}

	private void build() {
		// Set basic layout
		FormLayout layout = new FormLayout("5px, p, 5px, f:p:g, 5px, p, 5px", //columns
				"5px, p, f:5px:g"); //rows
		PanelBuilder builder = new PanelBuilder(layout);
		builder.opaque(true);

		CellConstraints cc = new CellConstraints();
		
		JLabel lSelectSubtitlePath = new JLabel(OpenSubtitlesPlugin.messages.getString("GlobalConfigurationPanel.SelectSubtitlePathLabel.Text"));
		JButton bSelectSubtitlePath = new JButton(OpenSubtitlesPlugin.messages.getString("GlobalConfigurationPanel.SelectSubtitlePathButton.Text"));
		bSelectSubtitlePath.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(tfSubtitlesPath.getText()));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if (fc.showOpenDialog(getTopLevelAncestor()) == JFileChooser.APPROVE_OPTION) {
					tfSubtitlesPath.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		builder.add(lSelectSubtitlePath, cc.xy(2, 2));
		builder.add(tfSubtitlesPath, cc.xy(4, 2));
		builder.add(bSelectSubtitlePath, cc.xy(6, 2));
		
		add(builder.getPanel());
	}

	public void setConfiguration(GlobalConfiguration globalConfiguration) {
		tfSubtitlesPath.setText(globalConfiguration.getSubtitleFolder());
	}

	public void updateConfiguration(GlobalConfiguration globalconfiguration) {
		globalconfiguration.setSubtitleFolder(tfSubtitlesPath.getText());
	}

}
