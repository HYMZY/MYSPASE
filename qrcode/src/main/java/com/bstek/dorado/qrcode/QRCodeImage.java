package com.bstek.dorado.qrcode;

import com.bstek.dorado.annotation.ClientObject;
import com.bstek.dorado.annotation.ClientProperty;
import com.bstek.dorado.annotation.IdeProperty;
import com.bstek.dorado.annotation.XmlNode;
import com.bstek.dorado.annotation.XmlProperty;
import com.bstek.dorado.view.annotation.Widget;
import com.bstek.dorado.view.widget.Control;
import com.bstek.dorado.common.ClientType;

@XmlNode(clientTypes = { ClientType.DESKTOP, ClientType.TOUCH })
@Widget(name = "QRCodeImage", category = "Advance", dependsPackage = "qrcode-image")
@ClientObject(prototype = "dorado.widget.QRCodeImage", shortTypeName = "QRCodeImage")
public class QRCodeImage extends Control {
	private String type = "Text";
	private String errorCorrectionLevel="L";
	private String outputFomart="PNG";
	private String characterSet="utf8";
	private String data;
	@Deprecated
	private int sideLength =350;
	private boolean validatorsDisabled =false;
	
	private String color = "#000000";
	private String backgroundColor = "#FFFFFF";
	private int quietZoneSize=4;
	private String logo;
	private int renderStyle;
	private String blankImage ="$url('>dorado/client/resources/qrcode-wt.gif')";
	
	@ClientProperty(escapeValue = "DEFAULT_LOGO")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getRenderStyle() {
		return renderStyle;
	}

	public void setRenderStyle(int renderStyle) {
		this.renderStyle = renderStyle;
	}

	@ClientProperty(escapeValue = "#000000")
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@ClientProperty(escapeValue = "#FFFFFF")
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	
	@ClientProperty(escapeValue = "4")
	public int getQuietZoneSize() {
		return quietZoneSize;
	}

	public void setQuietZoneSize(int quietZoneSize) {
		this.quietZoneSize = quietZoneSize;
	}

	@ClientProperty(escapeValue = "Text")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@ClientProperty(escapeValue = "L")
	public String getErrorCorrectionLevel() {
		return errorCorrectionLevel;
	}

	public void setErrorCorrectionLevel(String errorCorrectionLevel) {
		this.errorCorrectionLevel = errorCorrectionLevel;
	}

	@ClientProperty(escapeValue = "PNG")
	public String getOutputFomart() {
		return outputFomart;
	}

	public void setOutputFomart(String outputFomart) {
		this.outputFomart = outputFomart;
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}


	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	@XmlProperty(deprecated = true)
	@ClientProperty(escapeValue = "350")
	public int getSideLength() {
		return sideLength;
	}
	
	public void setSideLength(int sideLength) {
		this.sideLength = sideLength;
	}
	
	@ClientProperty(escapeValue = "false")
	public boolean isValidatorsDisabled() {
		return validatorsDisabled;
	}

	public void setValidatorsDisabled(boolean validatorsDisabled) {
		this.validatorsDisabled = validatorsDisabled;
	}

	@ClientProperty(escapeValue = "$url('>dorado/client/resources/qrcode-wt.gif')")
	public String getBlankImage() {
		return blankImage;
	}
	
	public void setBlankImage(String blankImage) {
		this.blankImage = blankImage;
	}

}
