package org.andy.code.eRechnung;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.andy.code.dataStructure.entitiyMaster.Bank;
import org.andy.code.dataStructure.entitiyMaster.Kunde;
import org.andy.code.dataStructure.entitiyMaster.Owner;
import org.andy.code.dataStructure.entitiyProductive.Rechnung;
import org.mustangproject.Invoice;
import org.mustangproject.ZUGFeRD.Profiles;
import org.mustangproject.ZUGFeRD.ZUGFeRD2PullProvider;

public class CreateXRechnungXML {

	public static void generateXRechnungXML(Rechnung rechnung, Bank bank, Kunde kunde, Owner owner, String sXmlName) throws ParseException, IOException {
		BufferedWriter writer = null;

		Invoice i = SetInvoiceEx.doInvoice(rechnung, bank, kunde, owner);

		ZUGFeRD2PullProvider zf2p = new ZUGFeRD2PullProvider();
		zf2p.setProfile(Profiles.getByName("XRechnung"));
		zf2p.generateXML(i);
		String theXML = new String(zf2p.getXML());

		writer = new BufferedWriter(new FileWriter(sXmlName));
		writer.write(theXML);
		writer.close();
	}
}
