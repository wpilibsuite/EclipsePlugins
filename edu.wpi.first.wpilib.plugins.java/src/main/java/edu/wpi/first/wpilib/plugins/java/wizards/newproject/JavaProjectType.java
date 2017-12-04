package edu.wpi.first.wpilib.plugins.java.wizards.newproject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilib.plugins.core.wizards.ProjectType;
import edu.wpi.first.wpilib.plugins.java.WPILibJavaPlugin;

public class JavaProjectType implements ProjectType {
	static ProjectType SAMPLE = new JavaProjectType() {
		@Override public Map<String, String> getFiles(String packageName) {
			Map<String, String> files = super.getFiles(packageName);
			files.put("src/"+packageName.replace(".", "/")+"/Robot.java", "templates/sample/Robot.java");
			return files;
		}
	};
	static ProjectType ITERATIVE = new JavaProjectType() {
		@Override public Map<String, String> getFiles(String packageName) {
			Map<String, String> files = super.getFiles(packageName);
			files.put("src/"+packageName.replace(".", "/")+"/Robot.java", "templates/iterative/Robot.java");
			return files;
		}
	};
	static ProjectType TIMED = new JavaProjectType() {
		@Override public Map<String, String> getFiles(String packageName) {
			Map<String, String> files = super.getFiles(packageName);
			files.put("src/"+packageName.replace(".", "/")+"/Robot.java", "templates/timed/Robot.java");
			return files;
		}
	};
	static ProjectType COMMAND_BASED = new JavaProjectType() {
		@Override public String[] getFolders(String packageName) {
			String[] paths = {"src/"+packageName.replace(".", "/"),
					"src/"+packageName.replace(".", "/")+"/commands",
					"src/"+packageName.replace(".", "/")+"/subsystems",
					"src/"+packageName.replace(".", "/")+"/triggers"};
			return paths;
		}
		@Override public Map<String, String> getFiles(String packageName) {
			Map<String, String> files = super.getFiles(packageName);
			files.put("src/"+packageName.replace(".", "/")+"/Robot.java", "templates/commandbased/Robot.java");
			files.put("src/"+packageName.replace(".", "/")+"/RobotMap.java", "templates/commandbased/RobotMap.java");
			files.put("src/"+packageName.replace(".", "/")+"/OI.java", "templates/commandbased/OI.java");
			files.put("src/"+packageName.replace(".", "/")+"/commands/ExampleCommand.java", "templates/commandbased/commands/ExampleCommand.java");
			files.put("src/"+packageName.replace(".", "/")+"/subsystems/ExampleSubsystem.java", "templates/commandbased/subsystems/ExampleSubsystem.java");
			return files;
		}
	};
	@SuppressWarnings("serial")
	static Map<String, ProjectType> TYPES = new HashMap<String, ProjectType>() {{
		put(ProjectType.SAMPLE, SAMPLE);
		put(ProjectType.ITERATIVE, ITERATIVE);
		put(ProjectType.COMMAND_BASED, COMMAND_BASED);
		put(ProjectType.TIMED, TIMED);
	}};

	@Override
	public String[] getFolders(String packageName) {
		String[] paths = {"src/"+packageName.replace(".", "/")};
		return paths;
	}

	@Override
	public Map<String, String> getFiles(String packageName) {
		HashMap<String, String> files = new HashMap<String, String>();
		files.put("build.xml", "build.xml");
		files.put("build.properties", "build.properties");
		files.put(".classpath", ".classpath");
		return files;
	}

	@Override
	public URL getBaseURL() {
		return WPILibJavaPlugin.getDefault().getBundle().getEntry("/resources/templates/");
	}
}
