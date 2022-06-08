package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.restservices.objetos;

import java.time.LocalDateTime;

/**
 *
 * @author albertosanchezlopez
 */
public class RespuestaRest {
    public static final int CODIGO_OK=0;
    public static final int CODIGO_ERROR=1;
    
    private Object response;
    private String msg;
    private String detail;
    private int code;
    private LocalDateTime salt;
    
    public RespuestaRest() {
        this.salt = LocalDateTime.now();
        this.code=CODIGO_OK;
    }

    public RespuestaRest(int code, String msg) {
        this();
        this.code = code;
        this.msg = msg;
    }

    public RespuestaRest(int code, String msg, Object response) {
        this(code, msg);
        this.response = response;
    }
    
    public void setResponse(Object response){
        this.response=response;
    }
    
    public void setCode(int code){
        this.code=code;
    }
    
    public void setMsg(String msg){
        this.msg=msg;
    }
    
    public void setDetail(String msg){
        this.detail=msg;
    }
    
    public LocalDateTime getSalt(){
        return this.salt;
    }
    
    public Object getResponse(){
        return this.response;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public String getMsg(){
        return this.msg;
    }
    
    public String getDetail(){
        return this.detail;
    }
}
