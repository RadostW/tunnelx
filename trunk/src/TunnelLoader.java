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

import java.io.File; 
import java.io.IOException;  


//
//
// TunnelLoader
//
//


/////////////////////////////////////////////
/////////////////////////////////////////////
class TunnelLoader
{
	TunnelXMLparse txp; 
	TunnelXML tunnXML; 

	/////////////////////////////////////////////
	void emitError(String mess, IOException e) throws IOException
	{
		TN.emitError(mess); 
		throw e; 
	}


	/////////////////////////////////////////////
	void LoadSVXdata(OneTunnel tunnel)  
	{
		try 
		{
			LineInputStream lis = new LineInputStream(tunnel.svxfile, null, null); 

			// strip the *begins and *includes 
			while (lis.FetchNextLine())  
			{
				if (lis.w[0].equalsIgnoreCase("*begin")) 
					; 
				else if (lis.w[0].equalsIgnoreCase("*end")) 
					; 
				else if (lis.w[0].equalsIgnoreCase("*include")) 
					; 
				else 
					tunnel.AppendLine(lis.GetLine()); 
			}

			lis.close(); 
		}
		catch (IOException ie) 
		{
			TN.emitWarning(ie.toString()); 		
		}; 
	}

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	boolean LoadDirectoryRecurse(OneTunnel tunnel, File loaddirectory) throws IOException
	{
		tunnel.tundirectory = loaddirectory; 

		//TN.emitMessage("Dir " + loaddirectory.getName()); 
		if (!loaddirectory.isDirectory())  
			emitError("file not a directory " + loaddirectory.toString(), new IOException()); 

		boolean bsomethinghere = false; 
		File[] sfiles = loaddirectory.listFiles(); 

		// here we begin to open XML readers and such like, filling in the different slots.  
		for (int i = 0; i < sfiles.length; i++) 
		{
			if (sfiles[i].isFile())  
			{
				String suff = TN.getSuffix(sfiles[i].getName()); 

				if (suff.equals(TN.SUFF_XML))  
				{
					//TN.emitMessage("parsing " + sfiles[i].getName()); 
					txp.SetUp(tunnel, TN.loseSuffix(sfiles[i].getName())); 
					tunnXML.ParseFile(txp, sfiles[i]); 

					// fill in the file positions according to what was in this file.  
					if (txp.bContainsExports) 
					{
						tunnel.exportfile = sfiles[i]; 
						tunnel.bexportfilechanged = false; 
					}
					else if (txp.bContainsMeasurements)  
					{
						tunnel.xmlfile = sfiles[i]; 
						tunnel.bxmlfilechanged = false; 
					}
					else if (txp.nsketches == 1)  
					{
						OneSketch sketch = (OneSketch)tunnel.tsketches.lastElement(); 
						sketch.sketchfile = sfiles[i]; 
						sketch.bsketchfilechanged = false; 
					}

					bsomethinghere = true; 
				}

				else if (suff.equals(TN.SUFF_SVX))  
				{
					if (tunnel.svxfile != null)  
						TN.emitError("two svx files in same directory"); 
					tunnel.svxfile = sfiles[i]; 
					tunnel.bsvxfilechanged = false; 
					LoadSVXdata(tunnel); 

					bsomethinghere = true; 
				}
				else if (suff.equals(TN.SUFF_PNG) || suff.equalsIgnoreCase(TN.SUFF_GIF) || suff.equalsIgnoreCase(TN.SUFF_JPG))  
					tunnel.imgfiles.addElement(sfiles[i]); 
				else if (suff.equalsIgnoreCase(TN.SUFF_TXT))  
					; 
				else 
					TN.emitMessage("Unknown file type " + sfiles[i].getName()); 
			}
		}


		// get the subdirectories and recurse.  
		for (int i = 0; i < sfiles.length; i++) 
		{
			if (sfiles[i].isDirectory())  
			{
				String dtname = sfiles[i].getName(); 
				OneTunnel dtunnel = tunnel.IntroduceSubTunnel(new OneTunnel(dtname, null)); 

				if (!LoadDirectoryRecurse(dtunnel, sfiles[i])) 
					tunnel.ndowntunnels--; // if there's nothing interesting, take this introducedd tunnel back out!  
				else 
					bsomethinghere = true; 
			}
		}
		return bsomethinghere; 
	}

	/////////////////////////////////////////////
	public TunnelLoader(OneTunnel filetunnel, File loaddirectory, boolean lbSymbolType)  
	{
		// check that saved directory is good.  
		try
		{
			// create the directory tree
			txp = new TunnelXMLparse(); 
			txp.bSymbolType = lbSymbolType; 

			tunnXML = new TunnelXML(); 
			LoadDirectoryRecurse(filetunnel, loaddirectory); 
		}
		catch (IOException ie) 
		{
			TN.emitWarning(ie.toString()); 		
			ie.printStackTrace();
		}  
		catch (NullPointerException e) 
		{
			TN.emitWarning(e.toString()); 		
			e.printStackTrace();
		}; 
	}
}; 
