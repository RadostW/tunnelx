////////////////////////////////////////////////////////////////////////////////
// TunnelX -- Cave Drawing Program
// Copyright (C) 2002  Julian Todd.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
////////////////////////////////////////////////////////////////////////////////
package Tunnel;

import javax.swing.JFrame;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;

import java.awt.FileDialog;

import java.awt.Image;
import java.awt.Insets;
import java.awt.Dimension;

import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.geom.AffineTransform;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JProgressBar;

//
//
// SketchDisplay
//
//


// this class contains the whole outer set of options and buttons
class SketchDisplay extends JFrame
{
	MainBox mainbox;

	// the panel which holds the sketch graphics
	SketchGraphics sketchgraphicspanel;

	// the window with the symbols
	SymbolsDisplay symbolsdisplay;

	// the menu bar
	JMenuBar menubar = new JMenuBar();

	// file menu
	JMenu menufile = new JMenu("File");
	JMenuItem miCopyCentrelineElev = new JMenuItem("Copy Centreline Elev");

	JMenuItem miSaveSketch = new JMenuItem("Save");
	JMenuItem miSaveSketchAs = new JMenuItem("Save As...");
    JMenuItem miUploadSketch = new JMenuItem("Upload");
	JMenuItem miMakeImages = new JMenuItem("Make images"); 

	JMenuItem doneitem = new JMenuItem("Close");

	SketchLineStyle sketchlinestyle;

	SketchSubsetPanel subsetpanel;
	SelectedSubsetStructure selectedsubsetstruct; 

	SketchBackgroundPanel backgroundpanel;
	SketchInfoPanel infopanel;
	SketchPrintPanel printingpanel;
	SketchSecondRender secondrender;
    SketchZTiltPanel ztiltpanel; 
    TodeNodePanel todenodepanel; 
	
	JTabbedPane bottabbedpane;


	/////////////////////////////////////////////
	// inactivate case
	class SketchHide extends WindowAdapter implements ActionListener
	{
		void CloseWindow()
		{
			//mainbox.symbolsdisplay.hide();
			sketchgraphicspanel.ClearSelection(true);
			setVisible(false);
		}

		public void windowClosing(WindowEvent e)
		{
			CloseWindow();
		}

		public void actionPerformed(ActionEvent e)
		{
			CloseWindow();
		}
	}



	/////////////////////////////////////////////
	// View menu actions
	/////////////////////////////////////////////
	public class AcViewac extends AbstractAction
	{
		int viewaction;
		KeyStroke ks; 
        public AcViewac(String name, String shdesc, KeyStroke lks, int lviewaction)
		{
            super(name);
			ks = lks;
            putValue(SHORT_DESCRIPTION, shdesc);
			viewaction = lviewaction;
        }
        public void actionPerformed(ActionEvent e)
		{

			// Debugging
			// TN.emitMessage("ACTION:   " + viewaction +" occured");

			if (viewaction == 4)
				sketchgraphicspanel.Scale(0.5F);
			else if (viewaction == 5)
				sketchgraphicspanel.Scale(2.0F);
			else if (viewaction == 6) // right
				sketchgraphicspanel.Translate(-0.2F, 0.0F);
			else if (viewaction == 7) // left
				sketchgraphicspanel.Translate(0.2F, 0.0F);
			else if (viewaction == 8) // up
				sketchgraphicspanel.Translate(0.0F, 0.2F);
			else if (viewaction == 9) // down
				sketchgraphicspanel.Translate(0.0F, -0.2F);
			else if (viewaction == 10)
				sketchgraphicspanel.Translate(0.0F, 0.0F);
			else if (viewaction == 124)
				sketchgraphicspanel.Rotate(5.0F);
			else if (viewaction == 125)
				sketchgraphicspanel.Rotate(-5.0F);
			else if (viewaction == 126)
				ztiltpanel.MoveTiltPlane(1);
			else if (viewaction == 127)
				ztiltpanel.MoveTiltPlane(-1);
			else if (viewaction == 128)
				sketchgraphicspanel.ElevBackImageWarp();

			else if (viewaction == 122)
				sketchgraphicspanel.TiltView(15.0);
			else if (viewaction == 123)
				sketchgraphicspanel.TiltView(-15.0); 

			else if (viewaction == 21)
				backgroundpanel.SetGridOrigin(true);
			else if (viewaction == 22)
				backgroundpanel.SetGridOrigin(false);

			// 1, 2, 3, 11, 12, 121
			else
				sketchgraphicspanel.MaxAction(viewaction);
        }
	}

    int CTRL_DOWN_MASK = java.awt.event.InputEvent.CTRL_MASK; 
	int SHIFT_DOWN_MASK = java.awt.event.InputEvent.SHIFT_MASK; 
	int ALT_DOWN_MASK = java.awt.event.InputEvent.ALT_DOWN_MASK;

