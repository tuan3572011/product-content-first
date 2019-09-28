package com.product.content.controller;

import com.product.content.model.Category;
import com.product.content.model.Image;
import com.product.content.model.Project;
import com.product.content.repository.ProjectRepository;
import com.product.content.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admin/project")
public class ProjectResource {

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private ImageService imageService;

	@PostConstruct
    private void init(){
		List<Image> images = this.imageService.getAll();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2007);
		Random rd = new Random();
		for(int i = 0; i< 10; i++){
			calendar.set(Calendar.MONTH, 1 + i);
			Project project = new Project();
			project.setProductName(i + " London Based Project");
			project.setDescription(i + " Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincid unt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet.Lorem ipsum dolor sit amet, consectetur adipisicing elit, " +
					" <br> Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa");
			project.setCustomerName(i + " Company Name");
			project.setLocation(i + " Egypt, Tanta");
			project.setFromDate(calendar.getTime());
			project.setToDate(calendar.getTime());
			if(!images.isEmpty()){
				project.setThumbnail(images.get(rd.nextInt(images.size())).getName());
				project.setImages(Arrays.asList(project.getThumbnail(), images.get(rd.nextInt(images.size())).getName(), images.get(rd.nextInt(images.size())).getName()));
			}
			project.setNewest(rd.nextBoolean());
			project.setCategory(Category.values()[rd.nextInt(3)].name());
			this.createProject(project);
		}

    }

	@GetMapping("all")
	public ResponseEntity<List<Project>> getAll(){
		return new ResponseEntity<List<Project>>(projectRepository.findAll(), HttpStatus.OK);
	}

	@GetMapping("newest")
	public ResponseEntity<List<Project>> getNewest(){
		List<Project> projects = projectRepository.findAll().stream()
				.filter(Project::isNewest)
				.collect(Collectors.toList());
		Collections.sort(projects, Comparator.comparing(Project::getToDate));
		return new ResponseEntity<List<Project>>(projects.stream().limit(3).collect(Collectors.toList()), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Project> getById(@RequestParam("projectId") Long id){
		return new ResponseEntity<Project>(projectRepository.getOne(id), HttpStatus.OK);
	}
	

	@PostMapping
	public ResponseEntity<Project> createProject(@RequestBody Project project) {
		return new ResponseEntity<>(projectRepository.save(project), HttpStatus.CREATED);
	}
	
	@PutMapping()
	public ResponseEntity<Project> update(@RequestBody Project project) {
		return new ResponseEntity<>(projectRepository.save(project), HttpStatus.OK);
	}
	
	@DeleteMapping()
	@CrossOrigin
	public ResponseEntity<Void> delete(@RequestParam("projectId") Long id) {
		projectRepository.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
