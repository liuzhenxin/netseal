package cn.com.infosec.netseal.webserver.util;

import java.util.Map;

public class TreeNode {
	private String id;
	private String name;
	private boolean isParent;
	private boolean checked;
	private String uri;
	private String title;
	private Map<String, String> fontCss;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public boolean getChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getTitle() {
		if (title == null) {
			return name;
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getFontCss() {
		return fontCss;
	}

	public void setFontCss(Map<String, String> fontCss) {
		this.fontCss = fontCss;
	}

}
