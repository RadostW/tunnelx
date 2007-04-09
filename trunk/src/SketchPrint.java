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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.Rectangle;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.awt.RenderingHints;
import java.awt.AlphaComposite; 
import java.awt.Composite; 

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

//import javax.swing.text.*;
//import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import java.awt.GridLayout;

import javax.imageio.ImageIO;






//
//
// SketchPrint
//
//
// this should be a full-blown printpreview.
class SketchPrint implements Printable
{
	// objects brought from SketchGraphics
	OneSketch tsketch;
	JFrame frame;
	Dimension csize; // used for print view
	AffineTransform currtrans;

	boolean bHideCentreline;
	boolean bHideMarkers;
	boolean bHideStationNames;
	OneTunnel vgsymbols;

	AffineTransform mdtrans = new AffineTransform();

	/////////////////////////////////////////////
	// printing constants
	double prtxlo;
	double prtxhi;
	double prtylo;
	double prtyhi;

	double prtpagewidth;
	double prtpageheight;
	int nptrpagesx;
	int nptrpagesy;
	int prtscalecode;

	// used because we don't see page format in the printthis function
	int nprintcalls;

 	double prtimgscale;

	double prtimageablebordermm = 5.0; // in mm
	double prtimageablewidth;
	double prtimageableheight;
	double prtimageablex;
	double prtimageabley;

	Line2D prtimageablecutrectangle[] = new Line2D[4];
	boolean bdrawcutoutrectangle;

	boolean bsinglepageenabled;
	int singlepagenx;
	int singlepageny;


	// page format information
	double pfimageablewidth;
	double pfimageableheight;
	double pfimageableX;
	double pfimageableY;

	/////////////////////////////////////////////
	boolean PrintScaleSetup()
	{
		double pcentmargin = 0.005;
		Rectangle2D boundrect = tsketch.getBounds(true, false);
		prtxlo = boundrect.getX() - boundrect.getWidth() * pcentmargin;
		prtxhi = boundrect.getX() + boundrect.getWidth() * (1 + pcentmargin);
		prtylo = boundrect.getY() - boundrect.getHeight() * pcentmargin;
		prtyhi = boundrect.getY() + boundrect.getHeight() * (1 + pcentmargin);
		System.out.println("Image dimensions");
		System.out.println("prtxlo " + prtxlo + " prtxhi " + prtxhi + "\nprtylo " + prtylo + " prtyhi " + prtyhi);

		double prtimageableborderpt = prtimageablebordermm / 25.4 * 72.0;
		prtimageablewidth = pfimageablewidth - prtimageableborderpt * 2;
		prtimageableheight = pfimageableheight - prtimageableborderpt * 2;
		prtimageablex = pfimageableX + prtimageableborderpt;
		prtimageabley = pfimageableY + prtimageableborderpt;

		System.out.println(prtimageablewidth);
		System.out.println(prtimageableheight);
		System.out.println(prtimageablex);
		System.out.println(prtimageabley);

		// do the rectangle with four lines so the dashes line up, and move out by half a linewidth.
		double lnwdisp = SketchLineStyle.printcutoutlinestyleattr.GetStrokeWidth() / 2;
		prtimageablecutrectangle[0] = new Line2D.Double(prtimageablex - lnwdisp, prtimageabley - lnwdisp, 	prtimageablex - lnwdisp, prtimageabley + prtimageableheight + lnwdisp);
		prtimageablecutrectangle[1] = new Line2D.Double(prtimageablex - lnwdisp, prtimageabley - lnwdisp, 	prtimageablex + prtimageablewidth + lnwdisp, prtimageabley - lnwdisp);
		prtimageablecutrectangle[2] = new Line2D.Double(prtimageablex + prtimageablewidth + lnwdisp, prtimageabley - lnwdisp, 	prtimageablex + prtimageablewidth + lnwdisp, prtimageabley + prtimageableheight + lnwdisp);
		prtimageablecutrectangle[3] = new Line2D.Double(prtimageablex - lnwdisp, prtimageabley + prtimageableheight + lnwdisp, 	prtimageablex + prtimageablewidth + lnwdisp, prtimageabley + prtimageableheight + lnwdisp);

		TN.emitMessage("Page dimensions in points inch-width:" + pfimageablewidth / 72.0 + "  inch-height:" + pfimageableheight / 72.0);

		// crazy doing it this far down the line, but seems necessary.
		PrintScaleDialog psd = new PrintScaleDialog(frame, prtxhi - prtxlo, prtyhi - prtylo, prtimageablewidth, prtimageableheight);
		int n = psd.getScale();
		if (n == -1) // returns -1 if user cancelled operation
			return false;
		bdrawcutoutrectangle = psd.cutoutrectangle.isSelected();
		bsinglepageenabled = psd.singlepageenabled.isSelected();
		if (bsinglepageenabled)
		{
			singlepagenx = Integer.parseInt(psd.pagenx.getText());
			singlepageny = Integer.parseInt(psd.pageny.getText());
		}

		prtimgscale = n / 72.0 * 0.254;

		System.out.println("Printing to scale: " + prtimgscale);

		prtpagewidth = prtimageablewidth * prtimgscale;
		prtpageheight = prtimageableheight * prtimgscale;
		System.out.println("prtpagewidth " + prtpagewidth + " prtpageheight " + prtpageheight);

		nptrpagesx = (int)((prtxhi - prtxlo) / prtpagewidth + 1.0);
		nptrpagesy = (int)((prtyhi - prtylo) / prtpageheight + 1.0);
		System.out.println("npages w " + nptrpagesx + " h " + nptrpagesy);

		// force down to one page
		if (psd.forceonepage.isSelected())
		{
			nptrpagesx = 1;
			nptrpagesy = 1;
		}

		nprintcalls = 0;
		return true;
	}


