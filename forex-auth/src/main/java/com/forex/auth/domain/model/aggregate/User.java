package com.forex.auth.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * User aggregate root. Represents a system user with authentication info, roles and permissions.
 * 认证用户聚合根，表示一个具备认证信息、角色和权限的系统用户。
 */
@Getter
public class User extends BaseAggregate {

    /** User unique identifier. 用户唯一标识。 */
    private Long id;
    /** Login username. 登录用户名。 */
    private String username;
    /** Encoded password. 加密后密码。 */
    private String password;
    /** Real name of the user. 用户真实姓名。 */
    private String realName;
    /** Email address. 邮箱地址。 */
    private String email;
    /** Phone number. 手机号码。 */
    private String phone;
    /** Account status: 1=active, 0=disabled. 账户状态：1=启用，0=禁用。 */
    private Integer status;
    /** Assigned roles. 已分配的角色集合。 */
    private Set<Role> roles;
    /** Derived permissions from roles. 从角色派生的权限集合。 */
    private Set<Permission> permissions;

    private User() {
        super();
    }

    /**
     * Factory method to create a new user account.
     * 工厂方法，创建新用户。
     */
    public static User create(String username, String encodedPassword, String realName, String email, String phone) {
        User user = new User();
        user.username = username;
        user.password = encodedPassword;
        user.realName = realName;
        user.email = email;
        user.phone = phone;
        user.status = 1;
        user.validate();
        return user;
    }

    /**
     * Factory to rebuild aggregate from persistence.
     * 从持久化重建聚合。
     */
    public static User reconstitute(Long id, String username, String encodedPassword,
                                     String realName, String email, String phone, Integer status) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = encodedPassword;
        user.realName = realName;
        user.email = email;
        user.phone = phone;
        user.status = status;
        return user;
    }

    /**
     * Assign roles and derive permissions from them.
     * 分配角色并从中派生权限集合。
     */
    public void assignRoles(Set<Role> roles) {
        this.roles = roles;
        this.permissions = roles.stream()
                .flatMap(r -> r.getPermissions().stream())
                .collect(java.util.stream.Collectors.toSet());
        markUpdated();
    }

    /**
     * Disable the user account. 禁用账户。
     */
    public void disable() {
        this.status = 0;
        markUpdated();
    }

    /**
     * Enable the user account. 启用账户。
     */
    public void enable() {
        this.status = 1;
        markUpdated();
    }

    /**
     * Check if the account is active. 检查账户是否可用。
     */
    public boolean isActive() {
        return this.status == 1;
    }

    /**
     * Update password with a new encoded value. 修改密码。
     */
    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }
}
