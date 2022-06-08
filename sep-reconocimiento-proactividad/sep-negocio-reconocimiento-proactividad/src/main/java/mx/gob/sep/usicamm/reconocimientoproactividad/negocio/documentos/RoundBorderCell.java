package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.documentos;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

/**
 *
 * @author hiryu
 */
public class RoundBorderCell implements PdfPCellEvent {

    @Override
    public void cellLayout(PdfPCell ppc, Rectangle rect, PdfContentByte[] canvas) {
        PdfContentByte cb = canvas[PdfPTable.BACKGROUNDCANVAS];
        
        cb.roundRectangle(rect.getLeft()+1.5f, rect.getBottom()+1.5f, rect.getWidth()-3, rect.getHeight()-3, 4);
        cb.stroke();
    }
    
}
