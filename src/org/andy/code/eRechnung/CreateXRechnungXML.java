package org.andy.code.eRechnung;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.mustangproject.Invoice;
import org.mustangproject.ZUGFeRD.Profiles;
import org.mustangproject.ZUGFeRD.ZUGFeRD2PullProvider;

public class CreateXRechnungXML {

	public static void generateXRechnungXML(String[][] sArray, String sXmlName) throws ParseException, IOException {
		BufferedWriter writer = null;

		Invoice i = SetInvoiceEx.doInvoice(sArray);

		ZUGFeRD2PullProvider zf2p = new ZUGFeRD2PullProvider();
		zf2p.setProfile(Profiles.getByName("XRechnung"));
		zf2p.generateXML(i);
		String theXML = new String(zf2p.getXML());

		writer = new BufferedWriter(new FileWriter(sXmlName));
		writer.write(theXML);
		writer.close();
	}
}
