package todosimple.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;
import todosimple.models.enums.ProfileEnum;

@NoArgsConstructor
@Getter
public class UserSpringSecurity implements UserDetails{
    
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;


    public UserSpringSecurity(Long id, String username, String password, Set<ProfileEnum> profileEnum) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = profileEnum.stream().map(e -> new SimpleGrantedAuthority(e.getDescription())).collect(Collectors.toList());
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;    
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public boolean hasRole(ProfileEnum profileEnum){
        return getAuthorities().contains(new SimpleGrantedAuthority(profileEnum.getDescription()));
    }
}
