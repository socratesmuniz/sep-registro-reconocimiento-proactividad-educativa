package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.documentos;

import com.itextpdf.text.BadElementException;
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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.configuracion.ConfiguracionAplicacion;

/**
 *
 * @author hiryu
 */
@Slf4j
public class HeaderFooterPDF extends PdfPageEventHelper{
    private String hash;
    private ConfiguracionAplicacion config;
    private String entidad;

    public HeaderFooterPDF(String hash, ConfiguracionAplicacion globalValue, String entidad){
        this.hash=hash;
        this.config=globalValue;
        this.entidad=entidad;
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

            //intento recuperar el logo del subsistema            
            imgLogo2=this.getLogoEntidad();
            Chunk cTmp2 = new Chunk(imgLogo2, 0, -45);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(cTmp2), rect.getRight(), rect.getTop()+30, 0);
        }
        catch(Exception ex){
            log.error("No se logro cargar el logo para los PDFs ("+HeaderFooterPDF.class.getResource("./logoSEP.png")+")", ex);
        }
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Proceso de reconocimientoproactividadeducativa de asesoría, apoyo y,", UtilsPDF.generaFuenteCabecera()), ((rect.getLeft()+rect.getRight())/2), rect.getTop()-20, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("acompañamiento en educación básica, ciclo escolar 2022-2023", UtilsPDF.generaFuenteCabecera()), ((rect.getLeft()+rect.getRight())/2), rect.getTop()-40, 0);

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
    
    
    
    private Image getLogoEntidad() throws BadElementException, IOException{
        log.debug("Logos locales: "+this.config.LOGOS_LOCALES);
        if(this.config.LOGOS_LOCALES!=null && this.config.LOGOS_LOCALES){
            log.debug("Utiliza logos locales");
            try{
                return Image.getInstance(HeaderFooterPDF.class.getResource("./entidades/"+this.entidad+".png"));
            }
            catch(Exception ex){
                return Image.getInstance(HeaderFooterPDF.class.getResource("./sinLogo.png"));
            }
        }
        else{
            log.debug("Utiliza logos remotos");
            URL rutaLogo=this.getRutaLogoEntidad(this.entidad);
            
            if(rutaLogo!=null){
                return Image.getInstance(rutaLogo);
            }
            else{
                return Image.getInstance(HeaderFooterPDF.class.getResource("./sinLogo.png"));
            }
        }
    }
    
    private URL getRutaLogoEntidad(String entidad){
        URL ruta=null;
        HttpURLConnection con;
        
        if(this.config.RUTA_LOGOS!=null && entidad!=null){
            try{
                if(!this.config.RUTA_LOGOS.endsWith("/")){
                    ruta=new URL(this.config.RUTA_LOGOS+"/"+entidad.replaceAll(" ", "%20")+".png");
                }
                else{
                    ruta=new URL(this.config.RUTA_LOGOS+entidad.replaceAll(" ", "%20")+".png");
                }
                log.debug("URL del logo: "+ruta);
                
                //valido si existe el recurso
                con = (HttpURLConnection)ruta.openConnection();
                log.debug("Existe URL: "+con.getResponseCode());
                if( con.getResponseCode()!=200 ){
                    log.warn("Ruta del log no existe: "+ruta);
                    return null;
                }
            }
            catch(Exception ex){
                log.warn("Problema para recuperar el logo de la entidad "+entidad, ex);
            }
        }
        
        return ruta;
    }
}
