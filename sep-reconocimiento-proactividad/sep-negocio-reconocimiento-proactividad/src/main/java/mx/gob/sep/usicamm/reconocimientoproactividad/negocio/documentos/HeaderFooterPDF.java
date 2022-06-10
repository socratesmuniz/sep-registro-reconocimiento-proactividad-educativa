package mx.gob.sep.usicamm.reconocimientoproactividad.negocio.documentos;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;

/**
 *
 * @author hiryu
 */
@Slf4j
public class HeaderFooterPDF extends PdfPageEventHelper{
    private String hash;
    private ConfiguracionAplicacion config;

    public HeaderFooterPDF(String hash, ConfiguracionAplicacion globalValue){
        this.hash=hash;
        this.config=globalValue;
    }
    
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle rect = writer.getBoxSize("header");
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        Image imgLogo;
        Image imgLogo2;
        Chunk cTmp;
        PdfContentByte canvas;
        PdfGState state = new PdfGState();
        
        //header
        try{
            imgLogo=Image.getInstance(HeaderFooterPDF.class.getResource("./logoSEP-USICAMM.png"));
            cTmp = new Chunk(imgLogo, 0, -45);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(cTmp), rect.getLeft(), rect.getTop()+30, 0);
        }
        catch(Exception ex){
            log.error("No se logro cargar el logo para los PDFs ("+HeaderFooterPDF.class.getResource("./logoSEP.png")+")", ex);
        }
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Proceso de reconocimiento a la Práctica Educativa", UtilsPDF.generaFuenteCabecera()), ((rect.getLeft()+rect.getRight())/2), rect.getTop()-20, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("implementada durante la Contingencia Sanitaria Ocasionada", UtilsPDF.generaFuenteCabecera()), ((rect.getLeft()+rect.getRight())/2), rect.getTop()-40, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("por el Virus SARS-CoV2 (COVID-19)", UtilsPDF.generaFuenteCabecera()), ((rect.getLeft()+rect.getRight())/2), rect.getTop()-60, 0);

        //marca de agua
        try{
            imgLogo=Image.getInstance(HeaderFooterPDF.class.getResource("./fondo.png"));
            imgLogo.setAbsolutePosition(rect.getLeft()-35, 10);
            imgLogo.scalePercent(126f);
            canvas=writer.getDirectContentUnder();
            canvas.saveState();
            state.setFillOpacity(0.5f);
            canvas.setGState(state);
            canvas.addImage(imgLogo);
            canvas.restoreState();
        }
        catch(Exception ex){
            this.log.error("No se logro cargar el fondo para los PDFs ("+HeaderFooterPDF.class.getResource("./fondo.png")+")", ex);
        }
        
        //pie
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(hash, UtilsPDF.generaFuentePie()), rect.getRight()+30, rect.getBottom()-20, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase("Fecha y hora de impresión "+sdf.format(new Date()), UtilsPDF.generaFuentePie()), rect.getRight()+30, rect.getBottom()-30, 0);
        //marca agua
        if(this.config.MARCA_AGUA!=null && this.config.MARCA_AGUA){
            ColumnText.showTextAligned(writer.getDirectContentUnder(), Element.ALIGN_CENTER, new Phrase(""+this.config.TEXTO_MARCA_AGUA, UtilsPDF.generaFuenteMarcaAgua()), ((rect.getLeft()+rect.getRight())/2), ((rect.getTop()+rect.getBottom())/2), 55);
        }
    }
}
