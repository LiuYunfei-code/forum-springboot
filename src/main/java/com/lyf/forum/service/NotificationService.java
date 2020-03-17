package com.lyf.forum.service;

import com.lyf.forum.dto.NotificationDTO;
import com.lyf.forum.dto.PaginationDTO;
import com.lyf.forum.enums.NotificationStatusEnum;
import com.lyf.forum.enums.NotificationTypeEnum;
import com.lyf.forum.mapper.NotificationMapper;
import com.lyf.forum.mapper.UserMapper;
import com.lyf.forum.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    public PaginationDTO<NotificationDTO> list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        // 查询当前 userId 的记录数
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);

        // 没有相关记录直接返回
        if (totalCount == 0)
            return paginationDTO;
        // 设置分页信息
        paginationDTO.setPagination(totalCount, page, size);

        if (page < 1) {
            page = 1;
        } else if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page - 1);

        notificationExample.setOrderByClause("gmt_create desc");
        List<Notification> notificationList = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));
        if (notificationList.size()==0)
            return paginationDTO;

        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        for (Notification notification : notificationList) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setType(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOList.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOList);
        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public Notification read(Long id) {

        Notification notification = new Notification();
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andIdEqualTo(id);
        notificationMapper.updateByExampleSelective(notification, notificationExample);
        return notificationMapper.selectByExample(notificationExample).get(0);
    }
}
