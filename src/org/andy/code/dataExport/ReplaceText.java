package org.andy.code.dataExport;

public class ReplaceText {

	private static final String RE = "{RE}";
	private static final String AN = "{AN}";
	private static final String BESTNR = "{Best-Nr}";
	private static final String DATUM = "{Datum}";
	private static final String TAGE = "{Tage}";
	private static final String NAME = "{Name}";
	private static final String WERT = "{Wert}";
	private static final String MAHN_NR = "{NrMahn}";
	private static final String SPESEN = "{Spesen}";
	private static final String OWNER = "{OwnerName}";

	public static String doReplace(String sInput, String sRE, String sAN, String sBestNr, String sDatum, String sTage, String sName,
			String sWert, String sNrMahn, String sSpesen, String sOwnerName) throws Exception {
		return replace(sInput, sRE, sAN, sBestNr, sDatum, sTage, sName, sWert, sNrMahn, sSpesen, sOwnerName);
	}

	private static String replace(String sInput, String sRE, String sAN, String sBestNr, String sDatum, String sTage, String sName,
			String sWert, String sNrMahn, String sSpesen, String sOwnerName) throws Exception {
		String sTmp = sInput;
		if(!sRE.equals("none")) {
			sTmp = sTmp.replace(RE, sRE);
		}
		if(!sAN.equals("none")) {
			sTmp = sTmp.replace(AN, sAN);
		}
		if(!sBestNr.equals("none")) {
			sTmp = sTmp.replace(BESTNR, sBestNr);
		}
		if(!sDatum.equals("none")) {
			sTmp = sTmp.replace(DATUM, sDatum);
		}
		if(!sTage.equals("none")) {
			sTmp = sTmp.replace(TAGE, sTage);
		}
		if(!sName.equals("none")) {
			sTmp = sTmp.replace(NAME, sName);
		}
		if(!sWert.equals("none")) {
			sTmp = sTmp.replace(WERT, sWert);
		}
		if(!sNrMahn.equals("none")) {
			sTmp = sTmp.replace(MAHN_NR, sNrMahn);
		}
		if(!sSpesen.equals("none")) {
			sTmp = sTmp.replace(SPESEN, sSpesen);
		}
		if(!sOwnerName.equals("none")) {
			sTmp = sTmp.replace(OWNER, sOwnerName);
		}
		return sTmp;
	}

}
