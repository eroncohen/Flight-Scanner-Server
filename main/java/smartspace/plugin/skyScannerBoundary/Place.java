package smartspace.plugin.skyScannerBoundary;

public class Place {
	private int Id;
	private int ParentId;
	private String Code;
	private String Type;
	private String Name;
	
	public Place() {
	
	}

	public int getId() {
		return Id;
	}

	public Place(int id, int parentId, String code, String type, String name) {
		super();
		Id = id;
		ParentId = parentId;
		Code = code;
		Type = type;
		Name = name;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getParentId() {
		return ParentId;
	}

	public void setParentId(int parentId) {
		ParentId = parentId;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	
	@Override
	public String toString() {
		return "Place [Id=" + Id + ", ParentId=" + ParentId + ", Code=" + Code + ", Type=" + Type + ", Name=" + Name
				+ "]";
	}
}
