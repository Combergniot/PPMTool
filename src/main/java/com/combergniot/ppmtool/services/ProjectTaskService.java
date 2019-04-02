package com.combergniot.ppmtool.services;

import com.combergniot.ppmtool.domain.Backlog;
import com.combergniot.ppmtool.domain.ProjectTask;
import com.combergniot.ppmtool.repositories.BacklogRepository;
import com.combergniot.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
//PTs to be added a specific project, project !=null, BL exists
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
        projectTask.setBacklog(backlog);
//        project sequnce should be like this: IDPRO-1, IDPRO-2, ...IDPRO-101
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
    }

    public Iterable<ProjectTask> findBacklogById(String id) {
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }
}
