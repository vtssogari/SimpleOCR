# Simple OCR using tika, tesseract and pdfbox


```
		String pdf = "Non-text-searchable.pdf";
		Document doc = SimpleOCR.ocr(new File(pdf));
		System.out.println(doc.toPrettyJson());
```