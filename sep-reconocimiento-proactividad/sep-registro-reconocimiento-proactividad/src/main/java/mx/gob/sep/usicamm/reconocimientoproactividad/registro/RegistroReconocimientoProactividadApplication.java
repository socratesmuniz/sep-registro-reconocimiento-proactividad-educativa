package mx.gob.sep.usicamm.reconocimientoproactividad.registro;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author hiryu
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan({"mx.gob.sep.usicamm.reconocimientoproactividad"})
public class RegistroReconocimientoProactividadApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistroReconocimientoProactividadApplication.class);

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(RegistroReconocimientoProactividadApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("- - INICIO REGISTRO-RECONOCIMIENTO-PROACTIVIDAD-ACADEMICA COMPLETA - -");
        LOGGER.info("Connection Polling datasource: "+this.dataSource);  
    }
}
