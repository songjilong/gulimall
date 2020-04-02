package com.sjl.gulimall.member.dao;

import com.sjl.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-02 01:09:01
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
