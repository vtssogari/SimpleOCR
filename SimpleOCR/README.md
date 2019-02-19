# Simple OCR using tika, tesseract and pdfbox

## Command Line

```
Usage: SimpleOCR [-tfhrV] FILE...
      FILE...     one ore more files/dir to process
  -f              Force OCR processing
  -h, --help      Show this help message and exit.
  -r              recursive processing sub directory
  -t, --max-char=<minimumThresholdCharSize>
                  If PDF file, then threshold character size of each pages to start
                    OCR or not. Overriden by -o
  -V, --version   Print version information and exit.
```

## API

```
		String pdf = "Non-text-searchable.pdf";
		SimpleOCR instance = new SimpleOCR();
		Document doc = instance.ocr(new File(pdf));
		System.out.println(doc.toPrettyJson());
```