package mx.gob.sep.usicamm.reconocimientoproactividad.negocio.documentos;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.IOException;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;

/**
 *
 * @author hiryu
 */
public class UtilsPDF {
private UtilsPDF(){
    }
    
    public static void createHeader(PdfWriter writer, Document doc, String hash, ConfiguracionAplicacion config){
        HeaderFooterPDF event = new HeaderFooterPDF(hash, config);
        writer.setBoxSize("header", new Rectangle(36, 54, 559, 750));
        writer.setPageEvent(event);
    }

    public static Font generaFuenteCabecera(){
        Font f=new Font();
        
        f.setColor(new BaseColor(179, 142, 94));
        f.setSize(14);
        f.setStyle(Font.BOLD);
        
        return f;
    }

    public static Font generaFuentePie(){
        Font f=new Font();
        
        f.setColor(BaseColor.BLACK);
        f.setSize(7);
        f.setStyle(Font.BOLD);
        
        return f;
    }
    
    public static Font generaFuenteTituloPagina(){
        Font f=new Font();
        
        f.setColor(new BaseColor(0, 46, 41));
        f.setSize(16);
        f.setStyle(Font.BOLD);
        
        return f;
    }

    public static Font generaFuenteTituloTabla(){
        Font f=new Font();
        
        f.setColor(new BaseColor(179, 142, 94));
        f.setSize(12);
        f.setStyle(Font.BOLD);
        
        return f;
    }

    public static Font generaFuenteElemento(){
        Font f=new Font();
        
        f.setColor(BaseColor.BLACK);
        f.setSize(10);
        f.setStyle(Font.BOLD);
        
        return f;
    }

    public static Font generaFuenteValor(){
        Font f=new Font();
        
        f.setColor(BaseColor.BLACK);
        f.setSize(10);
        f.setStyle(Font.BOLD);
        
        return f;
    }

    public static Font generaFuenteCurso(){
        Font f=new Font();
        
        f.setColor(BaseColor.BLACK);
        f.setSize(8);
        
        return f;
    }

    public static Font generaFuenteTexto(){
        Font f=new Font();
        
        f.setColor(BaseColor.BLACK);
        f.setSize(11);
        f.setStyle(Font.NORMAL);
        
        return f;
    }

    public static Font generaFuenteNota3(){
        Font f=new Font();
        
        f.setColor(BaseColor.GREEN);
        f.setSize(11);
        f.setStyle(Font.NORMAL);
        
        return f;
    }

    public static Font generaFuenteNegrita(){
        Font f=generaFuenteTexto();
        
        f.setStyle(Font.BOLD);
        
        return f;
    }

    public static Font generaFuenteFirma(){
        Font f=new Font();
        
        f.setColor(BaseColor.BLACK);
        f.setSize(8);
        f.setStyle(Font.BOLD);
        
        return f;
    }

    public static Font generaFuenteNota(){
        Font f=new Font();
        
        f.setColor(new BaseColor(0, 46, 41));
        f.setSize(10);
        f.setStyle(Font.NORMAL);
        
        return f;
    }

    public static Font generaFuenteNotaNegrita(){
        Font f=generaFuenteNota();
        
        f.setStyle(Font.BOLD);
        
        return f;
    }

    public static Font generaFuenteNota2(){
        Font f=new Font();
        
        f.setColor(new BaseColor(98, 17, 50));
        f.setSize(10);
        f.setStyle(Font.NORMAL);
        
        return f;
    }

    public static Font generaFuenteMarcaAgua(){
        Font f=new Font();
        
        f.setColor(new BaseColor(200, 200, 200));
        f.setSize(120);
        f.setStyle(Font.BOLDITALIC);
        
        return f;
    }

    public static PdfPTable generaTablaElementos(){
        PdfPTable tabla=new PdfPTable(new float[]{40, 60});
        
        tabla.setWidthPercentage(70);
        tabla.setSpacingBefore(20);
        tabla.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        return tabla;
    }

