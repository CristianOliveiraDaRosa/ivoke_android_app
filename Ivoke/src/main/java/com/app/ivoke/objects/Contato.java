package com.app.ivoke.objects;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Contato {

    String usuario;
    String ultimaMensagem;
    Date   dataHora;
    int    mensagensNaoLidas;

    public Contato(String pUsuario,String pUltimaMensagem, int mensagens, Date date)
    {
        usuario           = pUsuario;
        mensagensNaoLidas = mensagens;
        ultimaMensagem    = pUltimaMensagem;
        dataHora = date;
    }

    public String getUsuario()
    {
        return usuario;
    }

    public int getMensagensNaoLidas()
    {
        return mensagensNaoLidas;
    }

    public String getUltimaMensagem()
    {
        return ultimaMensagem;
    }

    @SuppressLint("SimpleDateFormat")
    public String getQuando()
    {
        return new SimpleDateFormat("H:mm").format(dataHora);
    }

}
