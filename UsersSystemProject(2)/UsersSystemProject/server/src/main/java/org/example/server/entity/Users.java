package org.example.server.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class Users implements Serializable,Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto_increment类型
    @TableId(value = "uid", type = IdType.AUTO)
    private Long uid; //用户编号，主键，不能重复
    private String username; //用户名
    private String password;  //密码
    private String gender;// 性别
    private String birthday; //出生日期
    private String major; // 专业
    private double score;
    private String banji;//班级

    // ========== 新增：角色字段（核心！区分管理员/普通用户） ==========
    // @TableField映射数据库users表的role字段，若表字段名和属性名一致可省略value
    @TableField("role")
    private String role; // 取值："admin"（管理员）、"normal"（普通用户）
}