/*
 * PS3 Media Server, for streaming any medias to your PS3.
 * Copyright (C) 2012  Ph.Waeber
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.pms.medialibrary.gui.tab;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import net.pms.Messages;
import net.pms.medialibrary.commons.dataobjects.DOFileImportTemplate;
import net.pms.medialibrary.commons.dataobjects.DOManagedFile;
import net.pms.medialibrary.commons.events.FileImportDialogListener;
import net.pms.medialibrary.commons.helpers.GUIHelper;
import net.pms.medialibrary.commons.interfaces.IMediaLibraryStorage;
import net.pms.medialibrary.gui.dialogs.FileImportTemplateDialog;
import net.pms.medialibrary.gui.shared.EButton;
import net.pms.medialibrary.storage.MediaLibraryStorage;
import net.pms.notifications.NotificationCenter;
import net.pms.notifications.NotificationSubscriber;
import net.pms.notifications.types.DBEvent;
import net.pms.notifications.types.DBEvent.Type;

public class ManagedFoldersPanel extends JPanel {
	private static final long      serialVersionUID = 1558319355911044800L;
//	private static final Logger    log              = Logger.getLogger(ManagedFoldersPanel.class);

	private final int              MAX_FOLDERS      = 40;
	private JButton                bAddFolder;
	private JButton                bSaveAndApply;
	private List<ManagedFolderObj> managedFolders   = new ArrayList<ManagedFolderObj>();
	private IMediaLibraryStorage   storage;
	
	private static final ImageIcon GREEN_BULLET_ICON = new ImageIcon(ManagedFoldersPanel.class.getResource("/resources/images/bullet-green-16.png"));
	private static final ImageIcon RED_BULLET_ICON = new ImageIcon(ManagedFoldersPanel.class.getResource("/resources/images/bullet-red-16.png"));

	public ManagedFoldersPanel() {
		setLayout(new GridLayout());
		init();

		storage = MediaLibraryStorage.getInstance();
		addManagedFolders(storage.getManagedFolders());
		applyLayout();
		
		// Subscribe to DB reset event
		NotificationCenter.getInstance(DBEvent.class).subscribe(new NotificationSubscriber<DBEvent>() {
			
			@Override
			public void onMessage(DBEvent obj) {
				if(obj.getType() == Type.Reset) {
					// Remove all managed folders when the DB has been reset
					managedFolders.clear();
					applyLayout();
				}
			}
		});
	}

	private void addManagedFolders(List<DOManagedFile> mFolder) {
		for (DOManagedFile f : mFolder) {
			addManagedFolder(f);
		}
	}

	private void addManagedFolder(DOManagedFile f) {
		if (getManagedFolders().size() < MAX_FOLDERS) {
			JCheckBox cbWatch = new JCheckBox();
			cbWatch.setSelected(f.isWatchEnabled());
			JTextField tfFolderPath = new JTextField(f.getPath());
			JButton bBrowse = new JButton(Messages.getString("ML.ManagedFoldersPanel.bBrowse"));
			JCheckBox cbVideo = new JCheckBox();
			cbVideo.setSelected(f.isVideoEnabled());
			JCheckBox cbAudio = new JCheckBox();
			cbAudio.setSelected(f.isAudioEnabled());
			JCheckBox cbPictures = new JCheckBox();
			cbPictures.setSelected(f.isPicturesEnabled());
			JButton bScan = new JButton(Messages.getString("ML.ManagedFoldersPanel.bScan"));
			JButton bDelete = new JButton(new ImageIcon(getClass().getResource("/resources/images/tp_remove.png")));			
			JCheckBox cbSubFolders = new JCheckBox();
			cbSubFolders.setSelected(f.isSubFoldersEnabled());
			JCheckBox cbEnablePlugins = new JCheckBox();
			cbEnablePlugins.setSelected(f.isPluginImportEnabled());
			
			EButton bConfigureFileImportTemplate = new EButton(Messages.getString("ML.ScanFolderDialog.bConfigure"), f.getFileImportTemplate());
			bConfigureFileImportTemplate.setEnabled(f.isPluginImportEnabled());
			bConfigureFileImportTemplate.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int templateId = 1;
					
					final EButton b = (EButton)e.getSource();
					Object uo = b.getUserObject();
					if(uo instanceof DOFileImportTemplate) {
						templateId = ((DOFileImportTemplate)uo).getId();
					}
					
					//show the dialog
					FileImportTemplateDialog fid = new FileImportTemplateDialog(SwingUtilities.getWindowAncestor(b), templateId);
					fid.setLocation(GUIHelper.getCenterDialogOnParentLocation(fid.getPreferredSize(), b));
					fid.setResizable(false);
					fid.setModal(true);
					
					fid.addFileImportDialogListener(new FileImportDialogListener() {
						
						@Override
						public void templateSaved(DOFileImportTemplate fileImportTemplate) {
							b.setUserObject(fileImportTemplate);
						}
					});
					
					fid.pack();
					fid.setVisible(true);
					
					if(fid.isSave()) {
						templateId = fid.getTemplateId();
						updateSaveAndApplyButtonState(true);
					}
					
					b.setUserObject(storage.getFileImportTemplate(templateId));
						
					//Store the media library folders in the db when a template changes
					//to be able to check if a template is being used when deleting one
					//cleanManagedFolders();
					//storage.setManagedFolders(getManagedFolders());
				}
			});

			ManagedFolderObj obj = new ManagedFolderObj(cbWatch, tfFolderPath, cbVideo, bConfigureFileImportTemplate, cbAudio, cbPictures, bBrowse, bScan, bDelete, cbSubFolders, cbEnablePlugins, managedFolders.size());
			managedFolders.add(obj);

			obj.addRemoveListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					managedFolders.remove(((ManagedFolderObj) e.getSource()).getIndex());
					applyLayout();
					updateSaveAndApplyButtonState(true);
				}
			});
		} else {
			JOptionPane.showMessageDialog(this, String.format(Messages.getString("ML.ManagedFoldersPanel.FolderLimitMsg"), MAX_FOLDERS));
		}
	}

	public List<DOManagedFile> getManagedFolders() {
		List<DOManagedFile> mf = new ArrayList<DOManagedFile>();
		for (ManagedFolderObj obj : managedFolders) {
			DOManagedFile f = new DOManagedFile(obj.getCbWatch().isSelected(), obj.getTextFieldFolderPath().getText(), obj.getCheckBoxVideo().isSelected(), obj.getCheckBoxAudio()
			        .isSelected(), obj.getCheckBoxPictures().isSelected(), obj.getCheckBoxSubFolders().isSelected(), obj.getCheckBoxEnablePlugins().isSelected(), obj.getFileImportTemplate());
			if(!mf.contains(f)) {
				mf.add(f);
			}
		}

		return mf;
	}

	public void cleanManagedFolders() {
		List<DOManagedFile> mf =  getManagedFolders();

		if (mf.size() != managedFolders.size()) {
			managedFolders.clear();
			addManagedFolders(mf);
			applyLayout();
		}
	}

	private void init() {
		bAddFolder = new JButton(Messages.getString("ML.ManagedFoldersPanel.bAddFolder"));
		bAddFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DOManagedFile mf = new DOManagedFile();
				mf.setFileImportTemplate(storage.getFileImportTemplate(1));
				mf.setSubFoldersEnabled(true);
				mf.setVideoEnabled(true);
				
				addManagedFolder(mf);
				applyLayout();
				
				updateSaveAndApplyButtonState(true);
			}
		});
		
		bSaveAndApply = new JButton(Messages.getString("ML.ManagedFoldersPanel.bSaveAndApply"));
		bSaveAndApply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				storage.setManagedFolders(getManagedFolders());
				updateSaveAndApplyButtonState(false);
			}
		});
		updateSaveAndApplyButtonState(false);
	}

	private void applyLayout() {
		FormLayout layout = new FormLayout("fill:p:grow", // columns
		        "fill:10:grow, 5, p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.opaque(true);

		CellConstraints cc = new CellConstraints();

		builder.add(bAddFolder, cc.xy(1, 3, CellConstraints.CENTER, CellConstraints.BOTTOM));
		builder.add(bSaveAndApply, cc.xy(1, 3, CellConstraints.RIGHT, CellConstraints.BOTTOM));

		FormLayout layout2 = new FormLayout(
		        "center:p, 5px, center:p, 2px, 20:grow, 2px, p, 2px, p, 10px, center:p, 2px, center:p, 2px, center:p, 10px, p, 2px, p, 10px, p", // columns
		        "p, p, p, p, p, p, p, p, p, p," + // rows (40)
		        "p, p, p, p, p, p, p, p, p, p," + 
		        "p, p, p, p, p, p, p, p, p, p," + 
		        "p, p, p, p, p, p, p, p, p, p");
		PanelBuilder builder2 = new PanelBuilder(layout2);

		// show folders if there are any
		JPanel pManagedFolders;
		if (managedFolders.size() > 0) {
			// TODO: uncomment audio and picture items once implemented
			//create labels with tooltips
			JLabel lWatch = new JLabel(new ImageIcon(getClass().getResource("/resources/images/watch_folder-16.png")));
			lWatch.setToolTipText(Messages.getString("ML.ManagedFoldersPanel.lWatch"));
			JLabel lSubFolders = new JLabel(new ImageIcon(getClass().getResource("/resources/images/subfolders-16.png")));
			lSubFolders.setToolTipText(Messages.getString("ML.ManagedFoldersPanel.lSubfolders"));
			JLabel lVideo = new JLabel(new ImageIcon(getClass().getResource("/resources/images/videofolder-16.png")));
			lVideo.setToolTipText(Messages.getString("ML.ManagedFoldersPanel.lVideo"));
			JLabel lAudio = new JLabel(new ImageIcon(getClass().getResource("/resources/images/audiofolder-16.png")));
			lAudio.setToolTipText(Messages.getString("ML.ManagedFoldersPanel.lAudio"));
			JLabel lPictures = new JLabel(new ImageIcon(getClass().getResource("/resources/images/picturesfolder-16.png")));
			lPictures.setToolTipText(Messages.getString("ML.ManagedFoldersPanel.lPictures"));

			// set headers
			builder2.add(lWatch, cc.xy(1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
			builder2.add(lSubFolders, cc.xy(3, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
			builder2.addLabel(Messages.getString("ML.ManagedFoldersPanel.lFolderPath"), cc.xy(5, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
			builder2.add(lVideo, cc.xy(11, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
			// builder2.add(lAudio, cc.xy(13, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
			// builder2.add(lPictures, cc.xy(15, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
			builder2.addLabel(Messages.getString("ML.ManagedFoldersPanel.lPlugins"), cc.xyw(17, 1, 3, CellConstraints.CENTER, CellConstraints.DEFAULT));

			int rowIndex = 2;
			for (ManagedFolderObj f : managedFolders) {
				f.addPropertyChangedListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						updateSaveAndApplyButtonState(true);
					}					
				});
				f.setIndex(rowIndex - 2);
				builder2.add(f.getCbWatch(), cc.xy(1, rowIndex));
				builder2.add(f.getCheckBoxSubFolders(), cc.xy(3, rowIndex));
				builder2.add(f.getTextFieldFolderPath(), cc.xy(5, rowIndex));
				builder2.add(f.getButtonBrowse(), cc.xy(7, rowIndex));
				builder2.add(f.getButtonDelete(), cc.xy(9, rowIndex));
				builder2.add(f.getCheckBoxVideo(), cc.xy(11, rowIndex));
				// builder2.add(f.getCbAudio(), cc.xy(13, rowIndex));
				// builder2.add(f.getCbPictures(), cc.xy(15, rowIndex));
				builder2.add(f.getCheckBoxEnablePlugins(), cc.xy(17, rowIndex));
				builder2.add(f.getButtonConfigureFileImportTemplate(), cc.xy(19, rowIndex));
				builder2.add(f.getbButtonScan(), cc.xy(21, rowIndex));
				rowIndex++;
			}
			pManagedFolders = builder2.getPanel();
		} else {
			pManagedFolders = new JPanel(new GridLayout());
			pManagedFolders.add(new JLabel(Messages.getString("ML.ManagedFoldersPanel.pNoFoldersConfigured"), JLabel.CENTER));
		}

		JScrollPane sp = new JScrollPane(pManagedFolders);
		sp.setBorder(BorderFactory.createEmptyBorder());
		builder.add(sp, cc.xy(1, 1));

		removeAll();
		add(builder.getPanel());
		validate();
	}
	
	private void updateSaveAndApplyButtonState(boolean hasChange) {
		if(hasChange) {
			bSaveAndApply.setIcon(RED_BULLET_ICON);
			bSaveAndApply.setToolTipText(Messages.getString("ML.ManagedFoldersPanel.bSaveAndApply.ToolTipText.HasChange"));			
		} else {
			bSaveAndApply.setIcon(GREEN_BULLET_ICON);
			bSaveAndApply.setToolTipText(Messages.getString("ML.ManagedFoldersPanel.bSaveAndApply.ToolTipText.HasNoChange"));			
		}
	}
}
