package ma.hps.powercard.compliance.serviceimpl.spec;

import java.util.Collection;

import ma.hps.powercard.compliance.serviceapi.MenuVO;


public class Module
{
	private String name;
	private Collection<MenuVO> menus;
	
	public Module() {
	}

	public Module(String name, Collection<MenuVO> menus) {
		this.name  = name;
		this.menus = menus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<MenuVO> getMenus() {
		return menus;
	}

	public void setMenus(Collection<MenuVO> menus) {
		this.menus = menus;
	}
}