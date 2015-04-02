package net.pms.plugin.filedetail.opensubtitles.gui;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolTip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import net.pms.medialibrary.commons.HyperLinkToolTip;
import net.pms.newgui.components.ComboBoxItem;
import net.pms.plugin.filedetail.opensubtitles.OpenSubtitlesPlugin;
import net.pms.plugin.filedetail.opensubtitles.common.DisplayMode;
import net.pms.plugin.filedetail.opensubtitles.configuration.InstanceConfiguration;

public class InstanceConfigurationPanel extends JPanel {
	private static final long serialVersionUID = 6191468425911111164L;

	private static final Logger LOGGER = LoggerFactory.getLogger(InstanceConfigurationPanel.class);
	
	private JComboBox<ComboBoxItem<DisplayMode>> cbDisplayMode;
	private JTextField tfSubtitleLanguages;
	private JTextField tfMaxNumberOfSubtitles;
	
	public InstanceConfigurationPanel() {
		setLayout(new GridLayout());
		
		init();
		build();
	}

	public InstanceConfigurationPanel(InstanceConfiguration instanceConfiguration) {
		this();		
		setConfiguration(instanceConfiguration);
	}

	public void setConfiguration(InstanceConfiguration instanceConfiguration) {
		for(int i = 0; i < cbDisplayMode.getItemCount(); i++) {
			ComboBoxItem<DisplayMode> cbItem = cbDisplayMode.getItemAt(i);
			if(cbItem.getValue() == instanceConfiguration.getDisplayMode()) {
				cbDisplayMode.setSelectedItem(cbItem);
				break;
			}
		}
		
		tfSubtitleLanguages.setText(instanceConfiguration.getSubtitleLanguages());
		tfMaxNumberOfSubtitles.setText(String.valueOf(instanceConfiguration.getMaxNumberOfSubtitles()));
	}

	@SuppressWarnings("unchecked")
	public InstanceConfiguration getConfiguration() {
		InstanceConfiguration instanceConfiguration = new InstanceConfiguration();
		instanceConfiguration.setDisplayMode(((ComboBoxItem<DisplayMode>)cbDisplayMode.getSelectedItem()).getValue());
		instanceConfiguration.setSubtitleLanguages(tfSubtitleLanguages.getText());

		if(instanceConfiguration.getDisplayMode() == DisplayMode.Folder) {
			int maxNumberOfSubtitles;
			try{
				maxNumberOfSubtitles = Integer.parseInt(tfMaxNumberOfSubtitles.getText());
				instanceConfiguration.setMaxNumberOfSubtitles(maxNumberOfSubtitles);
			} catch(NumberFormatException ex) {
				LOGGER.error(String.format("Invalid value specified for maxNumberOfSubtitles (using default value). Value='%s'", tfMaxNumberOfSubtitles.getText()));
			}
		}
		
		return instanceConfiguration;
	}

	private void init() {
		cbDisplayMode = new JComboBox<>();
		cbDisplayMode.setToolTipText(OpenSubtitlesPlugin.messages.getString("Help.DisplayMode"));
		for(DisplayMode displayMode : DisplayMode.values()) {
			cbDisplayMode.addItem(new ComboBoxItem<DisplayMode>(OpenSubtitlesPlugin.messages.getString("DisplayMode." + displayMode), displayMode));
		}
		cbDisplayMode.addItemListener(new ItemListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void itemStateChanged(ItemEvent e) {
				switch(((ComboBoxItem<DisplayMode>)cbDisplayMode.getSelectedItem()).getValue()) {
				case File:
					tfMaxNumberOfSubtitles.setText("");
					tfMaxNumberOfSubtitles.setEnabled(false);
					break;
				case Folder:
					tfMaxNumberOfSubtitles.setEnabled(true);
					break;
				}
			}
		});
		
		tfSubtitleLanguages = new JTextField() {
			private static final long serialVersionUID = -2927951764552780686L;

			public JToolTip createToolTip() {
				JToolTip tip = new HyperLinkToolTip();
				tip.setComponent(this);
				return tip;
			}

			// Set tooltip location
			public Point getToolTipLocation(MouseEvent event) {
				return new Point(getWidth() / 2, getHeight() / 2);
			}
		};
		tfSubtitleLanguages.setToolTipText(OpenSubtitlesPlugin.messages.getString("Help.Languages"));
		
		tfMaxNumberOfSubtitles = new JTextField();
		tfMaxNumberOfSubtitles.setToolTipText(OpenSubtitlesPlugin.messages.getString("Help.MaxNumberOfSubtitles"));
	}

	private void build() {
		// Set basic layout
		FormLayout layout = new FormLayout("5px, p, 5px, f:100:g, 5px", //columns
				"5px, p, 5px, p, 5px, p, f:5px:g"); //rows
		PanelBuilder builder = new PanelBuilder(layout);
		builder.opaque(true);

		CellConstraints cc = new CellConstraints();
		
		builder.addLabel(OpenSubtitlesPlugin.messages.getString("Header.DisplayMode"), cc.xy(2, 2));
		builder.add(cbDisplayMode, cc.xy(4, 2));

		builder.addLabel(OpenSubtitlesPlugin.messages.getString("Header.Languages"), cc.xy(2, 4));
		builder.add(tfSubtitleLanguages, cc.xy(4, 4));

		builder.addLabel(OpenSubtitlesPlugin.messages.getString("Header.MaxNumberOfSubtitles"), cc.xy(2, 6));
		builder.add(tfMaxNumberOfSubtitles, cc.xy(4, 6));

		removeAll();
		add(builder.getPanel());
	}
}
