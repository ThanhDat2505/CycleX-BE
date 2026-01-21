package com.example.cyclexbe.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userid;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "full_name")
    private String fullname;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role")
    private String role;

    @Column(name = "is_verified")
    private boolean isverified;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status=UserStatus.ACTIVE;
    public enum UserStatus{
        ACTIVE,
        INACTIVE,
        BANNED,
        PENDING
    }
    @Column(name = "cccd")
    private String cccd;

    @Column(name = "avatar_url")
   private String avataurl;

    @Column(name = "created_at")
    private LocalDateTime createdat;

    @Column(name = "last_login")
    private LocalDateTime lastlogin;

    @Column(name = "update_at")
    private LocalDateTime updateat;

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    public List<bikeListing> listing;

    public User() {
    }

    public User(int userid, String email, String password, String fullname, String phone, String role, boolean isverified, UserStatus status, String cccd, String avataurl, LocalDateTime createdat, LocalDateTime lastlogin, LocalDateTime updateat) {
        this.userid = userid;
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.phone = phone;
        this.role = role;
        this.isverified = isverified;
        this.status = status;
        this.cccd = cccd;
        this.avataurl = avataurl;
        this.createdat = createdat;
        this.lastlogin = lastlogin;
        this.updateat = updateat;
    }

    public User(String email, String password, String fullname, String phone, String role, boolean isverified, UserStatus status, String cccd, String avataurl, LocalDateTime createdat, LocalDateTime lastlogin, LocalDateTime updateat) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.phone = phone;
        this.role = role;
        this.isverified = isverified;
        this.status = status;
        this.cccd = cccd;
        this.avataurl = avataurl;
        this.createdat = createdat;
        this.lastlogin = lastlogin;
        this.updateat = updateat;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isIsverified() {
        return isverified;
    }

    public void setIsverified(boolean isverified) {
        this.isverified = isverified;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getAvataurl() {
        return avataurl;
    }

    public void setAvataurl(String avataurl) {
        this.avataurl = avataurl;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public LocalDateTime getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(LocalDateTime lastlogin) {
        this.lastlogin = lastlogin;
    }

    public LocalDateTime getUpdateat() {
        return updateat;
    }

    public void setUpdateat(LocalDateTime updateat) {
        this.updateat = updateat;
    }
}
