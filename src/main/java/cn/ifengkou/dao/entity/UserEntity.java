package cn.ifengkou.dao.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 用户
 * @author shenlongguang<https://github.com/ifengkou>
 * @date: 2020/8/22
 */
@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
public class UserEntity extends BaseEntity {
    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "VARCHAR(50)")
    private String mobile;
    @Column(columnDefinition = "VARCHAR(150)")
    private String email;
}
