////////////////////////////////////////////////////////////////////////////////
// Tunnel v2.0 copyright Julian Todd 1999.  
// shared with version 1
////////////////////////////////////////////////////////////////////////////////

package Tunnel;

import java.util.Vector; 




/////////////////////////////////////////////
class EqVec extends Vector
{
	OneTunnel eqtunnelroot = null; 

	/////////////////////////////////////////////
	Eq GetAt(int i)
	{
		return((Eq)(elementAt(i))); 
	}

	/////////////////////////////////////////////
	Eq FindEq(OneTunnel leqtunnel, int icode)
	{
		if (leqtunnel == null) 
			System.out.println("Bad Find equ code:" + String.valueOf(icode)); 

		for (int i = 0; i < size(); i++)
		{
			Eq eq = GetAt(i); 
			if (eq.eqtunnel == leqtunnel) 
				return eq; 
		}
		return null; 
	}

	/////////////////////////////////////////////
	void AddEquateValue(Eq eqval) 
	{
		// build in the link 
		Eq eql = FindEq(eqval.eqtunnel, 1); 
		if (eql != null)
		{
			eqval.eqlink = eql.eqlink; 
			eql.eqlink = eqval; 
		}

		// update the root value 
		if (size() != 0)
		{
			// we keep moving root up till we score a hit.  
			while (true)
			{
				if (eqtunnelroot == null) 
				{
					System.out.println("eq overflow on " + eqval.toString()); 
					DumpOut(); 
					break; 
				}

				OneTunnel eqtscan = eqval.eqtunnel; 
				while (eqtscan != null)
				{
					if (eqtscan == eqtunnelroot)
						break; 
					eqtscan = eqtscan.uptunnel; 
				}
				
				if (eqtscan != null)
					break; 
				eqtunnelroot = eqtunnelroot.uptunnel; 
			}
		}
		else
			eqtunnelroot = eqval.eqtunnel; 

		// put it into the array 
		addElement(eqval); 
	}

	/////////////////////////////////////////////
	Eq AddEquateValue(OneTunnel leqtunnel, String leqstationname) 
	{
		if (leqtunnel == null) 
			System.out.println("how?"); 

		// first derive a unique name
		String uniquename = leqstationname; 
		for (int n = 1; true; n++)
		{
			int i; 
			for (i = leqtunnel.stationnames.size() - 1; i >= 0; i--)
			{
				if (uniquename.equalsIgnoreCase((String)(leqtunnel.stationnames.elementAt(i))))
					break; 
			}
			if (i == -1)
				break; 

			uniquename = leqstationname + "_" + String.valueOf(n); 
		}

		// now apply it 
		Eq eqval = new Eq(leqtunnel, uniquename); 
		AddEquateValue(eqval); 
		return eqval; 
	}


	/////////////////////////////////////////////
	// fill in the missing values 
	// this adds values into the array which later gets extended 
	boolean MakeEquateLine(Eq eqval)
	{
		if (eqval.eqtunnel != eqtunnelroot)
		{
			if (eqval.eqtunnel.uptunnel == null) 
			{
				System.out.println("export overflow"); 
				return false; 
			}

			Eq equp = FindEq(eqval.eqtunnel.uptunnel, 2); 
			if (equp == null)
			{
				String exprefix = (eqval.eqtunnel.name.length() != 0 ? eqval.eqtunnel.name + "." : ""); 
				equp = AddEquateValue(eqval.eqtunnel.uptunnel, exprefix + eqval.eqstationname); 
				equp.eqtunnel.stationnames.addElement(equp.eqstationname); 
			}
			eqval.eqtunnel.vexports.addElement(new OneExport(eqval.eqstationname, equp.eqstationname)); 
		}
		return true; 		
	}

	/////////////////////////////////////////////
	// move the root up one spot  
	void ExtendRootIfNecessary()
	{
		// extend it if the root equate value is not unique
		Eq eqroot = FindEq(eqtunnelroot, 3); 
		if ((eqroot != null) && (eqroot.eqlink != eqroot))
		{
			AddEquateValue(eqtunnelroot.uptunnel, eqtunnelroot.uptunnel.name + "." + eqroot.eqstationname); 
		}
	}


	/////////////////////////////////////////////
	void DumpOut() 
	{
		if (eqtunnelroot != null) 
			System.out.println("roottunn: " + eqtunnelroot.name); 
		for (int i = 0; i < size(); i++)
			System.out.println(((Eq)(elementAt(i))).toString());  
	}
}
