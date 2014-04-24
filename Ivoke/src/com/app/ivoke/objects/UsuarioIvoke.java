package com.app.ivoke.objects;

import java.io.Serializable;

import com.facebook.model.GraphPlace;
import com.google.android.gms.maps.model.LatLng;

public class UsuarioIvoke implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int    ivokeID;
	private String facebookID;
	private String nome;
	private double localLatitude;
	private double localLongitude;
	
	private String facebookPlaceId;
	private String facebookPlaceName;
	private double facebookPlaceLatitude;
	private double facebookPlaceLongitude;
	
	public String getFacebookID() {
		return facebookID;
	}
	public void setFacebookID(String facebookId) {
		this.facebookID = facebookId;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public LatLng getLocalizacao() {
		return new LatLng(localLatitude, localLongitude);
	}
	public void setLocalizacao(LatLng localizacao) {
		localLatitude  = localizacao.latitude;
		localLongitude = localizacao.longitude;
	}
	public int getIvokeID() {
		return ivokeID;
	}
	public void setIvokeID(int ivokeID) {
		this.ivokeID = ivokeID;
	}
	public String getLocalCheckingId() {
		return facebookPlaceId;
	}
	
	public String getLocalCheckingName()
	{
		return facebookPlaceName;
	}
	
	public LatLng getFacebookPlaceLatLng()
	{
		return new LatLng(facebookPlaceLatitude, facebookPlaceLongitude);
	}
	
	public void setLocalChecking(GraphPlace localChecking) {
		
		facebookPlaceName = localChecking.getName();
		facebookPlaceId   = localChecking.getId();
		facebookPlaceLatitude  = localChecking.getLocation().getLatitude();
		facebookPlaceLongitude = localChecking.getLocation().getLongitude();
	}
	

}
