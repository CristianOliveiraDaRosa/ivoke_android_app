package com.app.ivoke.objects;

public class WebParameter
{
	private String key;
	private String valor;
	
	public WebParameter(String pKey, Object pValor)
	{
		setKey(pKey);
		setValor(pValor.toString());
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
