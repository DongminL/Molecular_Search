package com.example.molecularsearch.jwt;

import com.example.molecularsearch.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Users users;

    public CustomUserDetails(Users users) {
        this.users = users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(users.getRoleType()));

        return authorities;
    }

    @Override
    public String getUsername() {
        return users.getUserId();
    }

    /* 비밀번호는 없기 때문에 아무런 문자 반환 안함 */
    @Override
    public String getPassword() {
        return "";
    }

    /* 계정 만료 확인 */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /* 계정 잠금 확인 */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /* 비밀번호가 없어서 사용 X */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /* 계정 활성화 확인 */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
