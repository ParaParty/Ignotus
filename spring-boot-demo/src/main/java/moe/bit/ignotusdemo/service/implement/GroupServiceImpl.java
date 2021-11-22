package moe.bit.ignotusdemo.service.implement;

import moe.bit.ignotusdemo.model.dto.CreateGroupDto;
import moe.bit.ignotusdemo.model.entity.GroupEntity;
import moe.bit.ignotusdemo.model.vo.GroupVo;
import moe.bit.ignotusdemo.service.GroupService;
import com.tairitsu.ignotus.database.annotation.Transaction;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {
    @Override
    @Transaction
    public GroupVo createGroup(CreateGroupDto body) {
        GroupEntity group = GroupEntity.create(s -> {
            s.setName(body.getName());
        });
        group.flush(null);
        return new GroupVo(group);
    }
}
