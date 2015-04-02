package net.pms.medialibrary.commons;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ToolTipUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HyperLinkToolTip extends JToolTip {
	private static final Logger LOGGER = LoggerFactory.getLogger(HyperLinkToolTip.class);
	
	private static final long serialVersionUID = -8107203112982951774L;
	
	private JEditorPane editorPane;

	public HyperLinkToolTip() {
		setLayout(new BorderLayout());
		editorPane = new JEditorPane();
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		editorPane.setForeground(new ColorUIResource(255, 255, 255));
		editorPane.setBackground(new ColorUIResource(125, 184, 47));

		editorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if(Desktop.isDesktopSupported())
					{
						try {
							Desktop.getDesktop().browse(new URI(e.getDescription()));
							closeToolTip();
						} catch (IOException e1) {
							LOGGER.error("Failed to open hyperlink", e1);
						} catch (URISyntaxException e1) {
							LOGGER.error("Failed to open hyperlink", e1);
						}
					} else {
						LOGGER.warn("Desktop is not supported, the clicked link can't be opened");						
					}
				}
			}
		});
		add(editorPane);
	}

	@Override
	public void setTipText(String tipText) {
		editorPane.setText(tipText);
	}

	@Override
	public void updateUI() {
		setUI(new ToolTipUI() { });
	}
	
	private void closeToolTip() {
		setVisible(false);
	}
}