package org.andy.fx.code.googleServices;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.documentai.v1.*;
import com.google.protobuf.ByteString;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.andy.fx.code.googleServices.InterfaceBuilder.DocAiConfig;
import org.andy.fx.code.googleServices.InterfaceBuilder.InvoiceExtractionResult;
import org.andy.fx.code.googleServices.InterfaceBuilder.InvoiceExtractor;

public class CloudInvoiceExtractor implements InvoiceExtractor {
	
	private final DocAiConfig cfg;

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public CloudInvoiceExtractor(DocAiConfig cfg) {
		this.cfg = cfg;
	}

	@Override
	public InvoiceExtractionResult extract(Path pdf) throws Exception {
		String endpoint = cfg.location() + "-documentai.googleapis.com:443";
		DocumentProcessorServiceSettings s = DocumentProcessorServiceSettings.newBuilder().setEndpoint(endpoint)
				.build();
		try (DocumentProcessorServiceClient c = DocumentProcessorServiceClient.create(s)) {
			String name = "projects/%s/locations/%s/processors/%s".formatted(cfg.projectId(), cfg.location(),
					cfg.processorId());
			byte[] content = Files.readAllBytes(pdf);
			ProcessRequest req = ProcessRequest.newBuilder().setName(name).setRawDocument(RawDocument.newBuilder()
					.setContent(ByteString.copyFrom(content)).setMimeType("application/pdf").build()).build();
			ProcessResponse resp = c.processDocument(req);
			Document d = resp.getDocument();

			var header = new LinkedHashMap<String, String>();
			var items = new ArrayList<java.util.Map<String, String>>();
			String currency = null;
			
			for (Document.Entity e : d.getEntitiesList()) {
				switch (e.getType()) {
				case "invoice_id" -> header.put("invoiceId", val(e));
				case "invoice_date" -> header.put("invoiceDate", norm(e));
				case "due_date" -> header.put("dueDate", norm(e));
				case "supplier_name" -> header.put("supplierName", norm(e));
				case "supplier_address" -> header.put("supplierAddress", norm(e));
				case "supplier_tax_id" -> header.put("supplierTaxId", norm(e));
				case "supplier_registration" -> header.put("supplierRegistration", norm(e));
				case "supplier_phone" -> header.put("supplierPhone", norm(e));
				case "supplier_email" -> header.put("supplierEmail", norm(e));
				case "supplier_iban" -> header.put("supplierIBAN", norm(e));
				case "supplier_website" -> header.put("supplierWebsite", norm(e));
				case "receiver_name" -> header.put("receiverName", norm(e));
				case "receiver_address" -> header.put("receiverAddress", norm(e));
				case "receiver_tax_id" -> header.put("receiverTaxId", norm(e));
				case "ship_to_name" -> header.put("ShipToName", norm(e));
				case "ship_to_address" -> header.put("ShipToAddress", norm(e));
				case "remit_to_name" -> header.put("RemitToName", norm(e));
				case "remit_to_address" -> header.put("RemitToAddress", norm(e));
				case "currency" -> currency = norm(e);
				case "vat" -> header.put("taxRate", norm(e));
				case "net_amount" -> header.put("netAmount", norm(e));
				case "total_tax_amount" -> header.put("taxAmount", norm(e));
				case "total_amount" -> header.put("totalAmount", norm(e));
				case "line_item" -> {
					var row = new java.util.LinkedHashMap<String, String>();
					for (Document.Entity p : e.getPropertiesList()) {
						switch (p.getType()) {
						case "line_item/description" -> row.put("description", val(p));
						case "line_item/quantity" -> row.put("quantity", norm(p));
						case "line_item/unit_price" -> row.put("unit_price", norm(p));
						case "line_item/amount" -> row.put("amount", norm(p));
						}
					}
					items.add(row);
				}
				}
			}
			return new InvoiceExtractionResult(header, items, currency, d.getText());
		} catch (ApiException ex) {
			throw ex; // Orchestrator fängt ab und fällt zurück
		}
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static String val(Document.Entity e) {
		String t = e.getTextAnchor().getContent();
		return (t == null || t.isBlank()) ? e.getMentionText() : t;
	}

	private static String norm(Document.Entity e) {
		return e.hasNormalizedValue() && !e.getNormalizedValue().getText().isBlank() ? e.getNormalizedValue().getText()
				: val(e);
	}
}
