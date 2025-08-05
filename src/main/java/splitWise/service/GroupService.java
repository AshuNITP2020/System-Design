package splitWise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import splitWise.dto.GroupDTO;
import splitWise.dto.UserDTO;
import splitWise.entity.Group;
import splitWise.entity.User;
import splitWise.repository.GroupRepository;
import splitWise.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    public GroupDTO createGroup(GroupDTO groupDTO) {
        Group group = new Group();
        group.setGroupId(groupDTO.getGroupId());
        group.setName(groupDTO.getName());
        group = groupRepository.save(group);
        return toDTO(group);
    }

    public GroupDTO getGroup(Long id) {
        return groupRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public List<GroupDTO> getAllGroups() {
        return groupRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public GroupDTO updateGroup(Long id, GroupDTO groupDTO) {
        Optional<Group> groupOpt = groupRepository.findById(id);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            group.setName(groupDTO.getName());
            group = groupRepository.save(group);
            return toDTO(group);
        }
        return null;
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    public GroupDTO addUserToGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (group != null && user != null) {
            group.getMembers().add(user);
            group = groupRepository.save(group);
            return toDTO(group);
        }
        return null;
    }

    public GroupDTO removeUserFromGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (group != null && user != null) {
            group.getMembers().remove(user);
            group = groupRepository.save(group);
            return toDTO(group);
        }
        return null;
    }

    public List<UserDTO> getGroupMembers(Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) return new ArrayList<>();
        return group.getMembers().stream().map(this::toUserDTO).collect(Collectors.toList());
    }

    private GroupDTO toDTO(Group group) {
        GroupDTO dto = new GroupDTO();
        dto.setId(group.getId());
        dto.setGroupId(group.getGroupId());
        dto.setName(group.getName());
        dto.setMembers(group.getMembers().stream().map(this::toUserDTO).collect(Collectors.toList()));
        return dto;
    }

    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}

