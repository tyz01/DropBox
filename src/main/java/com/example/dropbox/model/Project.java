package com.example.dropbox.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String projectDescription;

    private String cloudProvider;
    private String projectFolderLink;

    private String adminEmail;
    @ManyToMany
    private Set<User> administrators = new HashSet<>();

    public void addAdministrator(User user) {
        administrators.add(user);
    }
}
