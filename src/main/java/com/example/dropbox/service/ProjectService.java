package com.example.dropbox.service;

import com.example.dropbox.google.GoogleDriveService;
import com.example.dropbox.model.*;
import com.example.dropbox.repository.ProjectRepository;
import com.example.dropbox.repository.RoleRepository;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProjectService {

    GoogleDriveService googleDriveService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public Project createProject(String projectName, String projectDescription, String cloudProvider, String projectFolderLink) {
        Project project = new Project();
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        project.setCloudProvider(cloudProvider);
        project.setProjectFolderLink(projectFolderLink);

        UserDetails userDetails = getCurrentUser();
        if (userDetails != null && userDetails instanceof User) {
            User currentUser = (User) userDetails;
        }

        return projectRepository.save(project);
    }

    public Project getProjectById(Long projectId) throws NotFoundException {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }
    public void addAdministratorToProjectAndFolder(Long projectId, String folderId) throws NotFoundException {
        Project project = getProjectById(projectId);
        User currentUser = (User) getCurrentUser();

        Role projectAdminRole = new Role(currentUser, project, RoleType.ADMINISTRATOR);
        roleRepository.save(projectAdminRole);
        googleDriveService.addFolderPermission(folderId, currentUser.getEmail(), "writer");
    }

    private UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                return (UserDetails) authentication.getPrincipal();
            }
        }
        return null;
    }

    public void addAdministratorToProject(Long projectId, User administrator) throws NotFoundException {
        Project project = getProjectById(projectId);
        project.addAdministrator(administrator);
        projectRepository.save(project);
    }

}
