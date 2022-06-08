package mx.gob.sep.usicamm.reconocimientoproactividad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author hiryu
 */
@SpringBootApplication
@ComponentScan({"mx.gob.sep.usicamm.reconocimientoproactividad"})
public class PruebasConfig implements CommandLineRunner{
    public static void main(String[] args) {
        SpringApplication.run(PruebasConfig.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("- - INICIO DE TEST - -");
    }
}
