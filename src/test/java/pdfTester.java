
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.junit.Test;

import java.awt.*;


public class pdfTester {

    @Test
    public void testPdfCreation(){
        try{
            PdfWriter writer = new PdfWriter("test.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(20,20,20,20);
            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);

            document.add(
                    new Paragraph("SIEMENS SRL")
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(22)
                            .setBold()
                            .setFontColor(new DeviceRgb(0,153,153))
            );
            document.add(
                    new Paragraph("Bilet Invoire")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(20)
                            .setBold()
            );

            Table table = new Table(new float[]{2, 2, 2, 2});
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
