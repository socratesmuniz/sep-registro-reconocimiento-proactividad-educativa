package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.documentos;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.entidades.DocenteDTO;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.entidades.ParticipacionDTO;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.DocentesService;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.RegistroParticipacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * 220876 - AT
 * 220877 - ATP
 * 220878 - T
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
    
    
    public void generaFicha(OutputStream flujo, String entidad, String curp) throws DocumentException, BadElementException, IOException{
        Document document = new Document(PageSize.LETTER, 40, 40, 110, 40);
        DocenteDTO datosDocente=null;
        ParticipacionDTO datosParticipacion=null;
        
        //recupero los datos necesarios
        try{
            datosDocente=this.docentesService.getDocente(curp);
            datosParticipacion=this.participacionService.recuperaParticipacion(datosDocente.getCveDocente());
        }
        catch(Exception ex){
            log.error("No se logro recuperar los datos para generar la ficha: "+ex.getMessage(), ex);
            throw new DocumentException("No se logro recuperar los datos para generar el documento");
        }

        PdfWriter writer = PdfWriter.getInstance(document, flujo);        
        UtilsPDF.createHeader(writer, document, datosParticipacion.getHuella(), entidad, this.config);
        document.open();
        
        this.generaDatosFicha(document, datosDocente, datosParticipacion);
        
        document.close();
    }
    
    
    private String getCadenaFecha(Date fecha){
        String[] meses=new String[]{"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio",
                "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        
        if(fecha==null){
            return "";
        }
        Calendar cTmp=Calendar.getInstance();
        cTmp.setTime(fecha);
        
        return (cTmp.get(Calendar.DAY_OF_MONTH)<10? "0"+cTmp.get(Calendar.DAY_OF_MONTH): cTmp.get(Calendar.DAY_OF_MONTH))+"/"+
                meses[cTmp.get(Calendar.MONTH)]+"/"+cTmp.get(Calendar.YEAR);
    }
    
    private String getCadenaHora(Date fecha){
        if(fecha==null){
            return "";
        }
        Calendar cTmp=Calendar.getInstance();
        cTmp.setTime(fecha);
        
        return (cTmp.get(Calendar.HOUR_OF_DAY)<10? "0"+cTmp.get(Calendar.HOUR_OF_DAY): cTmp.get(Calendar.HOUR_OF_DAY))+":"+
                (cTmp.get(Calendar.MINUTE)<10? "0"+cTmp.get(Calendar.MINUTE): cTmp.get(Calendar.MINUTE))+":"+
                (cTmp.get(Calendar.SECOND)<10? "0"+cTmp.get(Calendar.SECOND): cTmp.get(Calendar.SECOND));
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
        img1=Image.getInstance(HeaderFooterPDF.class.getResource("./personaRegistro3.png"));
        img1.scalePercent(60f);
        doc.add(new Chunk(img1, -200, -620));
        
        doc.add(UtilsPDF.generaLineaTitulo());        
        pTmp=new Paragraph(UtilsPDF.generaTextoTitulo("Ficha de registro"));
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
        tabla.addCell(UtilsPDF.generaCeldaElemento("Dirección:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosDocente.getDomicilio()));
        doc.add(tabla);
        
        tabla=UtilsPDF.generaTablaElementos();
        cell=UtilsPDF.generaCeldaTitulo("DATOS DEL PROCESO EN EL QUE PARTICIPAS");
        cell.setColspan(2);
        tabla.addCell(cell);
        tabla.addCell(UtilsPDF.generaCeldaElemento("Entidad federativa donde labora:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getEntidad()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Tipo educativo:"));
        tabla.addCell(UtilsPDF.generaCeldaValor("Básica"));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Sostenimiento:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getSostenimiento()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Nivel y/o servicio educativo:"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosParticipacion.getServicioEducativo()));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Año de la Convocatoria:"));
        tabla.addCell(UtilsPDF.generaCeldaValor("2022-2023"));
        tabla.addCell(UtilsPDF.generaCeldaValor(anios.isEmpty()? "Ninguno": anios));
        tabla.addCell(UtilsPDF.generaCeldaElemento("Consideraciones particulares"));
        tabla.addCell(UtilsPDF.generaCeldaValor(datosDocente.getConsideraciones()));
        doc.add(tabla);  
    }
}
