package mx.gob.sep.usicamm.reconocimientoproactividad.registro.controles;

import javax.servlet.http.HttpServletRequest;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.utils.Constantes;
import mx.gob.sep.usicamm.reconocimientoproactividad.registro.util.ConstantesWeb;
import mx.gob.sep.usicamm.reconocimientoproactividad.registro.util.ValidacionAcceso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author albertosanchezlopez
 */
@Controller
public class PaginasCtrl {
    private final ValidacionAcceso loginService;

    @Autowired
    public PaginasCtrl(ValidacionAcceso loginService) {
        this.loginService = loginService;
    }
    
    @GetMapping("/")
    public String goDefault() {
        return ConstantesWeb.TEMPLATE_ADVENTENCIA;
    }

    @GetMapping("/inicio")
    public String goInicio(@RequestParam(defaultValue = Constantes.NONE) String curp, @RequestParam(defaultValue = Constantes.NONE) String token, Model model) {
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String browser =request.getHeader("User-Agent");
        String IP_server=request.getRemoteAddr();
        String host=request.getServerName();

        return this.loginService.autenticaUsuarioMesa(curp, token, model, IP_server, browser, host);
    }

    @GetMapping("/home")
    public String goHome() {
        return ConstantesWeb.TEMPLATE_HOME;
    }

    @GetMapping("/participacion")
    public String goParticipacion() {
        return ConstantesWeb.TEMPLATE_PARTICIPACION;
    }
    @GetMapping("/resumen")
    public String goResumen() {
        return ConstantesWeb.TEMPLATE_RESUMEN;
    }

    @GetMapping("/datos-personales")
    public String goDatosPersonales() {
        return ConstantesWeb.TEMPLATE_DATOS_PERSONALES;
    }

    @GetMapping("/registrado")
    public String goFinalizado() {
        return ConstantesWeb.TEMPLATE_FINALIZADO;
    }
}
