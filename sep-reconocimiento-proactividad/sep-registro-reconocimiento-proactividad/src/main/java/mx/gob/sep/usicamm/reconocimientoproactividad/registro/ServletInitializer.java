package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.registro;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 *
 * @author albertosanchezlopez
 */
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RegistroReconocimientoProactividadAcademicaApplication.class);
    }

}
