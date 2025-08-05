package splitWise.dto;

import java.util.List;

public class GroupDTO {
    private Long id;
    private String groupId;
    private String name;
    private List<UserDTO> members;

    public GroupDTO() {}

    public GroupDTO(Long id, String groupId, String name, List<UserDTO> members) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.members = members;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<UserDTO> getMembers() { return members; }
    public void setMembers(List<UserDTO> members) { this.members = members; }
}
