package org.apache.uima.aae.message;

import java.util.UUID;

import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;

public class UimaAsOrigin implements Origin {

	private final String uniqueId = UUID.randomUUID().toString();
	private final String name;
	private final EndpointType type;
	
	public UimaAsOrigin(String name, EndpointType type) {
		this.name = name;
		this.type = type;
	}
	public UimaAsOrigin(String name) {
		this(name,EndpointType.NA);
	}

	@Override
	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UimaAsOrigin other = (UimaAsOrigin) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uniqueId == null) {
			if (other.uniqueId != null)
				return false;
		} else if (!uniqueId.equals(other.uniqueId))
			return false;
		return true;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
    public String toString() {
        return "Origin[name: " + name + "] [id:"+uniqueId+"]";
    }

	@Override
	public EndpointType getType() {
		return type;
	}
}
