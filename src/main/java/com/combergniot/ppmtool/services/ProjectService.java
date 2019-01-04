package com.combergniot.ppmtool.services;

import com.combergniot.ppmtool.domain.Project;
import com.combergniot.ppmtool.exceptions.ProjectIdException;
import com.combergniot.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdateProject(Project project){

        try {
            project.setProjectIdentifer(project.getProjectIdentifer().toUpperCase());
            return projectRepository.save(project);
        } catch (Exception e) {
           throw new ProjectIdException("Project ID '" + project.getProjectIdentifer()+"' already exists");
        }
    }
}