	AcViewac acvMax =          new AcViewac("Max",             "Maximize View", null, 2);
	AcViewac acvCentre =       new AcViewac("Centre",          "Centre View", null, 1);
	AcViewac acvMaxSubset =    new AcViewac("Max Subset",      "Maximize Subset View", KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, 0), 12);
	AcViewac acvMaxSelect =    new AcViewac("Max Select",      "Maximize Select View", null, 121);
	AcViewac acvCentreSubset = new AcViewac("Centre Subset",   "Centre Subset View", null, 11);
	AcViewac acvUpright =      new AcViewac("Upright",         "Upright View", null, 3);
	AcViewac acvScaledown =    new AcViewac("Scale Down",      "Zoom out", KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0), 4);
	AcViewac acvScaleup =      new AcViewac("Scale Up",        "Zoom in", KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, 0), 5);

	// Weird navigation with numpad
	AcViewac acvRight =        new AcViewac("Right",           "Translate view right", KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0), 6);
	AcViewac acvLeft =         new AcViewac("Left",            "Translate view left", KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0), 7);
	AcViewac acvUp =           new AcViewac("Up",              "Translate view up", KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0), 8);
	AcViewac acvDown =         new AcViewac("Down",            "Translate view down", KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, 0), 9);

	// Navigation with normal arrow keys	
	AcViewac acvRightAlt =     new AcViewac("Right",           "Translate view right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, SHIFT_DOWN_MASK), 6);
	AcViewac acvLeftAlt =      new AcViewac("Left",            "Translate view left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, SHIFT_DOWN_MASK), 7);
	AcViewac acvUpAlt =        new AcViewac("Up",              "Translate view up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, SHIFT_DOWN_MASK), 8);
	AcViewac acvDownAlt =      new AcViewac("Down",            "Translate view down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, SHIFT_DOWN_MASK), 9);


	AcViewac acvSetGridOrig =  new AcViewac("Set Grid Orig",   "Move the grid origin to the start node of selected line", null, 21);
	AcViewac acvResetGridOrig =new AcViewac("Reset Grid Orig", "Move the grid origin to original place", null, 22);
	AcViewac acvRedraw =       new AcViewac("Redraw",          "Redraw screen", null, 10);

	AcViewac acvTiltOver =     new AcViewac("Tilt Over",       "Tilt viewing plane away from face", null, 122);
	AcViewac acvTiltBack =     new AcViewac("Tilt Back",       "Tilt viewing plane back towards face", null, 123);
	AcViewac acvRotateRight =  new AcViewac("Rotate right", "Rotate viewing plane clockwise", KeyStroke.getKeyStroke(KeyEvent.VK_R, ALT_DOWN_MASK), 124);
	AcViewac acvRotateLeft =   new AcViewac("Rotate left", "Rotate viewing plane anti-clockwise", KeyStroke.getKeyStroke(KeyEvent.VK_L, ALT_DOWN_MASK), 125);
	AcViewac acvMovePlaneDown =new AcViewac("Move plane down", "tilt plane", KeyStroke.getKeyStroke(KeyEvent.VK_O, ALT_DOWN_MASK), 126);
	AcViewac acvMovePlaneUp =  new AcViewac("Move plane up", "tilt plane", KeyStroke.getKeyStroke(KeyEvent.VK_U, ALT_DOWN_MASK), 127);
	AcViewac acvElevImageWarp =new AcViewac("Elev Image Warp", "Selected line shears background image", null, 128);

	// view menu
	JMenu menuView = new JMenu("View");
	AcViewac[] acViewarr = { acvMaxSubset, acvMaxSelect, acvMax, acvCentre, 
	acvCentreSubset, acvUpright, acvScaledown, acvScaleup, 
	acvRight, acvLeft, acvUp, acvDown, 
	acvRightAlt, acvLeftAlt, acvUpAlt, acvDownAlt,
	acvRotateLeft, acvRotateRight, 
	acvMovePlaneDown, acvMovePlaneUp, acvSetGridOrig, acvResetGridOrig, acvRedraw, 
	acvTiltOver, acvTiltBack, acvElevImageWarp };


	/////////////////////////////////////////////
	// Display menu actions
	/////////////////////////////////////////////
	public class AcDispchbox extends AbstractAction
	{
		int backrepaint;
        public AcDispchbox(String name, String shdesc, int lbackrepaint)
		{
            super(name);
            putValue(SHORT_DESCRIPTION, shdesc);
			backrepaint = lbackrepaint;
        }
        public void actionPerformed(ActionEvent e)
		{
			if (backrepaint == 0)
				sketchgraphicspanel.RedrawBackgroundView();
			else
			{
				if (backrepaint == 2)
					selectedsubsetstruct.UpdateTreeSubsetSelection(subsetpanel.pansksubsetstree); 
				sketchgraphicspanel.RedoBackgroundView();
			}
		}
	}

	AcDispchbox acdCentreline =        new AcDispchbox("Centreline", "Centreline visible", 0);
	AcDispchbox acdStationNames =      new AcDispchbox("Station Names", "Station names visible", 0);
	AcDispchbox acdStationAlts =       new AcDispchbox("Station Altitudes", "Station altitudes visible", 0);
	AcDispchbox acdXSections =         new AcDispchbox("XSections", "Cross sections visible", 0);
	AcDispchbox acdTubes =             new AcDispchbox("Tubes", "Tubes visible", 0);
	AcDispchbox acdAxes =              new AcDispchbox("Axes", "Axes visible", 0);
	AcDispchbox acdDepthCols =         new AcDispchbox("Depth Colours", "Depth colours visible", 0);
	AcDispchbox acdShowNodes =         new AcDispchbox("Show Nodes", "Path nodes visible", 0);
	AcDispchbox acdShowBackground =    new AcDispchbox("Show Background", "Background image visible", 1);
	AcDispchbox acdShowGrid =          new AcDispchbox("Show Grid", "Background grid visible", 1);
	AcDispchbox acdTransitiveSubset =  new AcDispchbox("Transitive Subset", "View selected subsets and branches", 2);
	AcDispchbox acdInverseSubset =     new AcDispchbox("Inverse Subset", "Grey out the selected subsets", 2);
	AcDispchbox acdHideSplines =       new AcDispchbox("Hide Splines", "Show all paths as non-splined", 1);
	AcDispchbox acdNotDotted =         new AcDispchbox("Not dotted", "Make lines not dotted", 1);
	AcDispchbox acdShowTilt =          new AcDispchbox("Show tilted in z", "The tilt backwards feature", 0);
	AcDispchbox acdJigsawContour =     new AcDispchbox("Jigsaw Contour", "Make contours for laser cutting", 0);

	JCheckBoxMenuItem miCentreline =       new JCheckBoxMenuItem(acdCentreline);
	JCheckBoxMenuItem miStationNames =     new JCheckBoxMenuItem(acdStationNames);
	JCheckBoxMenuItem miStationAlts =      new JCheckBoxMenuItem(acdStationAlts);
	JCheckBoxMenuItem miDepthCols =        new JCheckBoxMenuItem(acdDepthCols);
	JCheckBoxMenuItem miShowNodes =        new JCheckBoxMenuItem(acdShowNodes);
	JCheckBoxMenuItem miShowBackground =   new JCheckBoxMenuItem(acdShowBackground);
	JCheckBoxMenuItem miShowGrid =         new JCheckBoxMenuItem(acdShowGrid);
	JCheckBoxMenuItem miTransitiveSubset = new JCheckBoxMenuItem(acdTransitiveSubset);
	JCheckBoxMenuItem miInverseSubset =    new JCheckBoxMenuItem(acdInverseSubset);
	JCheckBoxMenuItem miHideSplines =      new JCheckBoxMenuItem(acdHideSplines);
	JCheckBoxMenuItem miNotDotted =        new JCheckBoxMenuItem(acdNotDotted);

	JCheckBoxMenuItem miThinZheightsel =   new JCheckBoxMenuItem("Thin Z Selection", false);
	AcActionac acvThinZheightselWiden =    new AcActionac("Widen Z Selection", "Widen Z Selection", null, 96);
	AcActionac acvThinZheightselNarrow =   new AcActionac("Narrow Z Selection", "Narrow Z Selection", null, 97);
	JCheckBoxMenuItem miShowTilt =	       new JCheckBoxMenuItem(acdShowTilt);
	JCheckBoxMenuItem miJigsawContour =    new JCheckBoxMenuItem(acdJigsawContour);

	// display menu.
	JMenu menuDisplay = new JMenu("Display");
	JCheckBoxMenuItem[] miDisplayarr = { miJigsawContour, miCentreline, miStationNames, miStationAlts, miShowNodes, miDepthCols, miShowBackground, miShowGrid, miTransitiveSubset, miInverseSubset, miHideSplines, miNotDotted, miShowTilt };


	/////////////////////////////////////////////
	// Motion menu
	JCheckBoxMenuItem miTabletMouse =      new JCheckBoxMenuItem("Tablet Mouse",       false);
	JCheckBoxMenuItem miEnableRotate =     new JCheckBoxMenuItem("Enable rotate",      false);
	JCheckBoxMenuItem miTrackLines =       new JCheckBoxMenuItem("Track Lines",        false);
	JCheckBoxMenuItem miShearWarp =        new JCheckBoxMenuItem("Shear Warp",         false);
	JCheckBoxMenuItem miDefaultSplines =   new JCheckBoxMenuItem("Splines Default",    true);
	JCheckBoxMenuItem miSnapToGrid =       new JCheckBoxMenuItem("Snap to Grid",       false);
	JCheckBoxMenuItem miEnableDoubleClick =new JCheckBoxMenuItem("Enable double-click",true);

	JMenu menuMotion = new JMenu("Motion");
	JCheckBoxMenuItem[] miMotionarr = { miTabletMouse, miEnableRotate, miTrackLines, miShearWarp, miDefaultSplines, miSnapToGrid, miEnableDoubleClick };

	/////////////////////////////////////////////
	// Action menu actions
	/////////////////////////////////////////////
	public class AcActionac extends AbstractAction
	{
		int acaction;
		KeyStroke ks;
        public AcActionac(String name, String shdesc, KeyStroke lks, int lacaction)
		{
            super(name);
			ks = lks;
            putValue(SHORT_DESCRIPTION, shdesc);
			acaction = lacaction;
        }

		/////////////////////////////////////////////
        public void actionPerformed(ActionEvent e)
		{
			if (acaction == 4)
			{
				sketchgraphicspanel.ClearSelection(false);
				sketchgraphicspanel.repaint();
			}
			else if (acaction == 5)
				sketchgraphicspanel.DeleteSel();
			else if (acaction == 6)
				sketchgraphicspanel.FuseCurrent(miShearWarp.isSelected());
			else if (acaction == 7)
				sketchgraphicspanel.BackSelUndo();
			else if (acaction == 8)
				sketchgraphicspanel.ReflectCurrent();
			else if (acaction == 9)
				sketchgraphicspanel.SetAsAxis();
			else if (acaction == 83)
				sketchgraphicspanel.Makesquare();
			else if (acaction == 10)
				sketchgraphicspanel.MakePitchUndercut();
			else if ((acaction == 11) || (acaction == 12))
			{
				SketchLineStyle.SetStrokeWidths(SketchLineStyle.strokew * (acaction == 11 ? 2.0F : 0.5F), miNotDotted.isSelected());
                if (todenodepanel != null)
                    todenodepanel.BuildSpirals(); 
				sketchgraphicspanel.RedrawBackgroundView();
			}
			else if (acaction == 18)
				sketchgraphicspanel.SelectConnectedSetsFromSelection(); 
			else if (acaction == 14)
				sketchgraphicspanel.MoveGround(false);
			else if (acaction == 15)
				sketchgraphicspanel.MoveGround(true);
			else if (acaction == 16)
				backgroundpanel.NewBackgroundFile();
			else if (acaction == 177)
				backgroundpanel.UploadBackgroundFile();
			else if (acaction == 17)
				sketchlinestyle.pthstyleareasigtab.StyleMappingCopyButt(true); 

			else if (acaction == 20)
				{ SketchLineStyle.bDepthColours = false;  SketchLineStyle.bPathSubsetColours = false;  sketchgraphicspanel.RedrawBackgroundView();  }
			else if (acaction == 21)
				{ SketchLineStyle.SetIColsByZ(sketchgraphicspanel.tsketch.vpaths, sketchgraphicspanel.tsvpathsviz, sketchgraphicspanel.tsketch.vnodes, sketchgraphicspanel.tsketch.vsareas);  sketchgraphicspanel.RedrawBackgroundView();  }
			else if (acaction == 24)
				{ SketchLineStyle.bDepthColours = false;  SketchLineStyle.bPathSubsetColours = true;  sketchgraphicspanel.RedrawBackgroundView();  }
			else if (acaction == 22)
				{ OnePathNode ops = (sketchgraphicspanel.currpathnode != null ? sketchgraphicspanel.currpathnode : (sketchgraphicspanel.currgenpath != null ? sketchgraphicspanel.currgenpath.pnstart : null)); 
				  SketchLineStyle.SetIColsProximity(0, sketchgraphicspanel.tsketch, ops);  sketchgraphicspanel.RedrawBackgroundView();  }
			else if (acaction == 23)
				{ OnePathNode ops = (sketchgraphicspanel.currpathnode != null ? sketchgraphicspanel.currpathnode : (sketchgraphicspanel.currgenpath != null ? sketchgraphicspanel.currgenpath.pnstart : null)); 
				  sketchlinestyle.SetIColsProximity(1, sketchgraphicspanel.tsketch, ops);  sketchgraphicspanel.RedrawBackgroundView();  }

			// the automatic actions which should be running constantly in a separate thread
			else if ((acaction == 51) || (acaction == 58))
			{
				sketchgraphicspanel.UpdateZNodes();

				// do everything
				if (acaction == 58)
				{
					sketchgraphicspanel.UpdateSAreas();
					sketchgraphicspanel.GUpdateSymbolLayout(true, visiprogressbar);
					sketchgraphicspanel.bNextRenderDetailed = true;
				}
			}
			else if (acaction == 52)
				sketchgraphicspanel.UpdateSAreas();
			else if ((acaction == 53) || (acaction == 54))
				sketchgraphicspanel.GUpdateSymbolLayout((acaction == 54), visiprogressbar);
			else if (acaction == 56) // detail render
				sketchgraphicspanel.bNextRenderDetailed = true;

			else if (acaction == 57) // printing proximities to the command line
			{
				ProximityDerivation pd = new ProximityDerivation(sketchgraphicspanel.tsketch);
				pd.PrintCNodeProximity(3); // uses default settings in the pd.parainstancequeue
			}
			else if (acaction == 59)
				ReloadFontcolours();

			// subsets
			else if (acaction == 72)
				subsetpanel.AddSelCentreToCurrentSubset();
			else if (acaction == 77)
				subsetpanel.AddRemainingCentreToCurrentSubset();
			else if (acaction == 73)
				subsetpanel.PartitionRemainsByClosestSubset();
			else if (acaction == 733)
				subsetpanel.PartitionRemainsByClosestSubsetDatetype();
			else if (acaction == 74)
				subsetpanel.PutSelToSubset(true);
			else if (acaction == 75)
				subsetpanel.PutSelToSubset(false);
			else if (acaction == 76)
				subsetpanel.pansksubsetstree.clearSelection();
			else if (acaction == 78)
				subsetpanel.DeleteTodeleteSubset();
			else if (acaction == 79)
				subsetpanel.RemoveAllFromSubset();


			else if (acaction == 71)
				subsetpanel.ElevationSubset(true);
			else if (acaction == 711)
				subsetpanel.ElevationSubset(false);
			else if (acaction == 70)
				subsetpanel.sascurrent.ToggleViewHidden(selectedsubsetstruct.vsselectedsubsets, miTransitiveSubset.isSelected()); 

			// these ones don't actually need the repaint
			else if (acaction == 80)
				sketchlinestyle.SetConnTabPane("Symbol");
			else if (acaction == 81)
				sketchlinestyle.SetConnTabPane("Label");
			else if (acaction == 82)
				sketchlinestyle.SetConnTabPane("Area-Sig");

			else if (acaction == 91)
    			sketchgraphicspanel.bNextRenderPinkDownSketch = true;
			else if (acaction == 93)
            {
    			sketchgraphicspanel.bNextRenderAreaStripes = true;
                for (OneSArea osa : sketchgraphicspanel.tsketch.vsareas)
                    osa.Dgptriangulation = new DelTriangulation(osa); 
            }
            
			else if (acaction == 95)
				sketchgraphicspanel.ImportSketch(mainbox.tunnelfilelist.GetSelectedSketchLoad(), miImportCentreSubsetsU.isSelected(), miClearCentreSubsets.isSelected(), miImportNoCentrelines.isSelected());

            else if (acaction == 96)
                ztiltpanel.WidenTiltPlane(1); 
            else if (acaction == 97)
                ztiltpanel.WidenTiltPlane(-1); 

			// paper sizes
			else if (acaction == 405)
				sketchgraphicspanel.ImportPaperM("A5", 0.1485F, 0.210F);
			else if (acaction == 415)
				sketchgraphicspanel.ImportPaperM("A5_land", 0.210F, 0.1485F);
			else if (acaction == 404)
				sketchgraphicspanel.ImportPaperM("A4", 0.210F, 0.297F);
			else if (acaction == 414)
				sketchgraphicspanel.ImportPaperM("A4_land", 0.297F, 0.210F);
			else if (acaction == 403)
				sketchgraphicspanel.ImportPaperM("A3", 0.297F, 0.420F);
			else if (acaction == 413)
				sketchgraphicspanel.ImportPaperM("A3_land", 0.420F, 0.297F);
			else if (acaction == 402)
				sketchgraphicspanel.ImportPaperM("A2", 0.420F, 0.594F);
			else if (acaction == 412)
				sketchgraphicspanel.ImportPaperM("A2_land", 0.594F, 0.420F);
			else if (acaction == 401)
				sketchgraphicspanel.ImportPaperM("A1", 0.594F, 0.840F);
			else if (acaction == 411)
				sketchgraphicspanel.ImportPaperM("A1_land", 0.840F, 0.594F);
			else if (acaction == 400)
				sketchgraphicspanel.ImportPaperM("A0", 0.840F, 1.188F);
			else if (acaction == 410)
				sketchgraphicspanel.ImportPaperM("A0_land", 1.188F, 0.840F);
			// new survex label controls interface
			else if (acaction == 501)
				ImportSketchCentrelineFile(null); 
			else if (acaction == 502)
				ImportAtlasTemplate(); 
			else if (acaction == 510)
				ImportCentrelineLabel("preview"); 
			else if ((acaction == 511) || (acaction == 512))
			{
				ImportCentrelineLabel(acaction == 511 ? "normal" : "TOPelevation"); 
				sketchgraphicspanel.MaxAction(2); 
			}
			sketchgraphicspanel.repaint();
        }
	}

	// action menu	
	AcActionac acaDeselect =       new AcActionac("Deselect", "Deselect", null, 4);
	AcActionac acaDelete =         new AcActionac("Delete", "Delete selection", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, CTRL_DOWN_MASK), 5);
	AcActionac acaFuse =           new AcActionac("Fuse", "Fuse paths", null, 6);
	
	// undo action -- simple escape key
	AcActionac acaBackNodeAlt =    new AcActionac("Back", "Remove last hit", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 7);
	
	// back action -- backwards compat
	AcActionac acaBackNode =       new AcActionac("Back", "Remove last hit", KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, CTRL_DOWN_MASK), 7);


	AcActionac acaReflect =        new AcActionac("Reflect", "Reflect path", null, 8);
	AcActionac acaSetasaxis =      new AcActionac("Set As Axis", "Set As Axis", null, 9);
	AcActionac acaMakesquare =     new AcActionac("Make square", "Make square", null, 83);
	AcActionac acaPitchUndercut =  new AcActionac("Pitch Undercut", "Drop-down an invisible copy of a pitch boundary", null, 10);

	AcActionac acaStrokeThin =     new AcActionac("Stroke >>", "Thicker lines", KeyStroke.getKeyStroke(KeyEvent.VK_GREATER, CTRL_DOWN_MASK), 11);
	AcActionac acaStrokeThick =    new AcActionac("Stroke <<", "Thinner lines", KeyStroke.getKeyStroke(KeyEvent.VK_LESS, CTRL_DOWN_MASK), 12);

	AcActionac acaMovePicture =    new AcActionac("Shift View", "Moves view by according to path", null, 14);
	AcActionac acaMoveBackground = new AcActionac("Shift Ground", "Moves background image by according to path", null, 15);

	AcActionac acaAddImage =       new AcActionac("Add Image", "Adds a new background image to the sketch", null, 16);
	AcActionac acaReloadImage =    new AcActionac("Select Image", "Copies this background image to background of the sketch", null, 17);
	
    // could grey this one too
    AcActionac acaUploadImage =    new AcActionac("Upload Back Image", "Uploads this background image to the server", null, 177);

	AcActionac acaSelectComponent =new AcActionac("Component", "Selects Connected Component for selected edge", null, 18);
	JCheckBoxMenuItem miDeleteCentrelines = new JCheckBoxMenuItem("Allow Delete Centrelines", false);

	// connective type specifiers
	AcActionac acaConntypesymbols =new AcActionac("Add symbols", "Put symbols on connective path", null, 80);
	AcActionac acaConntypelabel =  new AcActionac("Write Text", "Put label on connective path", null, 81);
	AcActionac acaConntypearea =   new AcActionac("Area signal", "Put area signal on connective path", null, 82);

	JMenu menuAction = new JMenu("Action");
	AcActionac[] acActionarr = { acaDeselect, acaDelete, acaFuse, acaBackNode, acaBackNodeAlt, acaReflect, acaPitchUndercut, acaStrokeThin, acaStrokeThick, acaSetasaxis, acaMakesquare, acaMovePicture, acaMoveBackground, acaAddImage, acaUploadImage, acaSelectComponent, acaConntypesymbols, acaConntypelabel, acaConntypearea };
	AcActionac[] acPathcomarr = { acaReflect, acaFuse, acaSelectComponent, acaBackNode, acaBackNodeAlt, acaDelete };

	// auto menu
	AcActionac acaSetZonnodes =    new AcActionac("Update Node Z", "Set node heights from centreline", null, 51);
	AcActionac acaUpdateSAreas =   new AcActionac("Update Areas", "Update automatic areas", null, 52);
	AcActionac acaUpdateSymbolLayout = new AcActionac("Update Symbol Lay", "Update symbol layout in view", null, 53);
	AcActionac acaUpdateSymbolLayoutAll = new AcActionac("Update Symbol Lay All", "Update symbol layout Everywhere", null, 54);
	AcActionac acaDetailRender =   new AcActionac("Detail Render", "Detail Render", null, 56);
	AcActionac acaUpdateEverything = new AcActionac("Update Everything", "All updates in a row", null, 58);
	AcActionac acaReloadFontcolours = new AcActionac("Reload Fontcolours", "Makes all the subsets again", null, 59);

	JMenu menuAuto = new JMenu("Update");
	AcActionac[] acAutoarr = { acaSetZonnodes, acaUpdateSAreas, acaUpdateSymbolLayout, acaUpdateSymbolLayoutAll, acaDetailRender, acaUpdateEverything, acaReloadFontcolours };

	// import menu
	AcActionac acaPrevDownsketch = new AcActionac("Preview Down Sketch", "See the sketch that will be distorted", null, 91);

	AcActionac acaImportDownSketch = new AcActionac("Import Down Sketch", "Bring in the distorted sketch", null, 95);
	JCheckBoxMenuItem miImportTitleSubsets = new JCheckBoxMenuItem("*title Subsets", true);
	JCheckBoxMenuItem miImportDateSubsets = new JCheckBoxMenuItem("*date Subsets", false);
	JCheckBoxMenuItem miImportCentreSubsetsU = new JCheckBoxMenuItem("Import Cen-Subsets", true);
	JCheckBoxMenuItem miClearCentreSubsets = new JCheckBoxMenuItem("Clear Cen-Subsets", true);
	JCheckBoxMenuItem miImportNoCentrelines = new JCheckBoxMenuItem("Exclude Centrelines", true);
	JCheckBoxMenuItem miUseSurvex = new JCheckBoxMenuItem("Use Survex", false);
	JCheckBoxMenuItem miIncludeSplay = new JCheckBoxMenuItem("Include splays", true);
	JCheckBoxMenuItem miFileBeginPlot = new JCheckBoxMenuItem("File include plotting", false);

	AcActionac acaStripeAreas = new AcActionac("Stripe Areas", "See the areas filled with stripes", null, 93);

	AcActionac acaImportA5 =           new AcActionac("Make A5", "Make A5 rectangle", null, 405);
	AcActionac acaImportA5landscape =  new AcActionac("Make A5 landscape", "Make A5 rectangle landscape", null, 415);
	AcActionac acaImportA4 =           new AcActionac("Make A4", "Make A4 rectangle", null, 404);
	AcActionac acaImportA4landscape =  new AcActionac("Make A4 landscape", "Make A4 rectangle landscape", null, 414);
	AcActionac acaImportA3 =           new AcActionac("Make A3", "Make A3 rectangle", null, 403);
	AcActionac acaImportA3landscape =  new AcActionac("Make A3 landscape", "Make A3 rectangle landscape", null, 413);
	AcActionac acaImportA2 =           new AcActionac("Make A2", "Make A2 rectangle", null, 402);
	AcActionac acaImportA2landscape =  new AcActionac("Make A2 landscape", "Make A2 rectangle landscape", null, 412);
	AcActionac acaImportA1 =           new AcActionac("Make A1", "Make A1 rectangle", null, 401);
	AcActionac acaImportA1landscape =  new AcActionac("Make A1 landscape", "Make A1 rectangle landscape", null, 411);
	AcActionac acaImportA0 =           new AcActionac("Make A0", "Make A0 rectangle", null, 400);
	AcActionac acaImportA0landscape =           new AcActionac("Make A0 landscape", "Make A0 rectangle", null, 410);
	AcActionac[] acmenuPaper = { acaImportA5, acaImportA5landscape, acaImportA4, acaImportA4landscape, acaImportA3, acaImportA3landscape, acaImportA2, acaImportA2landscape, acaImportA1, acaImportA1landscape, acaImportA0, acaImportA0landscape };

	AcActionac acaImportCentrelineFile = new AcActionac("Import Survex File", "Loads a survex file into a Label", null, 501);
	AcActionac acaPreviewLabelWireframe = new AcActionac("Wireframe view", "Previews selected SVX data as Wireframe in Aven if available", null, 510);
	AcActionac acaImportLabelCentreline = new AcActionac("Import Centreline", "Imports selected SVX data from label", null, 511);
	AcActionac acaImportLabelCentrelineElev = new AcActionac("Import Centreline Elev", "Imports selected SVX data from label DistoX elevfile", null, 512);

	AcActionac acaImportAtlasTemplate =new AcActionac("Import Atlas", "Makes atlas from template", null, 502);

	JMenu menuImport = new JMenu("Import");

	JMenu menuImportPaper = new JMenu("Import Paper");

	// colour menu
	AcActionac acaColourDefault =      new AcActionac("Default", "Plain colours", null, 20);
	AcActionac acaColourByZ =          new AcActionac("Height", "Depth colours", null, 21);
	AcActionac acaColourByProx =       new AcActionac("Proximity", "Visualize proximity to selection", null, 22);
	AcActionac acaColourByCnodeWeight =new AcActionac("CNode Weights", "Visualize centreline node weights", null, 23);
	AcActionac acaColourBySubset =     new AcActionac("By Subset", "Set edge colours according to subset area colour", null, 24);
	AcActionac acaPrintProximities =   new AcActionac("Print Prox", "Print proximities of nodes to centrelines", null, 57);

	JMenu menuColour = new JMenu("Colour");

	// subset menu
	JMenu menuSubset = new JMenu("Subset");
	AcActionac acaAddCentreSubset =        new AcActionac("Add Centrelines", "Add all centrelines from selected survey to subset", null, 72);
	AcActionac acaAddRestCentreSubset =    new AcActionac("Add Rest Centrelines", "Add all centrelines not already in a subset", null, 77);
	AcActionac acaPartitionSubset =        new AcActionac("Partition Remains", "Put paths into nearest subset", null, 73);
	AcActionac acaPartitionSubsetDates =   new AcActionac("Partition Date Subsets", "Put paths into nearest date subset", null, 733);
	AcActionac acaAddToSubset =            new AcActionac("Add to Subset", "Add selected paths to subset", null, 74);
	AcActionac acaRemoveFromSubset =       new AcActionac("Remove from Subset", "Remove selected paths to subset", null, 75);
	AcActionac acaDeleteTodeleteSubset =   new AcActionac("Delete 'todelete' Subset", "Delete all paths in the 'todelete' subset", null, 78);
	AcActionac acaClearSubsetContents =    new AcActionac("Clear subset contents", "Remove all paths from subset", null, 79);
	AcActionac acaCleartreeSelection =     new AcActionac("Clear subset selection", "Clear selections on subset tree", null, 76);
	AcActionac acaToggleViewHidden =       new AcActionac("Toggle Hidden", "Change hidden subset settings", null, 70);
	AcActionac[] acSubsetarr = { acaToggleViewHidden, acaAddCentreSubset, acaAddRestCentreSubset, acaPartitionSubset, acaPartitionSubsetDates, acaAddToSubset, acaRemoveFromSubset, acaClearSubsetContents, acaDeleteTodeleteSubset, acaCleartreeSelection };

	JCheckBoxMenuItem miAutoAddToSubset =  new JCheckBoxMenuItem("Add new paths subset", false);

	JMenu menuElevation = new JMenu("Elevation");
	AcActionac acaXCSubset = new AcActionac("XC subset", "Make new cross-section subset", null, 71);
	AcActionac acaElevationSubset = new AcActionac("Elevation subset", "Make new elevation subset", null, 711);
	AcActionac[] acElevarr = { acaXCSubset, acaElevationSubset, };

    JProgressBar visiprogressbar = new JProgressBar(0, 100); 
    
	//AcActionac acaAddCentreSubset =        new AcActionac("Add Centrelines", "Add all centrelines from selected survey to subset", 0, 72);


	/////////////////////////////////////////////
	/////////////////////////////////////////////
	// set up the arrays
	SketchDisplay(MainBox lmainbox)
	{
		super("Sketch Display");

		// symbols communication.
		mainbox = lmainbox;
		
		// it's important that the two panels are constructed in order.
		sketchgraphicspanel = new SketchGraphics(this);		

		// the window with the symbols
		symbolsdisplay = new SymbolsDisplay(this);

		// sketch line style selection
		sketchlinestyle = new SketchLineStyle(symbolsdisplay, this);

		miSaveSketch.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) { SaveSketch(0); } } );
		menufile.add(miSaveSketch);
		miSaveSketchAs.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) { SaveSketch(1); } } );
		menufile.add(miSaveSketchAs);
		miUploadSketch.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) { SaveSketch(2); } } );
		menufile.add(miMakeImages); 
		miMakeImages.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) { MakeImages(); } } );
		menufile.add(miUploadSketch);

		doneitem.addActionListener(new SketchHide());
		menufile.add(doneitem);

		menubar.add(menufile);

		// view menu stuff.
		for (int i = 0; i < acViewarr.length; i++)
		{
			JMenuItem mi = new JMenuItem(acViewarr[i]);
			if (acViewarr[i].ks != null)
				mi.setAccelerator(acViewarr[i].ks);
			menuView.add(mi);
		}
		menubar.add(menuView);

		// setup the display menu responses
		for (int i = 0; i < miDisplayarr.length; i++)
		{
			boolean binitialstate = !(/*(miDisplayarr[i] == miShowBackground) ||*/
                                      (miDisplayarr[i] == miJigsawContour) ||
									  (miDisplayarr[i] == miStationNames) ||
									  (miDisplayarr[i] == miStationAlts) ||
									  //(miDisplayarr[i] == miTransitiveSubset) ||
									  (miDisplayarr[i] == miInverseSubset) ||
									  (miDisplayarr[i] == miShowTilt) ||
									  ((miDisplayarr[i] == miHideSplines) && !OnePath.bHideSplines) || 
									  ((miDisplayarr[i] == miNotDotted) && !FileAbstraction.bIsUnixSystem));

			miDisplayarr[i].setState(binitialstate);
			menuDisplay.add(miDisplayarr[i]);
		}
		menuDisplay.add(new JMenuItem(acaStripeAreas));
		menuDisplay.add(miThinZheightsel); 
		menuDisplay.add(new JMenuItem(acvThinZheightselWiden)); 
		menuDisplay.add(new JMenuItem(acvThinZheightselNarrow)); 
		
		menubar.add(menuDisplay);

		// yoke these checkboxes to ones in the background menu
		miShowBackground.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) {
				if (backgroundpanel.cbshowbackground.isSelected() != miShowBackground.isSelected())
				  backgroundpanel.cbshowbackground.setSelected(miShowBackground.isSelected());
			} } );
		miShowGrid.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) {
				if (backgroundpanel.cbshowgrid.isSelected() != miShowGrid.isSelected())
				  backgroundpanel.cbshowgrid.setSelected(miShowGrid.isSelected());
			} } );

		miHideSplines.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) { ApplySplineChange(miHideSplines.isSelected()); } } ); 
		miNotDotted.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) { SketchLineStyle.SetStrokeWidths(SketchLineStyle.strokew, miNotDotted.isSelected()); } } ); 
		

		miSnapToGrid.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) {
				if (backgroundpanel.cbsnaptogrid.isSelected() != miSnapToGrid.isSelected())
				  backgroundpanel.cbsnaptogrid.setSelected(miSnapToGrid.isSelected());
			} } );
		
        
		miShowTilt.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) {
				if (ztiltpanel.cbaShowTilt.isSelected() != miShowTilt.isSelected())
				  ztiltpanel.cbaShowTilt.setSelected(miShowTilt.isSelected());
			} } );
        
		miThinZheightsel.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) 
                { 
                    if (ztiltpanel.cbaThinZheightsel.isSelected() != miThinZheightsel.isSelected())
                        ztiltpanel.cbaThinZheightsel.setSelected(miThinZheightsel.isSelected());
                    ztiltpanel.ApplyZheightSelected(miThinZheightsel.isSelected()); 
                } 
            } ); 

		miJigsawContour.addActionListener(new ActionListener()
			{ public void actionPerformed(ActionEvent event) 
                { 
                    ztiltpanel.ApplyJigsawContour(miJigsawContour.isSelected()); 
                } 
            } ); 


		// motion menu
		for (int i = 0; i < miMotionarr.length; i++)
			menuMotion.add(miMotionarr[i]);
		menubar.add(menuMotion);

		// action menu stuff.
		for (int i = 0; i < acActionarr.length; i++)
		{
			JMenuItem mi = new JMenuItem(acActionarr[i]);
			if (acActionarr[i].ks != null)
				mi.setAccelerator(acActionarr[i].ks);
			menuAction.add(mi);
		}
		menuAction.add(miDeleteCentrelines); 
		menubar.add(menuAction);

		// auto menu
		for (int i = 0; i < acAutoarr.length; i++)
			menuAuto.add(new JMenuItem(acAutoarr[i]));
		menubar.add(menuAuto);

		// import menu
		menuImport.add(acaImportCentrelineFile); 

		miUseSurvex.setSelected(FileAbstraction.SurvexExists()); 
		miImportNoCentrelines.setToolTipText("Applies to Import Down Sketch only");
		miDeleteCentrelines.setToolTipText("Enable deletion of centrelines as well as other types"); 

		menuImport.add(miUseSurvex); 
		menuImport.add(miIncludeSplay); 
        menuImport.add(miFileBeginPlot); 
		menuImport.add(acaPreviewLabelWireframe); 
		menuImport.add(miImportTitleSubsets);
		menuImport.add(miImportDateSubsets); 
		menuImport.add(acaImportLabelCentreline); 
		menuImport.add(acaImportLabelCentrelineElev); 
		menuImport.add(new JMenuItem(acaPrevDownsketch));
		menuImport.add(miImportNoCentrelines);
		menuImport.add(miImportCentreSubsetsU);
		menuImport.add(miClearCentreSubsets);
		menuImport.add(new JMenuItem(acaImportDownSketch));

		for (int i = 0; i < acmenuPaper.length; i++)
			menuImportPaper.add(new JMenuItem(acmenuPaper[i]));
		menuImport.add(menuImportPaper);
		menuImportPaper.setToolTipText("Used to define the paper outline in a poster view"); 

		menuImport.add(new JMenuItem(acaImportAtlasTemplate)); 

		menubar.add(menuImport);

		// colour menu stuff.
		menuColour.add(new JMenuItem(acaColourDefault));
		menuColour.add(new JMenuItem(acaColourByZ));
		menuColour.add(new JMenuItem(acaColourByProx));
		menuColour.add(new JMenuItem(acaColourByCnodeWeight));
		menuColour.add(new JMenuItem(acaColourBySubset));
		menuColour.add(new JMenuItem(acaPrintProximities));
		menubar.add(menuColour);

		for (int i = 0; i < acElevarr.length; i++)
			menuElevation.add(new JMenuItem(acElevarr[i]));
		menubar.add(menuElevation);

		// subset menu stuff.
		menuSubset.add(new JMenuItem(acSubsetarr[0]));
		menuSubset.add(new JMenuItem(acSubsetarr[1]));
        menuSubset.add(miAutoAddToSubset); 
		for (int i = 0; i < acSubsetarr.length; i++)
			menuSubset.add(new JMenuItem(acSubsetarr[i]));
		menubar.add(menuSubset);

        if (mainbox.instanthelp != null)
        {
        	JMenu menuHelp = new JMenu("Help");
            for (JMenuItem mihelp : mainbox.instanthelp.mihelps)
                menuHelp.add(mihelp); 
            menubar.add(menuHelp); 
        }

		// menu bar is complete.
		setJMenuBar(menubar);

		// the panel of useful buttons that're part of the non-connective type display
		JPanel pnonconn = new JPanel(new GridLayout(0, 2));
        
		pnonconn.add(new JButton(acaStrokeThin));
		pnonconn.add(new JButton(acaStrokeThick));
		pnonconn.add(new JLabel());
		pnonconn.add(new JLabel());
		pnonconn.add(new JButton(acaSetZonnodes));
		pnonconn.add(new JButton(acaUpdateSAreas));
		pnonconn.add(new JButton(acaUpdateSymbolLayout));
		pnonconn.add(new JButton(acaDetailRender));
		pnonconn.add(new JLabel());
		pnonconn.add(new JLabel());

		pnonconn.add(new JLabel());
		pnonconn.add(new JLabel());
		pnonconn.add(new JLabel("Connective subtypes"));
		pnonconn.add(new JButton(acaConntypesymbols));
		pnonconn.add(new JButton(acaConntypelabel));
		pnonconn.add(new JButton(acaConntypearea));
		SetEnabledConnectiveSubtype(false);

		// we build one of the old tabbing panes into the bottom and have it
		sketchlinestyle.pthstylenonconn.setLayout(new BorderLayout());
		sketchlinestyle.pthstylenonconn.add(pnonconn, BorderLayout.CENTER);
		sketchlinestyle.pthstylenonconn.add(visiprogressbar, BorderLayout.SOUTH);;

		// put in the deselect and delete below the row of style buttons
		Insets inset = new Insets(1, 1, 1, 1);
		for (int i = 0; i < acPathcomarr.length; i++)
		{
			JButton butt = new JButton(acPathcomarr[i]); 
			butt.setMargin(inset);
			sketchlinestyle.pathcoms.add(butt);
		}

		subsetpanel = new SketchSubsetPanel(this);
	    selectedsubsetstruct = new SelectedSubsetStructure(this); 
		backgroundpanel = new SketchBackgroundPanel(this);
        infopanel = new SketchInfoPanel(this);
		printingpanel = new SketchPrintPanel(this); 
        secondrender = new SketchSecondRender(this); 
        ztiltpanel = new SketchZTiltPanel(this);  
        if (TN.bTodeNode)
            todenodepanel = new TodeNodePanel(this); 

		// do the tabbed pane of extra buttons and fields in the side panel.
		bottabbedpane = new JTabbedPane();
		bottabbedpane.addTab("subs",  null, subsetpanel,     "Subsets used in sketch and on the selected paths");
		bottabbedpane.addTab("img",   null, backgroundpanel, "Manage the background scanned image used for tracing");
		bottabbedpane.addTab("info",  null, infopanel,       "Inspect the raw information relating to a selected path");          // (sketchdisplay.bottabbedpane.getSelectedIndex() == 2)
		bottabbedpane.addTab("print", null, printingpanel,   "Set resolution for the rendered survey either to a file or to the internet");
        bottabbedpane.addTab("view",  null, secondrender,    "Secondary preview of sketch in a mini-window"); 
        bottabbedpane.addTab("tilt",  null, ztiltpanel,      "Tilt controls"); 
        if (TN.bTodeNode)
        {
            bottabbedpane.addTab("tode",  null, todenodepanel,    "Neuron experiment"); 
            bottabbedpane.setSelectedIndex(6); 
        }
        else
            bottabbedpane.setSelectedIndex(1); 

		bottabbedpane.addChangeListener(new ChangeListener()
			{ public void stateChanged(ChangeEvent event) { sketchgraphicspanel.UpdateBottTabbedPane(sketchgraphicspanel.currgenpath, sketchgraphicspanel.currselarea, true); } } );

		// the full side panel
		JSplitPane sidepanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sidepanel.setLeftComponent(sketchlinestyle);
		sidepanel.setRightComponent(bottabbedpane);
		sidepanel.setDividerLocation(300);
        sketchlinestyle.setMinimumSize(new Dimension(10, 10)); 
		bottabbedpane.setMinimumSize(new Dimension(10, 10)); 
        //JPanel sidepanel = new JPanel(new BorderLayout());
		//sidepanel.add(sketchlinestyle, BorderLayout.CENTER);
		//sidepanel.add(bottabbedpane, BorderLayout.SOUTH);
        sidepanel.setMinimumSize(new Dimension(10, 10)); 

		JPanel grpanel = new JPanel(new BorderLayout());
		grpanel.add(sketchgraphicspanel, BorderLayout.CENTER);

		// split pane between side panel and graphics area
		JSplitPane splitPaneG = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPaneG.setDividerLocation(300);

		splitPaneG.setLeftComponent(sidepanel);
		splitPaneG.setRightComponent(grpanel);


		// final set up of display
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splitPaneG, BorderLayout.CENTER);

		addWindowListener(new SketchHide());

		pack();
        setSize(800, 600);
		setLocation(300, 100);
    }


	/////////////////////////////////////////////
	void MakeImages()
	{
		Set<OnePath> opselset = sketchgraphicspanel.MakeTotalSelList(); 
		sketchgraphicspanel.UpdateZNodes();
		sketchgraphicspanel.UpdateSAreas();
		List<OneSArea> tvsareas = new ArrayList<OneSArea>(); 
		for (OneSArea osa : sketchgraphicspanel.tsketch.vsareas)  // vsareas is a sorted set so cannot be indexed
		{
			if (osa.iareapressig != SketchLineStyle.ASE_SKETCHFRAME)
				continue; 
			tvsareas.add(osa); 
		}
		for (int iosa = tvsareas.size() - 1; iosa >= 0; iosa--)   // do in reverse so last one made is first to generate
		{
			OneSArea osa = tvsareas.get(iosa); 
			for (int iskop = osa.opsketchframedefs.size() - 1; iskop >= 0; iskop--)  // might have same frame generating images at different resolutions
			{
				OnePath skop = osa.opsketchframedefs.get(iskop); 
				if (!opselset.isEmpty() && !opselset.contains(skop))
					continue;   // only do the selected ones if they are selected
				SketchFrameDef sketchframedef = skop.plabedl.sketchframedef; 
				List<String> losubsets = new ArrayList<String>(); 
				for (String subset : skop.vssubsets)
				{
					if (!subset.equals(TN.framestylesubset))
						losubsets.add(subset); 
				}
				if (losubsets.size() == skop.vssubsets.size())
					continue; // no framestyle subset listed
				if (losubsets.size() == 0)
					losubsets.add(""); 

				// set the outer style for this rendition
				if (!sketchframedef.sfstyle.equals(""))
				{
					int i = subsetpanel.Getcbsubsetstyleindex(sketchframedef.sfstyle); 
					if (i != -1)
					{
						SubsetAttrStyle sas = (SubsetAttrStyle)subsetpanel.jcbsubsetstyles.getItemAt(i); 
						subsetpanel.jcbsubsetstyles.setSelectedIndex(i); 
						subsetpanel.SubsetSelectionChanged(true); // needs to be called if done from file menu when the window thread is blocked
					}
				}
				for (int ilosubset = losubsets.size() - 1; ilosubset >= 0; ilosubset--)
				{
					String losubset = losubsets.get(ilosubset); 
System.out.println("llllllllll " + losubset); 
					if (!losubset.equals(""))
						subsetpanel.SelectSubset(losubset); 
					printingpanel.subsetrect = sketchgraphicspanel.tsketch.getBounds(true, true); 
		            printingpanel.UpdatePrintingRectangle(sketchgraphicspanel.tsketch.sketchLocOffset, sketchgraphicspanel.tsketch.realposterpaperscale, true); 
					printingpanel.tfdefaultsavename.setText(losubset); 
					if ((sketchframedef.imagepixelswidth != -1) && (sketchframedef.imagepixelsheight != -1))
					{
						printingpanel.tfpixelswidth.setText(String.valueOf(sketchframedef.imagepixelswidth)); 
						printingpanel.tfpixelsheight.setText(String.valueOf(sketchframedef.imagepixelsheight)); 
					}
					else if (sketchframedef.imagepixelswidth != -1)
					{
						printingpanel.tfpixelswidth.setText(String.valueOf(sketchframedef.imagepixelswidth)); 
						printingpanel.Updatefinalsize(1); 
					}
					else if (sketchframedef.imagepixelsheight != -1)
					{
						printingpanel.tfpixelsheight.setText(String.valueOf(sketchframedef.imagepixelsheight)); 
						printingpanel.Updatefinalsize(2); 
					}
					printingpanel.OutputIMG("png", 3, true); // 2 for set styles, 3 for everything
				}
			}
		}
	}

	/////////////////////////////////////////////
	boolean SaveSketch(int savetype)  // 0 save, 1 saveas, 2 upload
    {
        if (savetype == 1)
        {
            FileAbstraction lsketchfile = sketchgraphicspanel.tsketch.sketchfile.SaveAsDialog(SvxFileDialog.FT_XMLSKETCH, sketchgraphicspanel.sketchdisplay, false); 
            if (lsketchfile == null)
                return false; 
            sketchgraphicspanel.tsketch.sketchfile = lsketchfile; 
            sketchgraphicspanel.tsketch.sketchfile.xfiletype = FileAbstraction.FA_FILE_XML_SKETCH; 
            setTitle("TunnelX - " + sketchgraphicspanel.tsketch.sketchfile.getPath());
        }
        
        if (savetype == 2)
        {
            String target = TN.troggleurl + "jgtuploadfile";  // for now
            FileAbstraction uploadedimage = NetConnection.uploadFile(FileAbstraction.MakeOpenableFileAbstraction(target), "sketch", sketchgraphicspanel.tsketch.sketchfile.getSketchName() + ".xml", null, sketchgraphicspanel.tsketch); 
            if (uploadedimage == null)
                return TN.emitWarning("bum"); 
            TN.emitMessage("jjj   " + uploadedimage.getPath());
            FileAbstraction lsketchfile = FileAbstraction.GetImageFile(null, TN.setSuffix(uploadedimage.getPath(), TN.SUFF_XML));
			if (lsketchfile == null)
                return false; 
            sketchgraphicspanel.tsketch.sketchfile = lsketchfile; 

            setTitle("TunnelX - " + sketchgraphicspanel.tsketch.sketchfile.getPath());
    		mainbox.tunnelfilelist.tflist.repaint(); 

            return true; 
        }
        
        visiprogressbar.setString("saving");
        visiprogressbar.setStringPainted(true);
        visiprogressbar.setValue(80); 
        sketchgraphicspanel.tsketch.SaveSketch(); 
        visiprogressbar.setStringPainted(false);
        visiprogressbar.setValue(0); 

		mainbox.tunnelfilelist.tflist.repaint(); 
        return true;     
    }


	/////////////////////////////////////////////
	// switched on and off if we have a connective line selected
	void SetEnabledConnectiveSubtype(boolean benabled)
	{
		acaConntypesymbols.setEnabled(benabled);
		acaConntypelabel.setEnabled(benabled);
		acaConntypearea.setEnabled(benabled);
	}


	/////////////////////////////////////////////
	void ApplySplineChange(boolean lbHideSplines)
	{
		OnePath.bHideSplines = lbHideSplines;
		for (OneSketch tsketch : mainbox.ftsketches)
		{
			if (tsketch.bsketchfileloaded)
				tsketch.ApplySplineChange();
		}
		for (OneSketch tsketch : mainbox.vgsymbolstsketches)
		{
			if (tsketch.bsketchfileloaded)
				tsketch.ApplySplineChange();
		}
	}


	/////////////////////////////////////////////
	void ActivateSketchDisplay(OneSketch activesketch, boolean lbEditable)
	{
		sketchgraphicspanel.bEditable = lbEditable;
		sketchgraphicspanel.ClearSelection(true);

		sketchgraphicspanel.tsketch = activesketch;
		sketchgraphicspanel.asketchavglast = null; // used for lazy evaluation of the average transform.

		// set greyness
		acaUpdateSAreas.setEnabled(!sketchgraphicspanel.tsketch.bSAreasUpdated);
		acaUpdateSymbolLayout.setEnabled(!sketchgraphicspanel.tsketch.bSymbolLayoutUpdated);

		// set the transform pointers to same object
		setTitle(activesketch.sketchfile.getPath());
		
		// it's confusing if this applies to different views
		if (!miThinZheightsel.isSelected())
			miThinZheightsel.setSelected(false);

// could record the last viewing position of the sketch; saved in the sketch as an affine transform
		sketchgraphicspanel.MaxAction(2); // maximize

		sketchgraphicspanel.UpdateBottTabbedPane(null, null, true); 

		String sfstyle = ""; 
			// quick and dirty hunt for what you might want as the default
		if (sketchgraphicspanel.tsketch.sksascurrent == null)
		{
			int isfstylescore = 0; 
			for (OnePath op : sketchgraphicspanel.tsketch.vpaths)
			{
				int lisfstylescore = 2; 
				if (op.IsSketchFrameConnective() && !op.plabedl.sketchframedef.sfstyle.equals(""))
				{
					if (op.plabedl.sketchframedef.sfsketch.equals("") || op.plabedl.sketchframedef.IsImageType())
						lisfstylescore = 3; 
					for (String subset : op.vssubsets)
					{
						if (subset.equals(TN.framestylesubset))
							lisfstylescore = 4; 
					}
					if (lisfstylescore >= isfstylescore)
					{
						sfstyle = op.plabedl.sketchframedef.sfstyle; 
						isfstylescore = lisfstylescore; 
					}
				}
				if (op.IsSurvexLabel())
				{
					if ((subsetpanel.jcbsubsetstyles.getItemCount() != 0) && (isfstylescore == 0))
					{
						sfstyle = ((SubsetAttrStyle)subsetpanel.jcbsubsetstyles.getItemAt(0)).stylename; 
						isfstylescore = 1; 
					}
				}
			}

			TN.emitMessage("Choosing default sfstyle: "+sfstyle); 
		}
		else
		{
			sfstyle = sketchgraphicspanel.tsketch.sksascurrent.stylename; 
			TN.emitMessage("Resetting previous sfstyle: "+sfstyle); 
		}
		subsetpanel.SetSubsetStyleFromString(sfstyle); 

        printingpanel.ResetDIR((TN.currprintdir == null));  // catch it here
        infopanel.searchlistmodel.clear(); 

		toFront();
		setVisible(true);
		sketchgraphicspanel.repaint();
	}


	/////////////////////////////////////////////
	void ReloadFontcolours()
	{
        FileAbstraction.imagefiledirectories.clear(); 
        SketchLineStyle.nareasignames = 0; 
        sketchlinestyle.subsetattrstylesmap.clear(); 
		sketchlinestyle.bsubsetattributesneedupdating = true;

		for (FileAbstraction tfile : mainbox.allfontcolours)
			mainbox.tunnelloader.LoadFontcolour(tfile);

		if (sketchlinestyle.bsubsetattributesneedupdating)
			sketchlinestyle.UpdateSymbols(false);
		if (sketchgraphicspanel.tsketch != null)
			SketchGraphics.SketchChangedStatic(SketchGraphics.SC_CHANGE_SAS, sketchgraphicspanel.tsketch, this);
		mainbox.tunnelfilelist.tflist.repaint();
		miUseSurvex.setSelected(FileAbstraction.SurvexExists()); 
	}
	

	/////////////////////////////////////////////
	boolean ImportAtlasTemplate()
	{
		AtlasGenerator ag = new AtlasGenerator(); 
		OneSketch asketch = mainbox.tunnelfilelist.GetSelectedSketchLoad(); 
		if (asketch == null)
			return TN.emitWarning("No Sketch selected in mainbox which to use as the atlas template for duplicating into current sketch"); 
		if (asketch.sksascurrent == null)
			asketch.SetSubsetAttrStyle(sketchgraphicspanel.tsketch.sksascurrent, null);
		
		asketch.UpdateSomething(SketchGraphics.SC_UPDATE_ZNODES, true); 
		asketch.UpdateSomething(SketchGraphics.SC_UPDATE_AREAS, true); 
		mainbox.UpdateSketchFrames(asketch, SketchGraphics.SC_UPDATE_ALL_BUT_SYMBOLS); 

		ag.ImportAtlasTemplate(asketch);

		List<OnePath> pthstoadd = new ArrayList<OnePath>(); 
		pthstoadd.addAll(ag.vpathsatlas); 
		sketchgraphicspanel.CommitPathChanges(null, pthstoadd); 

		sketchgraphicspanel.UpdateBottTabbedPane(null, null, true); 
		subsetpanel.SubsetSelectionChanged(true);
		sketchgraphicspanel.MaxAction(2); // maximize
		return true; 
	}

	/////////////////////////////////////////////
	boolean ImportSketchCentrelineFile(SvxFileDialog sfiledialog)
	{
		OnePath optext; 

		if (sketchgraphicspanel.currgenpath == null)
		{
    	    List<OnePath> pthstoadd = new ArrayList<OnePath>();
			optext = sketchgraphicspanel.MakeConnectiveLineForData(1, TN.radiusofsurveylabel_S); 
			pthstoadd.add(optext); 
			sketchgraphicspanel.CommitPathChanges(null, pthstoadd); // selects
		}
		else
		{
			sketchlinestyle.GoSetParametersCurrPath();
			optext = sketchgraphicspanel.currgenpath;
		}


		if (!sketchgraphicspanel.bEditable || (optext == null) || (optext.linestyle != SketchLineStyle.SLS_CONNECTIVE) || (optext.plabedl == null) || optext.plabedl.sfontcode.equals(""))
			return TN.emitWarning("Connective Path with label must be created or selected");

		if (sfiledialog == null)
        {
            sfiledialog = SvxFileDialog.showOpenDialog(TN.currentDirectory, this, SvxFileDialog.FT_SVX, false);
            if ((sfiledialog == null) || ((sfiledialog.svxfile == null) && (sfiledialog.tunneldirectory == null)))
                return false;
            // was all made complicated and broken because of ability to save and rewrite the name of file if .xml left off it
            FileAbstraction fa = sfiledialog.svxfile; // sfiledialog.getSelectedFileA(SvxFileDialog.FT_SVX, false);
            if (fa.localurl == null)
                TN.currentDirectory = fa; 
    		TN.emitMessage(sfiledialog.svxfile.toString() + "  CD " + TN.currentDirectory.getAbsolutePath() + "  " + (fa.localurl == null));
        }

		String survextext = null; 
        if (sfiledialog.svxfile.xfiletype == FileAbstraction.FA_FILE_SVX) 
            survextext = (new SurvexLoaderNew()).LoadSVX(sfiledialog.svxfile);
        else if (sfiledialog.svxfile.xfiletype == FileAbstraction.FA_FILE_POCKET_TOPO) 
        {
            PocketTopoLoader ptl = new PocketTopoLoader(); 
            ptl.LoadPockettopo(sfiledialog.svxfile); 
            survextext = ptl.GetSVX();
			sketchgraphicspanel.CommitPathChanges(null, ptl.vpathsplan); 
       }
        else if (sfiledialog.svxfile.xfiletype == FileAbstraction.FA_FILE_POCKET_BINTOP) 
        {
            TunnelTopParser tunnTOP = new TunnelTopParser(); 
            tunnTOP.ParseTOPFile(sfiledialog.svxfile); 
            survextext = tunnTOP.GetSVX();
			for (OnePath op : tunnTOP.vpathsplan)
				op.vssubsets.add(TN.planCLINEsubset); 
			sketchgraphicspanel.CommitPathChanges(null, tunnTOP.vpathsplan); 
			for (OnePath op : tunnTOP.vpathselev)
				op.vssubsets.add(TN.elevCLINEsubset); 
			sketchgraphicspanel.CommitPathChanges(null, tunnTOP.vpathselev); 
			optext.vssubsets.add(TN.planCLINEsubset); 
			optext.vssubsets.add(TN.elevCLINEsubset); 
        }
        else
            TN.emitError("unknown file type loader " + sfiledialog.svxfile.xfiletype); 

        // select and apply the svx text
        sketchgraphicspanel.SelectSingle(optext);   // selects
		sketchlinestyle.pthstylelabeltab.Setlabtextfield(survextext); // the document events and the window copies it into optext
		if (sfiledialog == null)
			sketchgraphicspanel.MaxAction(2); // maximize
		else
			System.out.println("not doing maxaction"); 
		return true; 
	}

	/////////////////////////////////////////////
	boolean ImportCentrelineLabel(String scommand)
	{
        boolean bfilebeginmode = miFileBeginPlot.isSelected(); 
		TN.emitMessage("ImportCentrelineLabel scommand="+scommand); 
		boolean bpreview = scommand.equals("preview"); 
        // switch off survex if tmp not available
        if (miUseSurvex.isSelected() && !FileAbstraction.tmpdir.isDirectory())
		{
			// why isn't this working?
            TN.emitWarning("Cannot run survex without tunnelx/tmp directory");
            TN.emitWarning("Switching off Import | Use Survex flag"); 
            miUseSurvex.setEnabled(false); 
		}

		boolean btopextendedelevation = scommand.equals("TOPelevation"); 
        boolean busesurvex = !scommand.equals("nosurvex") && !btopextendedelevation && miUseSurvex.isSelected(); 
        boolean bincludesplay = miIncludeSplay.isSelected(); 

        // find the survex path label if we don't have the path with the raw label of the survex text selected
		OnePath opcll = sketchgraphicspanel.currgenpath;
        if ((opcll == null) || !opcll.IsSurvexLabel())
        {
            // don't use tspathssurvexlabel as may be out of date when making new sketch
			for (OnePath op : sketchgraphicspanel.tsketch.vpaths)
			{
				if (op.IsSurvexLabel())
					opcll = op; 
			}
        }

		if ((opcll == null) || !opcll.IsSurvexLabel())
			return !TN.emitWarning("Connective Path with label containing the survex data must be selected");

		boolean bplancase = opcll.vssubsets.contains(TN.planCLINEsubset); 
		boolean belevcase = opcll.vssubsets.contains(TN.elevCLINEsubset); 

		// load in the centreline we have into the sketch
		// could even check with centreline existing
// this is how we do the extending of centrelines
//		if (!bpreview && !sketchgraphicspanel.tsketch.sketchLocOffset.isZero())
//			return !TN.emitWarning("Sketch Loc Offset already set; pos already have loaded in a centreline");

		// set to null (for the purpose of resetting) only if not set already and there are no non-connective paths already drawn
		Vec3 appsketchLocOffset = sketchgraphicspanel.tsketch.sketchLocOffset; 
		if (sketchgraphicspanel.tsketch.sketchLocOffset.isZero())
		{
			int nnonconnectivepaths = 0; 
			for (OnePath op : sketchgraphicspanel.tsketch.vpaths)
			{
				if (op.linestyle != SketchLineStyle.SLS_CONNECTIVE)
					nnonconnectivepaths++; 
			}
			if (nnonconnectivepaths == 0)
				appsketchLocOffset = null; 
		}
		
		// run survex cases
		SurvexLoaderNew sln = null; 
		if (!bpreview || !busesurvex)
		{
			sln = new SurvexLoaderNew();
			sln.btopextendedelevation = btopextendedelevation; 
			sln.InterpretSvxText(opcll.plabedl.drawlab);
			TN.emitMessage("---------number of legs "+sln.osfileblockmap.size() + " and blocks "+sln.vfilebeginblocklegs.size()); 
            if (bfilebeginmode)
            {
                sketchgraphicspanel.tsketch.sksascurrent.filebeginblockrootleg = sln.filebeginblockrootleg; 
                subsetpanel.SubsetSelectionChanged(true); 
            }
        }

		boolean bsurvexfailed = false; // carry this signal through so we can still plot with the beginfilemode
		if (busesurvex) // copy in the POS files
		{
			bsurvexfailed = !FileAbstraction.RunSurvex(sln, opcll.plabedl.drawlab, appsketchLocOffset, bpreview); 
			if (bsurvexfailed && !bfilebeginmode)
				return false; 
			if (bpreview)
				return true; 
		}
		else
		{
			TN.emitWarning("Not using Survex, so no distributing of loop closure errors"); 
			sln.sketchLocOffset = (appsketchLocOffset == null ? new Vec3d((float)sln.avgfix.x, (float)sln.avgfix.y, (float)sln.avgfix.z) : new Vec3d((float)appsketchLocOffset.x, (float)appsketchLocOffset.y, (float)appsketchLocOffset.z)); 
			sln.CalcStationPositions(false);
		}
		
        if (bfilebeginmode)
        {
            // this sets according to a vector direction applied to each leg (also creates all the stations, which is useful)
			sln.CalcStationPositions(true);
            
            // this sets the location of the file/begin stations to the average of the stations underneath it
            //sln.vfilebeginblocklegs.get(0).SetAvgFileBeginLocRecurse(); 

            sln.vfilebeginblocklegs.get(0).SetTreeFileBeginLocRecurse(0, 0); 
        }

		if (bpreview) // show preview
		{
			mainbox.wireframedisplay.wiregraphicspanel.vlegs.clear(); 
			mainbox.wireframedisplay.wiregraphicspanel.vstations.clear(); 
			sln.ConstructWireframe(mainbox.wireframedisplay.wiregraphicspanel.vlegs, mainbox.wireframedisplay.wiregraphicspanel.vstations);
			mainbox.wireframedisplay.ActivateWireframeDisplay("Name of sketch");
			return true;
		}
		
        sketchgraphicspanel.ClearSelection(true);
		
		// set the Locoffset
		if (bsurvexfailed)
			;
		else if (appsketchLocOffset == null)
			sketchgraphicspanel.tsketch.sketchLocOffset = new Vec3((float)sln.sketchLocOffset.x, (float)sln.sketchLocOffset.y, (float)sln.sketchLocOffset.z);
		else
			assert Math.abs(appsketchLocOffset.x - sketchgraphicspanel.tsketch.sketchLocOffset.x) < 0.000001; 
			
		// do anaglyph rotation here
		// TransformSpaceToSketch tsts = new TransformSpaceToSketch(currgenpath, sketchdisplay.mainbox.sc);
		// statpathnode[ipns] = tsts.TransPoint(ol.osfrom.Loc);

		Vec3 xrot, yrot, zrot; 
		double rotanaglyph = 0.0; // hard code this and recompile before reimporting
		//double rotanaglyph = -5.0 * Math.PI / 180; // hard code this and recompile before reimporting
        if (sln.bprojectedelevation)
		{
			double th = sln.projectedelevationvalue * Math.PI / 180; 
			xrot = new Vec3((float)Math.cos(th), (float)Math.sin(th), 0.0F); 
			yrot = new Vec3(0.0F, 0.0F, 1.0F); 
			zrot = new Vec3(-(float)Math.sin(th), (float)Math.cos(th), 0.0F); 
            TN.emitWarning("\n\n\n*****\n*****elevation rotation th " + th + "\n*****\n\n\n"); 
		}
		else if (rotanaglyph != 0.0)
        {
            double cs = Math.cos(rotanaglyph); 
            double sn = Math.sin(rotanaglyph); 
            xrot = new Vec3((float)cs, 0.0F, (float)sn); 
			yrot = new Vec3(0.0F, 1.0F, 0.0F); 
			zrot = new Vec3(-(float)sn, 0.0F, (float)cs); 
            TN.emitWarning("\n\n\n*****\n*****anaglyph rot sn " + sn + "\n*****\n\n\n"); 
        }
        else
		{
            xrot = new Vec3(1.0F, 0.0F, 0.0F); 
			yrot = new Vec3(0.0F, 1.0F, 0.0F); 
			zrot = new Vec3(0.0F, 0.0F, 1.0F); 
		}
        

		double xsmin = 0.0; 
		double ysmin = 0.0; 
		boolean bfirstsmin = true; 
		if (!bsurvexfailed)
		{
			Vec3 fsketchLocOffset = new Vec3((float)sln.sketchLocOffset.x, (float)sln.sketchLocOffset.y, (float)sln.sketchLocOffset.z); 
			sketchgraphicspanel.tsketch.sketchLocOffset = new Vec3(fsketchLocOffset.Dot(xrot), fsketchLocOffset.Dot(yrot), fsketchLocOffset.Dot(zrot)); 

			List<OneStation> vstations = new ArrayList<OneStation>(); 
			for (OneStation os : sln.osmap.values())
			{
				if (os.station_opn == null)
				{
					vstations.add(os); 
					assert os.Loc != null; 
					os.station_opn = new OnePathNode(os.Loc.Dot(xrot) * TN.CENTRELINE_MAGNIFICATION, -os.Loc.Dot(yrot) * TN.CENTRELINE_MAGNIFICATION, os.Loc.Dot(zrot) * TN.CENTRELINE_MAGNIFICATION);
					xsmin = (bfirstsmin ? os.station_opn.pn.getX() : Math.min(os.station_opn.pn.getX(), xsmin)); 
					ysmin = (bfirstsmin ? os.station_opn.pn.getY() : Math.min(os.station_opn.pn.getY(), ysmin)); 
					bfirstsmin = false; 
				}
			}
		}
		
        if (bfilebeginmode)
        {
            for (OneStation os : sln.osfileblockmap.values())
            {
                assert os.Loc != null; 
                os.station_opn = new OnePathNode(os.Loc.Dot(xrot) * TN.CENTRELINE_MAGNIFICATION, -os.Loc.Dot(yrot) * TN.CENTRELINE_MAGNIFICATION, os.Loc.Dot(zrot) * TN.CENTRELINE_MAGNIFICATION);
                xsmin = (bfirstsmin ? os.station_opn.pn.getX() : Math.min(os.station_opn.pn.getX(), xsmin)); 
                ysmin = (bfirstsmin ? os.station_opn.pn.getY() : Math.min(os.station_opn.pn.getY(), ysmin)); 
                bfirstsmin = false; 
            }
        }

		if (!btopextendedelevation)
		{
			if (!sln.ThinDuplicateLegs(sketchgraphicspanel.tsketch.vnodes, sketchgraphicspanel.tsketch.vpaths))
				return TN.emitWarning("Cannot copy over extended legs"); 
		}
		
		boolean bcopytitles = (miImportTitleSubsets.isSelected() && !bfilebeginmode);
		boolean bcopydates = (miImportDateSubsets.isSelected() && !bfilebeginmode); 
		int Dnsurfacelegs = 0; 
		int Dnfixlegs = 0; 
        int Dnsplaylegs = 0; 

		List<OnePath> pthstoadd = new ArrayList<OnePath>(); 
		List<OnePath> pthstoremove = new ArrayList<OnePath>(); 

        // perform the translation of the "S"  (could also make it bigger)
        {
            double vx = xsmin - opcll.pnstart.pn.getX(); 
            double vy = ysmin - opcll.pnend.pn.getY(); 

    		OnePathNode nopnstart = new OnePathNode((float)(opcll.pnstart.pn.getX() + vx), (float)(opcll.pnstart.pn.getY() + vy), opcll.pnstart.zalt); 
    		OnePathNode nopnend = new OnePathNode((float)(opcll.pnend.pn.getX() + vx), (float)(opcll.pnend.pn.getY() + vy), opcll.pnend.zalt); 
			float[] pco = opcll.GetCoords();
			OnePath nopcll = new OnePath(nopnstart); 
			for (int i = 1; i < opcll.nlines; i++)
				nopcll.LineTo((float)(pco[i * 2] + vx), (float)(pco[i * 2 + 1] + vy)); 
			nopcll.EndPath(nopnend); 
			nopcll.CopyPathAttributes(opcll); 

            pthstoremove.add(opcll); 
			pthstoadd.add(nopcll); 
        }

        // add in all the legs to the adding section
		if (!bsurvexfailed)
		{
			for (OneLeg ol : sln.vlegs)
			{
				if (ol.osfrom == null)
					Dnfixlegs++; 
				else if (ol.bsurfaceleg)
					Dnsurfacelegs++; 
                else if (ol.bsplayleg && !bincludesplay)
					Dnsplaylegs++; 
				else
				{
					OnePath lop = new OnePath(ol.osfrom.station_opn, ol.osfrom.name, ol.osto.station_opn, ol.osto.name);
					if (bcopytitles && !ol.svxtitle.equals(""))
						lop.vssubsets.add(ol.svxtitle);
					if (bcopydates && !ol.svxdate.equals(""))
						lop.vssubsets.add("__date__ " + ol.svxdate.substring(0, 4)); 
					if (bplancase || belevcase)
						lop.vssubsets.add(btopextendedelevation ? TN.elevCLINEsubset : TN.planCLINEsubset); 
					if (bfilebeginmode)
						lop.vssubsets.add(ol.llcurrentfilebeginblockleg.stto); 
					pthstoadd.add(lop); 
				}
			}
		}
		if (bfilebeginmode)
        {
            for (OneLeg ol : sln.vfilebeginblocklegs)
            {
                OnePath lop = new OnePath(ol.osfrom.station_opn, ol.osfrom.name, ol.osto.station_opn, ol.osto.name);
                lop.linestyle = (ol.osto.name.endsWith(".") ? SketchLineStyle.SLS_DETAIL : SketchLineStyle.SLS_INVISIBLE); 
                lop.vssubsets.add(ol.stto); 
                lop.vssubsets.add("fileblocks");
                pthstoadd.add(lop); 
				TN.emitMessage("** " + ol.osto.name + "  "+ ol.lowerfilebegins.size()); 
                if (ol.lowerfilebegins.size() == 0)
                {
                    OnePath loplab = new OnePath(ol.osto.station_opn, ol.osto.name, ol.osto.station_opn, ol.osto.name); 
                    loplab.linestyle = SketchLineStyle.SLS_CONNECTIVE; 
                    loplab.plabedl = new PathLabelDecode();
                    loplab.plabedl.drawlab = "hi there";
					loplab.plabedl.sfontcode = "default";
                    pthstoadd.add(loplab); 
                }
            }
        }
        
		TN.emitMessage("Ignoring " + Dnfixlegs + " fixlegs and " + Dnsurfacelegs + " surfacelegs and " + Dnsplaylegs + " splaylegs"); 

		sketchgraphicspanel.asketchavglast = null; // change of avg transform cache.
		sketchgraphicspanel.CommitPathChanges(pthstoremove, pthstoadd); 

        // check everything has been designated centreline nodes after the above commit
        if (!bfilebeginmode)
        {
            for (OneLeg ol : sln.vlegs)
            {
                if (!ol.bsurfaceleg && (!ol.bsplayleg || bincludesplay))
                {
                    assert ((ol.osfrom == null) || ol.osfrom.station_opn.IsCentrelineNode()); 
                    assert (ol.osto.station_opn.IsCentrelineNode()); 
                }
            }
        }
        
		//subsetpanel.SubsetSelectionChanged(true);
		return true;
	}
}



