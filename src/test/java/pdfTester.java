
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.listener.TextChunk;
import com.itextpdf.layout.Document;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import org.junit.Test;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class pdfTester {

    @Test
    public void testPdfCreation(){
        try{
            PdfWriter writer = new PdfWriter("test.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
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
            document.add(
                    new Paragraph(
                            "Subsemnatul/a _____ rog a beneficia de o invoire avand durata de _ ore, " +
                                    "perioada necesara pentru rezolvarea unor probleme cu caracter personal."

                    )
                            .setFirstLineIndent(40)
                            .setFontSize(14)
            );
            document.add(
                    new Paragraph("Invoire:")
                    .setBold()
                    .setFontSize(16)
                    .setFirstLineIndent(40)
            );

            Table table = new Table(new float[]{2, 2});
            table.setWidth(UnitValue.createPercentValue(80));

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

            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(table);

            document.add(
                    new Paragraph("Propuneri recuperare:")
                    .setBold()
                    .setFontSize(16)
                    .setFirstLineIndent(40)
            );

            Table recoveryTable = new Table(new float[]{2,2,2});
            recoveryTable.setWidth(UnitValue.createPercentValue(80));

            recoveryTable.addHeaderCell(
                    new Cell().add(
                            new Paragraph("Recuperare pentru data de")
                    )
            );
            recoveryTable.addHeaderCell(
                    new Cell().add(
                            new Paragraph("Data propusa pentru recuperare")
                    )
            );
            recoveryTable.addHeaderCell(
                    new Cell().add(
                            new Paragraph("Nr. ore recuperate")
                    )
            );

            recoveryTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(recoveryTable);


            Table approvalTable = new Table(new float[]{1,2});
            approvalTable.addCell(
                    new Cell(1,2 ).add(new Paragraph("  Aprobare"))
            );
            approvalTable.addCell("Sef");
            approvalTable.addCell("Semnatura");
            approvalTable.addCell(
                    new Cell()
                    .add(new Paragraph("Direct:"))
                    .add(new Paragraph("Nume sef direct"))
            );
            approvalTable.addCell("");
            approvalTable.addCell(
                    new Cell()
                    .add(new Paragraph("Departament:"))
                    .add(new Paragraph("Nume sef depatament"))
            );
            approvalTable.addCell("");


            document.add(new Paragraph("\n\n"));
            document.add(
                    new Paragraph(
                            "Data de azi: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    )
                    .setVerticalAlignment(VerticalAlignment.BOTTOM)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT)
                    .setFontSize(12)
            );

            document.add(
                    new Paragraph("Semnatura angajat: ")
                    .setVerticalAlignment(VerticalAlignment.BOTTOM)
                    .setHorizontalAlignment(HorizontalAlignment.LEFT)
                    .setFontSize(12)
            );

            document.add(new Paragraph("\n\n"));

            approvalTable.setWidth(UnitValue.createPercentValue(60));
            approvalTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(approvalTable);

            document.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }



    }
}
