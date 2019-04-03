package com.combergniot.ppmtool.services;

import com.combergniot.ppmtool.domain.Backlog;
import com.combergniot.ppmtool.domain.Project;
import com.combergniot.ppmtool.domain.ProjectTask;
import com.combergniot.ppmtool.exceptions.ProjectNotFoundException;
import com.combergniot.ppmtool.repositories.BacklogRepository;
import com.combergniot.ppmtool.repositories.ProjectRepository;
import com.combergniot.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        try {
//PTs to be added a specific project, project !=null, BL exists
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
            projectTask.setBacklog(backlog);
//        project sequence should be like this: IDPRO-1, IDPRO-2, ...IDPRO-101
            Integer backlogSequence = backlog.getPTSequence();
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);
//        Add sequence to Project Task
            projectTask.setProjectSequence(backlog.getProjectIdentifier() + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);
            //        Initial status when status null
            if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
                projectTask.setStatus("TO_DO");
            }
//        Initial priority when priority null
            if (projectTask.getPriority() == null) { //in the future we need projectTask.getPriority()==0 to handle the form
                projectTask.setPriority(3);
            }
            return projectTaskRepository.save(projectTask);
        } catch (Exception e) {
            throw new ProjectNotFoundException("Project not Found");
        }
    }

    public Iterable<ProjectTask> findBacklogById(String id) {
        Project project = projectRepository.findByProjectIdentifier(id);
        if (project == null) {
            throw new ProjectNotFoundException("Project with ID: " + id + " does not exist");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {
//        make sure we are searching on the right backlog
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if (backlog == null) {
            throw new ProjectNotFoundException("Project with ID: " + backlog_id + " does not exist");
        }
//        make sure that our task exist
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if (projectTask == null) {
            throw new ProjectNotFoundException("Project Task " + pt_id + " not found");
        }
//        make sure the backlog/project id in the path corresponds to the right project
        if (!projectTask.getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project Task " + pt_id + " does not exist in project: " + backlog_id);
        }
        return projectTask;
    }
}
