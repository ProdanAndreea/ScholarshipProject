
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import org.junit.Test;


public class pdfTester {

    @Test
    public void testPdfCreation(){
        try{
            PdfWriter writer = new PdfWriter("test.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(20,20,20,20);
            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);

            Table table = new Table(new float[]{4, 2, 2, 2});
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(
                    new Cell().add(
                            new Paragraph("Data invoirii")
                    )
            );
            table.addHeaderCell(
                    new Cell().add(
                            new Paragraph("Nr de ore")
                    )
            );
            table.addHeaderCell(
                    new Cell().add(
                            new Paragraph("Ora plecarii")
                    )
            );
            table.addHeaderCell(
                    new Cell().add(
                            new Paragraph("Ora intoarcerii")
                    )
            );

            document.add(table);
            document.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }



    }
}
