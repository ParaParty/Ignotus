package moe.bit.ignotusdemo.controller;

import moe.bit.ignotusdemo.model.dto.CreateGroupDto;
import moe.bit.ignotusdemo.model.vo.GroupVo;
import moe.bit.ignotusdemo.service.GroupService;
import com.tairitsu.ignotus.foundation.annotation.JsonApiController;
import com.tairitsu.ignotus.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("api/groups")
    @JsonApiController()
    public GroupVo createGroup(@RequestBody @Valid CreateGroupDto body) {
        return groupService.createGroup(body);
    }

}
