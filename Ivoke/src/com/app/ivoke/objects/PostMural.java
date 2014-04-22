package com.app.ivoke.objects;

public class PostMural{
    String usuario;
	String mensagem;
	String quando;
    int iconeID;
    
    public PostMural(String pUsuarioNome,String pMensagem, String pQuando ,int pIconeID)
    {
    	usuario  = pUsuarioNome;
    	mensagem = pMensagem;
    	quando   = pQuando;
    	iconeID  = pIconeID;
    }
    
    public String getNome()
    {
    	return usuario;
    }
    
    public String getMensagem()
    {
    	return mensagem;
    }
    
    public String getQuando()
    {
    	return quando;
    }
    
    public int getIconeId()
    {
    	return iconeID;
    }
}

