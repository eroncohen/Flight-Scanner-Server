package smartspace.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ElementKey implements Serializable,Comparable<ElementKey>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String elementSmartspace;
	private String elementId;
	
	public ElementKey() {
		
	}
	
	public ElementKey(String elementSmartspace, String elementId) {
		this.elementSmartspace = elementSmartspace;
		this.elementId = elementId;
	}

	@Column(name = "elementSmartspace")
	public String getElementSmartspace() {
		return elementSmartspace;
	}

	public void setElementSmartspace(String elementSmartspace) {
		this.elementSmartspace = elementSmartspace;
	}

	@Column(name = "elementId")
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ElementKey))
			return false;
		ElementKey that = (ElementKey) o;
		return Objects.equals(getElementSmartspace(), that.getElementSmartspace())
				&& Objects.equals(getElementId(), that.getElementId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getElementSmartspace(), getElementId());
	}

	@Override
	public String toString() {
		return elementSmartspace + "@" + elementId;
	}

	@Override
	public int compareTo(ElementKey o) {
		return this.toString().compareTo(o.toString());
	}
}
