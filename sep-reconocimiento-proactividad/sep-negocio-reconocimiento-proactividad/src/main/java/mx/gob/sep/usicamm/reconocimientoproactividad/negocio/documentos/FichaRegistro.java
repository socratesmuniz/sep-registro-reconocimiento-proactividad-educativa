package mx.gob.sep.usicamm.reconocimientoproactividad.negocio.documentos;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.DocenteDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.ParticipacionDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.DocentesService;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.RegistroParticipacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author hiryu
 */
@Slf4j
@Component
public class FichaRegistro {
    private final ConfiguracionAplicacion config;
    private final DocentesService docentesService;
    private final RegistroParticipacionService participacionService;
    
    @Autowired
    public FichaRegistro(ConfiguracionAplicacion globalValue, DocentesService docentesService, RegistroParticipacionService participacionService){
        this.config=globalValue;
        this.docentesService=docentesService;
        this.participacionService=participacionService;
    }
    
    
    public void generaFicha(OutputStream flujo, String curp, int cveEntidad, int anioAplicacion) throws DocumentException, BadElementException, IOException{
        Document document = new Document(PageSize.LETTER, 40, 40, 110, 40);
        DocenteDTO datosDocente=null;
        ParticipacionDTO datosParticipacion=null;
        
        //recupero los datos necesarios
        try{
            datosDocente=this.docentesService.getDocente(curp);
            datosParticipacion=this.participacionService.recuperaParticipacion(datosDocente.getCveDocente(), cveEntidad, anioAplicacion);
        }
        catch(Exception ex){
            log.error("No se logro recuperar los datos para generar la ficha: "+ex.getMessage(), ex);
            throw new DocumentException("No se logro recuperar los datos para generar el documento");
        }

        PdfWriter writer = PdfWriter.getInstance(document, flujo);        
        UtilsPDF.createHeader(writer, document, this.participacionService.generaHuella(datosParticipacion), this.config);
        document.open();
        
        this.generaDatosFicha(document, datosDocente, datosParticipacion);
        
        document.close();
    }
    
    
    private void generaDatosFicha(Document doc, DocenteDTO datosDocente, ParticipacionDTO datosParticipacion) 
            throws DocumentException, BadElementException, IOException{
        PdfPTable tabla;
        Paragraph pTmp;
        PdfPCell cell;
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        String anios="";
        
        //imagenes
        Image img1=Image.getInstance(HeaderFooterPDF.class.getResource("./personaRegistro1.png"));
        img1.scalePercent(60f);
        doc.add(new Chunk(img1, 0, -180));
        img1=Image.getInstance(HeaderFooterPDF.class.getResource("./personaRegistro2.png"));
        img1.scalePercent(60f);
        doc.add(new Chunk(img1, -120, -365));
        /*img1=Image.getInstance(HeaderFooterPDF.class.getResource("./personaRegistro3.png"));
        img1.scalePercent(60f);
        doc.add(new Chunk(img1, -200, -620));*/
        
        doc.add(UtilsPDF.generaLineaTitulo());        
        pTmp=new Paragraph(UtilsPDF.generaTextoTitulo("Comprobante de participación"));
        pTmp.setAlignment(Element.ALIGN_CENTER);
        doc.add(pTmp);
        
        tabla=UtilsPDF.generaTablaElementos();
        cell=UtilsPDF.generaCeldaTitulo("DATOS DE LA MAESTRA O MAESTRO PARTICIPANTE");
        cell.setColspan(2);
        tabla.addCell(cell);
        tabla.addCell(UtilsPDF.generaCeldaElemento("CURP:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosDocente.getCurp()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Nombre:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosDocente.getNombre()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Primer apellido:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosDocente.getPrimerApellido()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Segundo apellido:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosDocente.getSegundoApellido()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Números telefónicos:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosDocente.getTelefono1()+(datosDocente.getTelefono2()!=null? " y "+datosDocente.getTelefono2(): "")));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Correos electrónicos:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosDocente.getCorreo1()+(datosDocente.getCorreo2()!=null? " y "+datosDocente.getCorreo2(): "")));
        doc.add(tabla);
        doc.add(new Paragraph(" \n \n"));
        
        tabla=UtilsPDF.generaTablaElementos();
        cell=UtilsPDF.generaCeldaTitulo("DATOS LABORALES");
        cell.setColspan(2);
        tabla.addCell(cell);
        tabla.addCell(UtilsPDF.generaCeldaElemento("Entidad federativa donde labora:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getEntidad()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Sostenimiento:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getSostenimiento()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Nivel y/o servicio educativo:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getServicioEducativo()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Año de la aplicación de la Práctica Educativa:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(""+datosParticipacion.getAnioAplicacion()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Clave de centro de trabajo de adscripción:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getCveCct()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Nombre del centro de trabajo de adscripción:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getCct()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Título de la Narrativa:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getNombreTrabajo()));
        doc.add(tabla);  
        doc.add(new Paragraph(" \n \n"));
        
        pTmp=new Paragraph("Estimada maestra o maestro, te solicitamos que te comuniques con tu Autoridad Educativa de la Entidad "
                + "Federativa, con la finalidad de entregar la narrativa de tu práctica educativa, con la que participarás antes "
                + "del 24 de junio 2022.", UtilsPDF.generaFuenteTexto());
        doc.add(pTmp);
    }
}