    public static PdfPTable generaTablaElementos2(){
        PdfPTable tabla=new PdfPTable(new float[]{70, 30});
        
        tabla.setWidthPercentage(70);
        tabla.setSpacingBefore(20);
        tabla.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        return tabla;
    }

    public static PdfPTable generaTablaElementos3(){
        PdfPTable tabla=new PdfPTable(new float[]{50, 50});
        
        tabla.setWidthPercentage(90);
        tabla.setSpacingBefore(20);
        tabla.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        return tabla;
    }

    public static PdfPCell generaCeldaTitulo(String titulo) throws BadElementException, IOException {
        Paragraph pTmp;
        PdfPCell cell = new PdfPCell();
        Image iBullet1=Image.getInstance(HeaderFooterPDF.class.getResource("./bulletFlechaDorada.png"));

        pTmp=new Paragraph();
        pTmp.add(new Chunk(iBullet1, -5, 0));
        pTmp.add(new Chunk(titulo, UtilsPDF.generaFuenteTituloTabla()));
        cell.addElement(pTmp);
        
        cell.setPaddingLeft(20);
        cell.setBorder(PdfPCell.NO_BORDER);

        return cell;
    }

    public static PdfPCell generaCeldaElemento(String titulo) {
        PdfPCell cell = new PdfPCell(new Paragraph(titulo, generaFuenteElemento()));
        
        cell.setBorder(PdfPCell.NO_BORDER);

        return cell;
    }

    public static PdfPCell generaCeldaValor(String titulo) {
        PdfPCell cell = new PdfPCell(new Paragraph((titulo==null? "Sin datos proporcionados": titulo), generaFuenteValor()));
        
        cell.setBorder(PdfPCell.NO_BORDER);

        return cell;
    }

    public static PdfPCell generaCeldaFirmas(String titulo) {
        PdfPCell cell = new PdfPCell(new Paragraph(titulo, generaFuenteFirma()));
        
        cell.setBorder(PdfPCell.NO_BORDER);
        
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        return cell;
    }

    public static PdfPCell generaCeldaNota(String titulo) {
        Paragraph par=new Paragraph(titulo, generaFuenteNota());
        par.setAlignment(Element.ALIGN_JUSTIFIED);
        
        PdfPCell cell = new PdfPCell(par);
        
        cell.setBorder(PdfPCell.NO_BORDER);

        return cell;
    }

    public static PdfPCell generaCeldaNotaNegrita(String titulo) {
        Paragraph par=new Paragraph(titulo, generaFuenteNotaNegrita());
        par.setAlignment(Element.ALIGN_JUSTIFIED);
        
        PdfPCell cell = new PdfPCell(par);
        
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);

        return cell;
    }

    public static PdfPCell generaCeldaBulletNota2(String punto, String titulo) {
        Paragraph pTmp=new Paragraph("");
        pTmp.setFont(generaFuenteNota2());
        Font fTmp=generaFuenteNota2();
        fTmp.setStyle(Font.BOLD);
        
        pTmp.add(new Chunk(punto+")  ", fTmp));
        pTmp.add(titulo);
        
        PdfPCell cell = new PdfPCell(pTmp);
        
        cell.setBorder(PdfPCell.NO_BORDER);

        return cell;
    }

    public static PdfPCell generaCeldaNota2(String titulo) {
        PdfPCell cell = new PdfPCell(new Paragraph(titulo, generaFuenteNota2()));
        
        cell.setBorder(PdfPCell.NO_BORDER);

        return cell;
    }
    
    public static LineSeparator generaLineaTitulo(){
        LineSeparator linea = new LineSeparator();
        linea.setLineWidth(3);
        linea.setLineColor(new BaseColor(0, 46, 41));
        linea.setOffset(-20);
        
        return linea;
    }
    
    public static Chunk generaTextoTitulo(String texto){
        Chunk cTmp=new Chunk("   "+texto+"   ", UtilsPDF.generaFuenteTituloPagina());
        cTmp.setBackground(BaseColor.WHITE);
        
        return cTmp; 
   }
}