	/////////////////////////////////////////////
	void PrintThisNon() throws Exception
	{
		PrinterJob printJob = PrinterJob.getPrinterJob();

		if(printJob.printDialog())
		{
			PageFormat pf = new PageFormat();
			pf = printJob.defaultPage();
			pf = printJob.pageDialog(pf);
			printJob.setPrintable(this, pf);
			printJob.print();
		}
	}

	/////////////////////////////////////////////
	void PrintThisJSVG() throws Exception
	{
		FileAbstraction fout = FileAbstraction.MakeWritableFileAbstraction("ssvg.svg");
		TN.emitMessage("Writing file " + fout.getName());
		LineOutputStream los = new LineOutputStream(fout);
		SvgGraphics2D svgg = new SvgGraphics2D(los);

		Rectangle2D bounds = tsketch.getBounds(true, false);
		svgg.writeheader((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth(), (float)bounds.getHeight());
		tsketch.paintWqualitySketch(new GraphicsAbstraction(svgg), true, vgsymbols, null);
		svgg.writefooter();
		los.close();
	}

	/////////////////////////////////////////////
	void PrintThisPYVTK() throws Exception
	{
		FileAbstraction fout = FileAbstraction.MakeWritableFileAbstraction("pyvtk.xml");
		TN.emitMessage("Writing file " + fout.getName());
		LineOutputStream los = new LineOutputStream(fout);
		pyvtkGraphics2D pyvtk = new pyvtkGraphics2D(los);
		boolean bRefillOverlaps = false;

		Rectangle2D bounds = tsketch.getBounds(false, false);
		pyvtk.writeheader((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth(), (float)bounds.getHeight());
		for (OneSArea osa : tsketch.vsareas)
		{
			if (osa.iareapressig == SketchLineStyle.ASE_KEEPAREA)
				pyvtk.writearea(osa);
		}

		//tsketch.paintWquality(svgg, bHideCentreline, bHideMarkers, bHideStationNames, vgsymbols);
		pyvtk.writefooter();
		los.close();
	}

	/////////////////////////////////////////////
	void PrintThis(int lprtscalecode, boolean lbHideCentreline, boolean lbHideMarkers, boolean lbHideStationNames, OneTunnel lvgsymbols, SketchLineStyle sketchlinestyle, OneSketch ltsketch, Dimension lcsize, AffineTransform lcurrtrans, JFrame inframe)
	{
		frame = inframe;

		tsketch = ltsketch;
		csize = lcsize;
		currtrans = lcurrtrans;
		bHideCentreline = lbHideCentreline;
		bHideMarkers = lbHideMarkers;
		bHideStationNames = lbHideStationNames;
		vgsymbols = lvgsymbols;
		bHideMarkers = true;
		prtscalecode = lprtscalecode;

		// counts the times the print function gets called
		// we know the paper size on the first call and can deal with it.
		nprintcalls = 0;

		try
		{
		if (lprtscalecode == 2)
			PrintThisPYVTK();
		else if (lprtscalecode == 3)
			PrintThisJSVG();
		else if (lprtscalecode == 5)
			assert false; //PrintThisBitmap(sketchlinestyle);
//		else if (lprtscalecode == 7)
//			PrintThisSVG();
		else
			PrintThisNon();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}





	/////////////////////////////////////////////
	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException
	{
		// copy in the values
		if (nprintcalls == 0)
		{
			pfimageablewidth = pf.getImageableWidth();
			pfimageableheight = pf.getImageableHeight();
			pfimageableX = pf.getImageableX();
			pfimageableY = pf.getImageableY();
		}

		// call the dialog and set the scaling up (except when it's fit to screen).
		if ((prtscalecode != 0) && (nprintcalls == 0))
		{
			if (!PrintScaleSetup())
				return Printable.NO_SUCH_PAGE; // wonder if this will work?
		}

		Graphics2D g2D = (Graphics2D)g;
		GraphicsAbstraction ga = new GraphicsAbstraction(g2D);
		try
		{
			return lprint(ga, pi);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return Printable.NO_SUCH_PAGE;
 	}

	/////////////////////////////////////////////
	// local version with the pageformat removed
	public int lprint(GraphicsAbstraction ga, int pi) throws Exception
	{
		if (prtscalecode != 0)
		{
			TN.emitMessage("Page " + pi + "  : calls " + nprintcalls);
			if (pi >= nptrpagesx * nptrpagesy)
				return Printable.NO_SUCH_PAGE;
			int ipy = pi / nptrpagesx;
			int ipx = pi - ipy * nptrpagesx;
			if (bsinglepageenabled)
			{
				if (pi >= 1)
					return Printable.NO_SUCH_PAGE;
				ipy = singlepageny;
				ipx = singlepagenx;
			}

			double pvx = (prtxlo + prtxhi - nptrpagesx * prtpagewidth) / 2 + ipx * prtpagewidth;
			double pvy = (prtylo + prtyhi - nptrpagesy * prtpageheight) / 2 + ipy * prtpageheight;

			// draw the cutout rectangle in page space
			if(bdrawcutoutrectangle)
			{
				for (int i = 0; i < prtimageablecutrectangle.length; i++)
					ga.drawShape(prtimageablecutrectangle[i], SketchLineStyle.printcutoutlinestyleattr);
			}

			// translate to scale space
			mdtrans.setToTranslation(prtimageablex, prtimageabley);
			mdtrans.scale(1.0F / prtimgscale, 1.0F / prtimgscale);
			mdtrans.translate(-pvx, -pvy);

			ga.transform(mdtrans);
			ga.SetMainClip();
		}

		// fit to screen
		else
		{
			if (pi >= 1)
				return Printable.NO_SUCH_PAGE;

			if (nprintcalls == 0)
				TN.emitMessage("Page dimensions in points inch-width:" + pfimageablewidth/72 + "  inch-height:" + pfimageableheight/72);
			//TN.emitMessage("Page dimensions in points inch-width:" + pf.getImageableWidth()/72 + "  inch-height:" + pf.getImageableHeight()/72);
			mdtrans.setToTranslation((pfimageableX + pfimageablewidth / 2), (pfimageableY + pfimageableheight / 2));

			// scale change relative to the size it's on the screen, so that what's on the screen is visible
			double scchange = Math.min(csize.width / (pfimageablewidth * 1.0F), csize.height / (pfimageableheight * 1.0F));
			mdtrans.scale(scchange, scchange);
			mdtrans.translate(-csize.width / 2, -csize.height / 2);

			// translation is relative to the screen translation; but if you have a better idea you can hard code it.
			ga.transform(mdtrans);
			ga.transform(currtrans);
			ga.SetMainClip();
		}

		// do the drawing of it
		tsketch.paintWqualitySketch(ga, true, vgsymbols, null);
		nprintcalls++;
		return Printable.PAGE_EXISTS;
	}
};



