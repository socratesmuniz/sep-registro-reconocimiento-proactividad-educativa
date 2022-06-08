package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.pruebas;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.documentos.FichaRegistro;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author hiryu
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class PruebaFichas {
    @Autowired
    private FichaRegistro fichaRegistro;

    @Test
    public void generaFichaRegistro() throws Exception {
        FileOutputStream out=new FileOutputStream("ficha.pdf");

        fichaRegistro.generaFicha(out, "Durango", "AAAA790609MCHNRL06");
    }
}
