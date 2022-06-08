package mx.gob.sep.usicamm.reconocimientoproactividad.pruebas;

import java.util.List;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.CatalogoDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.CatalogosService;
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
public class PruebaCatalogos {
    private static final int EDO_MEX=15;
    private static final int DOCENTE=1;
    
    @Autowired
    private CatalogosService service;

    @Test
    public void recuperaEntidades() throws Exception{
        List<CatalogoDTO> res = this.service.getEntidades();
        
        assert(res!=null && res.size()>0);
    }
}
