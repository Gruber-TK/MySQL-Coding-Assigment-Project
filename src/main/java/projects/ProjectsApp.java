package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project currentProject;

	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a Project",
			"2) List Projects",
			"3) Select a Project",
			"4) Update Project details",
			"5) Delete a Project"
			);
	// @formatter:on

	public static void main(String[] args) {

		new ProjectsApp().processUserSelections();

	}

	private void processUserSelections() {

		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();

				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
					break;
				case 5: 
					deleteProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid option. Please resubmit your selection.");
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}

	}

	private void deleteProject() {
		listProjects();
		Integer projectId = getIntInput("Enter the Project ID of the Project to delete");
		
		if (Objects.nonNull(projectId)) {
			projectService.deleteProject(projectId);
			
			System.out.println("You have deleted project " + projectId);
			
			if(Objects.nonNull(currentProject) && currentProject.getProjectId().equals(projectId)) {
				currentProject = null;
			}
		}
	}

	private void updateProjectDetails() {
		if (Objects.isNull(currentProject)) {
			System.out.println("\nPlease Select a Project");
			return;
		}

		String projectName = getStringInput("Enter the Project Name [" + currentProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput("Enter the Estimated Hours [" + currentProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the Actual Hours [" + currentProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter the Project Difficulty (1-5) [" + currentProject.getDifficulty() + "]");
		String notes = getStringInput("Enter the Project Notes [" + currentProject.getNotes() + "]");

		Project project = new Project();
		project.setProjectName(Objects.isNull(projectName) ? currentProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? currentProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? currentProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? currentProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? currentProject.getNotes() : notes);
		project.setProjectId(currentProject.getProjectId());
		
		projectService.modifyProjectDetails(project);
		currentProject = projectService.fetchProjectById(currentProject.getProjectId());
	}

	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a Project ID to select a Project: ");

		currentProject = null;

		currentProject = projectService.fetchProjectById(projectId);

	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects: ");

		projects.forEach(
				project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));

	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number");
		}
	}

	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a Menu Selection");

		return Objects.isNull(input) ? -1 : input;
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();
	}

	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the ENTER key to quit:");

		operations.forEach(line -> System.out.println("  " + line));

		if (Objects.isNull(currentProject)) {
			System.out.println("You are not currently working with a project.");
		} else {
			System.out.println("You are currently working with project: " + currentProject);
		}
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu. Goodbye!");
		return true;
	}

}
