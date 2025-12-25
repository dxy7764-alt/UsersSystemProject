package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.example.server.entity.Users;

public interface UsersMapper extends BaseMapper<Users> {
    // 1. 统计全量总学生数
    @Select("SELECT COUNT(*) AS totalCount FROM users")
    Integer selectTotalCount();

    // 2. 统计全量男生数
    @Select("SELECT COUNT(*) AS maleCount FROM users WHERE gender = '男'")
    Integer selectMaleCount();

    // 3. 统计全量女生数
    @Select("SELECT COUNT(*) AS femaleCount FROM users WHERE gender = '女'")
    Integer selectFemaleCount();

    // 4. 统计全量平均成绩（IFNULL处理空值，避免返回null）
    @Select("SELECT IFNULL(AVG(score), 0) AS averageScore FROM users")
    Double selectAverageScore();

}

