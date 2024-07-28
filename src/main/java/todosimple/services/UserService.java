package todosimple.services;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import todosimple.models.User;
import todosimple.models.enums.ProfileEnum;
import todosimple.repositories.UserRepository;
import todosimple.services.exceptions.DataBindingViolationException;
import todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    @Autowired
    private UserRepository userRepository;


    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
           "User not found! Id: " + id + ", Type: " + User.class.getName()
        ));
    }

    @Transactional
    public User create(User obj){
        obj.setId(null);
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);   
    }

    public User delete(Long id){
        User user = findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possivel excluir pois há entidades relacionadas");
        }
        return user;
    }
}
