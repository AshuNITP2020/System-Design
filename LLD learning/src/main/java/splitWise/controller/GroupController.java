package splitWise.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import splitWise.dto.GroupDTO;
import splitWise.dto.UserDTO;
import splitWise.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping
    public GroupDTO createGroup(@RequestBody GroupDTO groupDTO) {
        return groupService.createGroup(groupDTO);
    }

    @GetMapping("/{id}")
    public GroupDTO getGroup(@PathVariable Long id) {
        return groupService.getGroup(id);
    }

    @GetMapping
    public List<GroupDTO> getAllGroups() {
        return groupService.getAllGroups();
    }

    @PutMapping("/{id}")
    public GroupDTO updateGroup(@PathVariable Long id, @RequestBody GroupDTO groupDTO) {
        return groupService.updateGroup(id, groupDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
    }

    @PostMapping("/{groupId}/addUser/{userId}")
    public GroupDTO addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.addUserToGroup(groupId, userId);
    }

    @PostMapping("/{groupId}/removeUser/{userId}")
    public GroupDTO removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.removeUserFromGroup(groupId, userId);
    }

    @GetMapping("/{groupId}/members")
    public List<UserDTO> getGroupMembers(@PathVariable Long groupId) {
        return groupService.getGroupMembers(groupId);
    }
}
