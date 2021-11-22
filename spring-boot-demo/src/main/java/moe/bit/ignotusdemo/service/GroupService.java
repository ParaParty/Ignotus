package moe.bit.ignotusdemo.service;

import moe.bit.ignotusdemo.model.dto.CreateGroupDto;
import moe.bit.ignotusdemo.model.vo.GroupVo;

public interface GroupService {
    GroupVo createGroup(CreateGroupDto body);
}
