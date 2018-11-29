package org.apache.uima.aae.message;

import java.util.UUID;

import org.apache.uima.aae.definition.connectors.UimaAsEndpoint.EndpointType;

public class UimaAsTarget implements Target {
	private final String uniqueId = UUID.randomUUID().toString();
	private final String name;
	private final EndpointType type;



	public UimaAsTarget(String name, EndpointType type) {
		this.name = name;
		this.type = type;
	}
	@Override
	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public EndpointType getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		UimaAsTarget other = (UimaAsTarget) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (uniqueId == null) {
			if (other.uniqueId != null)
				return false;
		} else if (!uniqueId.equals(other.uniqueId))
			return false;
		return true;
	}
}
